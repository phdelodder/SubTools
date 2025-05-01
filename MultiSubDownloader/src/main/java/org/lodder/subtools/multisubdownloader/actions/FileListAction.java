package org.lodder.subtools.multisubdownloader.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import extensions.java.nio.file.Path.PathExt;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import manifold.ext.props.rt.api.set;
import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.listeners.IndexingProgressListener;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@ExtensionMethod({ Files.class })
public class FileListAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileListAction.class);
    private static final String SUBTITLE_EXTENSION = "srt";

    private final Settings settings;
    @set IndexingProgressListener indexingProgressListener;


    public List<Path> getFileListing(Path dir, boolean recursive, Language language, boolean forceSubtitleOverwrite) {
        LOGGER.trace("getFileListing: dir [{}] Recursive [{}] languageCode [{}] forceSubtitleOverwrite [{}]",
            dir, recursive, language, forceSubtitleOverwrite);
        int progressFileIndex = 0;
        int progressFilesTotal = 0;

        /* Start listing process */
        final List<Path> filelist = new ArrayList<>();
        List<Path> contents;
        try {
            contents = dir.list().toList();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return List.of();
        }

        /* Increase progressTotalFiles count */
        progressFilesTotal += contents.size();

        if (this.indexingProgressListener != null) {
            this.indexingProgressListener.progress(dir.toString());
        }

        for (Path file : contents) {
            progressFileIndex++;

            /* Update progressListener */
            if (this.indexingProgressListener != null) {
                /* Tell the progress listener the overall progress */
                int progress = (int) Math.floor((float) progressFileIndex / progressFilesTotal * 100);
                this.indexingProgressListener.progress(progress);
            }

            try {
                if (file.isRegularFile()) {
                    if (isValidVideoFile(file) && (forceSubtitleOverwrite || !fileHasSubtitles(file, language)) &&
                        !isExcludedFile(file)) {
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
        boolean excludedDir = settings.excludeList.stream().anyMatch(item -> item.isExcludedPath(path));
        if (excludedDir) {
            LOGGER.trace("isExcludedDir, skipping [{}]", path);
        }
        return excludedDir;
    }

    private boolean isExcludedFile(Path path) {
        boolean excludedFile = settings.excludeList.stream().anyMatch(item -> item.isExcludedPath(path));
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
            .map(_ -> file.changeExtension(SUBTITLE_EXTENSION))
            .findAny();

        if (subtitleNameOptional.isEmpty()) {
            return false;
        }
        String subtitleName = subtitleNameOptional.get();
        Path f = file.resolveSibling(subtitleName);
        if (f.exists()) {
            return true;
        } else {
            String subtitleExtensionWithDot = "." + SUBTITLE_EXTENSION;

            Set<String> langCodes = new HashSet<>();
            langCodes.add(language.langCode);
            langCodes.addAll(language.langCodesOther);
            String customLangCode = settings.episodeLibrarySettings.langCodeMap.get(language);
            if (!StringUtils.isBlank(customLangCode)) {
                langCodes.add(customLangCode);
            }
            List<String> filters = langCodes.stream().map(word -> word + "." + SUBTITLE_EXTENSION).toList();
            String subtitleNameWithoutExtension = subtitleName.replace(subtitleExtensionWithDot, "");
            return file.getParent()
                .list()
                .map(PathExt::getFileNameAsString)
                .filter(fileName -> filters.stream().anyMatch(fileName::endsWith))
                .anyMatch(fileName -> fileName.contains(subtitleNameWithoutExtension));
        }
    }
}
