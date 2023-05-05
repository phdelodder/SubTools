package org.lodder.subtools.multisubdownloader.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.listeners.IndexingProgressListener;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.util.FileUtils;
import org.lodder.subtools.sublibrary.util.NamedMatcher;
import org.lodder.subtools.sublibrary.util.NamedPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ Files.class, FileUtils.class })
public class FileListAction {

    private IndexingProgressListener indexingProgressListener;
    private int progressFileIndex;
    private int progressFilesTotal;
    private final Settings settings;
    private final Set<String> dutchFilters = Set.of("nld", "ned", "dutch", "dut", "nl");
    private final Set<String> englishFilters = Set.of("eng", "english", "en");
    private final static String subtitleExtension = ".srt";

    private static final Logger LOGGER = LoggerFactory.getLogger(FileListAction.class);

    public FileListAction(Settings settings) {
        this.settings = settings;
    }

    public List<Path> getFileListing(Path dir, boolean recursieve, Language language, boolean forceSubtitleOverwrite) {
        LOGGER.trace("getFileListing: dir [{}] Recursive [{}] languageCode [{}] forceSubtitleOverwrite [{}]", dir, recursieve, language,
                forceSubtitleOverwrite);
        /* Reset progress counters */
        this.progressFileIndex = 0;
        this.progressFilesTotal = 0;

        /* Start listing process */
        return this._getFileListing(dir, recursieve, language, forceSubtitleOverwrite);
    }

    private List<Path> _getFileListing(Path dir, boolean recursieve, Language language, boolean forceSubtitleOverwrite) {
        final List<Path> filelist = new ArrayList<>();
        List<Path> contents;
        try {
            contents = dir.list().toList();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return List.of();
        }

        /* Increase progressTotalFiles count */
        this.progressFilesTotal += contents.size();

        for (Path file : contents) {
            /* Increase progressFileIndex */
            this.progressFileIndex++;

            /* Update progressListener */
            if (this.indexingProgressListener != null) {
                /* Tell the progresslistener which directory we are handling */
                this.indexingProgressListener.progress(dir.toString());
                /* Tell the progresslistener the overall progress */
                int progress = (int) Math.floor((float) this.progressFileIndex / this.progressFilesTotal * 100);
                this.indexingProgressListener.progress(progress);
            }

            try {
                if (file.isRegularFile() && isValidVideoFile(file) && (!fileHasSubtitles(file, language) || forceSubtitleOverwrite)
                        && isNotExcluded(file)) {
                    filelist.add(file);
                } else if (recursieve && file.isDirectory() && !isExcludedDir(file)) {
                    filelist.addAll(getFileListing(file, recursieve, language, forceSubtitleOverwrite));
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return filelist;
    }

    private Boolean isExcludedDir(Path file) {
        boolean excludedDir = settings.getExcludeList().stream()
                .anyMatch(item -> item.getType() == SettingsExcludeType.FOLDER && Path.of(item.getDescription()).equals(file));
        if (excludedDir) {
            LOGGER.trace("isExcludedDir, skipping [{}]", file);
        }
        return excludedDir;
    }

    private boolean isNotExcluded(Path file) {
        boolean notExcluded = settings.getExcludeList().stream().noneMatch(element -> {
            if (element.getType() == SettingsExcludeType.REGEX) {
                NamedPattern np = NamedPattern.compile(element.getDescription().replace("*", ".*") + ".*$", Pattern.CASE_INSENSITIVE);
                NamedMatcher namedMatcher = np.matcher(file.getFileName().toString());
                return namedMatcher.find();
            } else if (element.getType() == SettingsExcludeType.FILE) {
                Path excludeFile = Path.of(element.getDescription());
                return excludeFile.equals(file);
            }
            return false;
        });
        if (notExcluded) {
            LOGGER.trace("isNotExcluded, skipping [{}]", file);
        }
        return notExcluded;
    }

    public boolean isValidVideoFile(Path file) {
        String filename = file.getFileName().toString();
        int mid = filename.lastIndexOf(".");
        String ext = filename.substring(mid + 1);
        if (filename.contains("sample")) {
            return false;
        }
        return Arrays.asList(VideoPatterns.EXTENSIONS).contains(ext);
    }

    public boolean fileHasSubtitles(Path file, Language language) throws IOException {
        String subname = Arrays.stream(VideoPatterns.EXTENSIONS)
                .filter(allowedExtension -> file.getFileName().toString().contains("." + allowedExtension))
                .map(allowedExtension -> file.getFileName().toString().replace("." + allowedExtension, subtitleExtension))
                .findAny().orElse("");

        Path f = file.resolveSibling(subname);
        if (f.exists()) {
            return true;
        } else {
            List<String> filters = switch (language) {
                case DUTCH -> {
                    List<String> dutchFilterList = getFilters(dutchFilters);
                    if (!StringUtils.isBlank(settings.getEpisodeLibrarySettings().getDefaultNlText())) {
                        dutchFilterList.add("." + settings.getEpisodeLibrarySettings().getDefaultNlText().concat(subtitleExtension));
                    }
                    yield dutchFilterList;
                }
                case ENGLISH -> {
                    List<String> englishFilterList = getFilters(englishFilters);
                    if (!StringUtils.isBlank(settings.getEpisodeLibrarySettings().getDefaultEnText())) {
                        englishFilterList.add("." + settings.getEpisodeLibrarySettings().getDefaultEnText().concat(subtitleExtension));
                    }
                    yield englishFilterList;
                }
                default -> {
                    // TODO implement others
                    yield List.of();
                }
            };

            return file.getParent().list().filter(p -> filters.contains(p.getExtension()))
                    .anyMatch(p -> p.getFileNameAsString().contains(subname.replace(subtitleExtension, "")));
        }
    }

    private List<String> getFilters(Set<String> words) {
        return words.stream().map(word -> word + subtitleExtension).toList();
    }

    public void setIndexingProgressListener(IndexingProgressListener indexingProgressListener) {
        this.indexingProgressListener = indexingProgressListener;
    }
}
