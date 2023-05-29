package org.lodder.subtools.multisubdownloader.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.listeners.IndexingProgressListener;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.util.FileUtils;
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
    private final static String subtitleExtension = "srt";

    private static final Logger LOGGER = LoggerFactory.getLogger(FileListAction.class);

    public FileListAction(Settings settings) {
        this.settings = settings;
    }

    public List<Path> getFileListing(Path dir, boolean recursive, Language language, boolean forceSubtitleOverwrite) {
        LOGGER.trace("getFileListing: dir [{}] Recursive [{}] languageCode [{}] forceSubtitleOverwrite [{}]", dir, recursive, language,
                forceSubtitleOverwrite);
        /* Reset progress counters */
        this.progressFileIndex = 0;
        this.progressFilesTotal = 0;

        /* Start listing process */
        return this._getFileListing(dir, recursive, language, forceSubtitleOverwrite);
    }

    private List<Path> _getFileListing(Path dir, boolean recursive, Language language, boolean forceSubtitleOverwrite) {
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

        if (this.indexingProgressListener != null) {
            this.indexingProgressListener.progress(dir.toString());
        }

        for (Path file : contents) {
            /* Increase progressFileIndex */
            this.progressFileIndex++;

            /* Update progressListener */
            if (this.indexingProgressListener != null) {
                /* Tell the progress listener the overall progress */
                int progress = (int) Math.floor((float) this.progressFileIndex / this.progressFilesTotal * 100);
                this.indexingProgressListener.progress(progress);
            }

            try {
                if (file.isRegularFile()) {
                    if (isValidVideoFile(file) && (forceSubtitleOverwrite || !fileHasSubtitles(file, language)) && !isExcludedFile(file)) {
                        filelist.add(file);
                    }
                } else if (recursive && !isExcludedDir(file)) {
                    filelist.addAll(getFileListing(file, recursive, language, forceSubtitleOverwrite));
                    if (this.indexingProgressListener != null) {
                        this.indexingProgressListener.progress(dir.toString());
                    }
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return filelist;
    }

    private boolean isExcludedDir(Path path) {
        boolean excludedDir = settings.getExcludeList().stream().anyMatch(item -> item.isExcludedPath(path));
        if (excludedDir) {
            LOGGER.trace("isExcludedDir, skipping [{}]", path);
        }
        return excludedDir;
    }

    private boolean isExcludedFile(Path path) {
        boolean excludedFile = settings.getExcludeList().stream().anyMatch(item -> item.isExcludedPath(path));
        if (excludedFile) {
            LOGGER.trace("isExcludedFile, skipping [{}]", path);
        }
        return excludedFile;
    }

    public boolean isValidVideoFile(Path file) {
        return VideoPatterns.EXTENSIONS.contains(file.getExtension()) && !file.getFileNameAsString().contains("sample");
    }

    public boolean fileHasSubtitles(Path file, Language language) throws IOException {
        String extension = file.getExtension();
        Optional<String> subtitleNameOptional = VideoPatterns.EXTENSIONS.stream()
                .filter(extension::equals)
                .map(x -> file.changeExtension(subtitleExtension))
                .findAny();

        if (subtitleNameOptional.isEmpty()) {
            return false;
        }
        String subtitleName = subtitleNameOptional.get();
        Path f = file.resolveSibling(subtitleName);
        if (f.exists()) {
            return true;
        } else {
            String subtitleExtensionWithDot = "." + subtitleExtension;
            List<String> filters = switch (language) {
                case DUTCH -> {
                    List<String> dutchFilterList = getFilters(dutchFilters);
                    if (!StringUtils.isBlank(settings.getEpisodeLibrarySettings().getDefaultNlText())) {
                        dutchFilterList.add("." + settings.getEpisodeLibrarySettings().getDefaultNlText() + subtitleExtensionWithDot);
                    }
                    yield dutchFilterList;
                }
                case ENGLISH -> {
                    List<String> englishFilterList = getFilters(englishFilters);
                    if (!StringUtils.isBlank(settings.getEpisodeLibrarySettings().getDefaultEnText())) {
                        englishFilterList.add("." + settings.getEpisodeLibrarySettings().getDefaultEnText() + subtitleExtensionWithDot);
                    }
                    yield englishFilterList;
                }
                default -> {
                    // TODO implement others
                    yield List.of();
                }
            };
            String subtitleNameWithoutExtension = subtitleName.replace(subtitleExtensionWithDot, "");
            return file.getParent().list().map(FileUtils::getFileNameAsString).filter(fileName -> filters.stream().anyMatch(fileName::endsWith))
                    .anyMatch(fileName -> fileName.contains(subtitleNameWithoutExtension));
        }
    }

    private List<String> getFilters(Set<String> words) {
        return words.stream().map(word -> word + "." + subtitleExtension).toList();
    }

    public void setIndexingProgressListener(IndexingProgressListener indexingProgressListener) {
        this.indexingProgressListener = indexingProgressListener;
    }
}
