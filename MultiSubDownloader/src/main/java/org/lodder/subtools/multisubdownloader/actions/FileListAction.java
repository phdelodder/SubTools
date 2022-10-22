package org.lodder.subtools.multisubdownloader.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.listeners.IndexingProgressListener;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.util.FilenameExtensionFilter;
import org.lodder.subtools.sublibrary.util.NamedMatcher;
import org.lodder.subtools.sublibrary.util.NamedPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileListAction {

    private IndexingProgressListener indexingProgressListener;
    private int progressFileIndex;
    private int progressFilesTotal;
    private final Settings settings;
    private final String[] dutchFilters = { "nld", "ned", "dutch", "dut", "nl" };
    private final String[] englishFilters = { "eng", "english", "en" };
    private final static String subtitleExtension = ".srt";

    private static final Logger LOGGER = LoggerFactory.getLogger(FileListAction.class);

    public FileListAction(Settings settings) {
        this.settings = settings;
    }

    public List<File> getFileListing(File dir, boolean recursieve, Language language, boolean forceSubtitleOverwrite) {
        LOGGER.trace("getFileListing: dir [{}] Recursive [{}] languageCode [{}] forceSubtitleOverwrite [{}]", dir, recursieve, language,
                forceSubtitleOverwrite);
        /* Reset progress counters */
        this.progressFileIndex = 0;
        this.progressFilesTotal = 0;

        /* Start listing process */
        return this._getFileListing(dir, recursieve, language, forceSubtitleOverwrite);
    }

    private List<File> _getFileListing(File dir, boolean recursieve, Language language, boolean forceSubtitleOverwrite) {
        final List<File> filelist = new ArrayList<>();
        final File[] contents = dir.listFiles();

        if (contents == null) {
            return filelist;
        }

        /* Increase progressTotalFiles count */
        this.progressFilesTotal += contents.length;

        for (final File file : contents) {
            /* Increase progressFileIndex */
            this.progressFileIndex++;

            /* Update progressListener */
            if (this.indexingProgressListener != null) {
                /* Tell the progresslistener which directory we are handling */
                this.indexingProgressListener.progress(dir.getPath());
                /* Tell the progresslistener the overall progress */
                int progress = (int) Math.floor((float) this.progressFileIndex / this.progressFilesTotal * 100);
                this.indexingProgressListener.progress(progress);
            }

            if (file.isFile() && isValidVideoFile(file) && (!fileHasSubtitles(file, language) || forceSubtitleOverwrite)
                    && isNotExcluded(file)) {
                filelist.add(file);
            } else if (file.isDirectory() && recursieve && !isExcludedDir(file)) {
                filelist.addAll(getFileListing(file, recursieve, language, forceSubtitleOverwrite));
            }
        }
        return filelist;
    }

    private Boolean isExcludedDir(File file) {
        boolean excludedDir = settings.getExcludeList().stream()
                .anyMatch(item -> item.getType() == SettingsExcludeType.FOLDER && new File(item.getDescription()).equals(file));
        if (excludedDir) {
            LOGGER.trace("isExcludedDir, skipping [{}]", file);
        }
        return excludedDir;
    }

    private boolean isNotExcluded(File file) {
        boolean notExcluded = settings.getExcludeList().stream().noneMatch(element -> {
            if (element.getType() == SettingsExcludeType.REGEX) {
                NamedPattern np = NamedPattern.compile(element.getDescription().replace("*", ".*") + ".*$", Pattern.CASE_INSENSITIVE);
                NamedMatcher namedMatcher = np.matcher(file.getName());
                return namedMatcher.find();
            } else if (element.getType() == SettingsExcludeType.FILE) {
                File excludeFile = new File(element.getDescription());
                return excludeFile.equals(file);
            }
            return false;
        });
        if (notExcluded) {
            LOGGER.trace("isNotExcluded, skipping [{}]", file);
        }
        return notExcluded;
    }

    public boolean isValidVideoFile(File file) {
        final String filename = file.getName();
        final int mid = filename.lastIndexOf(".");
        final String ext = filename.substring(mid + 1);
        if (filename.contains("sample")) {
            return false;
        }
        return Arrays.stream(VideoPatterns.EXTENSIONS).anyMatch(ext::equals);
    }

    public boolean fileHasSubtitles(File file, Language language) {
        String subname = Arrays.stream(VideoPatterns.EXTENSIONS)
                .filter(allowedExtension -> file.getName().contains("." + allowedExtension))
                .map(allowedExtension -> file.getName().replace("." + allowedExtension, subtitleExtension))
                .findAny().orElse("");

        final File f = new File(file.getParentFile(), subname);
        if (f.exists()) {
            return true;
        } else {
            List<String> filters = null;

            switch (language) {
                case DUTCH -> {
                    filters = getFilters(dutchFilters);
                    if (!StringUtils.isBlank(settings.getEpisodeLibrarySettings().getDefaultNlText())) {
                        filters.add("." + settings.getEpisodeLibrarySettings().getDefaultNlText().concat(subtitleExtension));
                    }
                }
                case ENGLISH -> {
                    filters = getFilters(englishFilters);
                    if (!StringUtils.isBlank(settings.getEpisodeLibrarySettings().getDefaultEnText())) {
                        filters.add("." + settings.getEpisodeLibrarySettings().getDefaultEnText().concat(subtitleExtension));
                    }
                }
                default -> {
                    // TODO implement others
                }
            }

            if (filters == null) {
                return false;
            }

            String[] contents = file.getParentFile().list(new FilenameExtensionFilter(filters.toArray(new String[filters.size()])));
            if (contents == null) {
                contents = new String[] {};
            }

            return checkFileListContent(contents, subname.replace(subtitleExtension, ""));
        }
    }

    private List<String> getFilters(String[] words) {
        return Arrays.stream(words).map(word -> word + subtitleExtension).collect(Collectors.toList());
    }

    public boolean checkFileListContent(String[] contents, String subname) {
        return contents.length > 0 && Arrays.stream(contents).anyMatch(file -> file.contains(subname));
    }

    public void setIndexingProgressListener(IndexingProgressListener indexingProgressListener) {
        this.indexingProgressListener = indexingProgressListener;
    }
}
