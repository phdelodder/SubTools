package org.lodder.subtools.multisubdownloader.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.lib.library.PathLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtensionMethod({ Files.class })
@RequiredArgsConstructor
public class DownloadAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadAction.class);

    private final Settings settings;
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

    public void download(Release release, Subtitle subtitle, Integer version=0) throws IOException,
        ManagerException {
        LOGGER.info("Downloading subtitle: [{}] for release: [{}]", subtitle.fileName, release.fileName);
        switch (release.videoType) {
            case EPISODE -> download(release, subtitle, settings.episodeLibrarySettings, version);
            case MOVIE -> download(release, subtitle, settings.movieLibrarySettings, version);
            default -> throw new IllegalArgumentException("Unexpected value: " + release.videoType);
        }
    }

    private void download(Release release, Subtitle subtitle, LibrarySettings librarySettings,
        @Nullable Integer version)
        throws IOException, ManagerException {
        LOGGER.trace("cleanUpFiles: LibraryAction {}", librarySettings.action);
        Path path = PathLibraryBuilder.fromSettings(librarySettings, manager, userInteractionHandler).build(release);
        if (!path.exists()) {
            LOGGER.debug("Download creating folder [{}] ", path.toAbsolutePath());
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new IOException("Download unable to create folder: " + path.toAbsolutePath(), e);
            }
        }

        FilenameLibraryBuilder filenameLibraryBuilder =
            FilenameLibraryBuilder.fromSettings(librarySettings, manager, userInteractionHandler);
        String videoFileName = filenameLibraryBuilder.build(release).toString();
        String subFileName = filenameLibraryBuilder.buildSubtitle(release, subtitle, videoFileName, version);
        Path subFile = path.resolve(subFileName);

        boolean success = switch (subtitle.downloadSource.sourceLocation) {
            case FILE -> {
                subtitle.downloadSource.file.copyToDir(path);
                yield true;
            }
            case URL, URL_SUPPLIER -> {
                try {
                    String url = subtitle.downloadSource.getValue();
                    boolean result = manager.store(url, subFile);
                    LOGGER.debug("doDownload file was [{}] ", result);
                    yield result;
                } catch (SubtitlesProviderException e) {
                    LOGGER.error("Error while getting url for [${release.releaseDescription}] " +
                        "for subtitle provider [${e.subtitleProvider}] (${e.getMessage()})", e);
                    throw new RuntimeException(e);
                }
            }
        };

        if (success) {
            if (!librarySettings.hasLibraryAction(LibraryActionType.NOTHING)) {
                Path oldLocationFile = release.getPath().resolve(release.fileName);
                if (oldLocationFile.exists()) {
                    LOGGER.info("Moving/Renaming [{}] to folder [{}] this might take a while... ", videoFileName, path);
                    oldLocationFile.moveToDir(path);
                    if (!librarySettings.hasLibraryOtherFileAction(LibraryOtherFileActionType.NOTHING)) {
                        CleanAction cleanAction = new CleanAction(librarySettings);
                        cleanAction.cleanUpFiles(release, path, videoFileName);
                    }
                    if (librarySettings.removeEmptyFolders && release.path.isEmptyDir()) {
                        release.path.deletePath();
                    }
                }
            }
            if (librarySettings.backupSubtitle) {
                String langFolder =
                    subtitle.language == null ? Language.ENGLISH.getName() : subtitle.language.getName();
                Path backupPath = librarySettings.backupSubtitlePath.resolve(langFolder);

                if (!backupPath.exists()) {
                    try {
                        Files.createDirectories(backupPath);
                    } catch (IOException e) {
                        throw new IOException("Download unable to create folder: " + backupPath.toAbsolutePath(), e);
                    }
                }

                if (librarySettings.backupUseWebsiteFileName) {
                    subFile.copyToDirAndRename(backupPath, subtitle.fileName);
                } else {
                    subFile.copyToDirAndRename(backupPath, subFileName);
                }
            }
        }
    }
}
