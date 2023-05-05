package org.lodder.subtools.multisubdownloader.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
import org.lodder.subtools.sublibrary.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ FileUtils.class, Files.class })
@RequiredArgsConstructor
public class DownloadAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadAction.class);

    private final Settings settings;
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

    public void download(Release release, Subtitle subtitle, Integer version) throws IOException, ManagerException {
        switch (release.getVideoType()) {
            case EPISODE -> download(release, subtitle, settings.getEpisodeLibrarySettings(), version);
            case MOVIE -> download(release, subtitle, settings.getMovieLibrarySettings(), version);
            default -> throw new IllegalArgumentException("Unexpected value: " + release.getVideoType());
        }
    }

    public void download(Release release, Subtitle subtitle) throws IOException, ManagerException {
        LOGGER.info("Downloading subtitle: [{}] for release: [{}]", subtitle.getFileName(), release.getFileName());
        download(release, subtitle, 0);
    }

    private void download(Release release, Subtitle subtitle, LibrarySettings librarySettings, Integer version)
            throws IOException, ManagerException {
        LOGGER.trace("cleanUpFiles: LibraryAction {}", librarySettings.getLibraryAction());
        PathLibraryBuilder pathLibraryBuilder = new PathLibraryBuilder(librarySettings, manager, userInteractionHandler);
        Path path = pathLibraryBuilder.build(release);
        if (!path.exists()) {
            LOGGER.debug("Download creating folder [{}] ", path.toAbsolutePath());
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new IOException("Download unable to create folder: " + path.toAbsolutePath(), e);
            }
        }

        FilenameLibraryBuilder filenameLibraryBuilder = new FilenameLibraryBuilder(librarySettings, manager, userInteractionHandler);
        String videoFileName = filenameLibraryBuilder.build(release).toString();
        String subFileName = filenameLibraryBuilder.buildSubtitle(release, subtitle, videoFileName, version);
        Path subFile = path.resolve(subFileName);

        boolean success;
        if (subtitle.getSourceLocation() == Subtitle.SourceLocation.FILE) {
            subtitle.getFile().copyToDir(path);
            success = true;
        } else {
            String url;
            try {
                url = subtitle.getSourceLocation() == Subtitle.SourceLocation.URL ? subtitle.getUrl() : subtitle.getUrlSupplier().get();
                success = manager.store(url, subFile);
                LOGGER.debug("doDownload file was [{}] ", success);
            } catch (SubtitlesProviderException e) {
                LOGGER.error("Error while getting url for [%s] for subtitle provider [%s] (%s)".formatted(release.getReleaseDescription(),
                        e.getSubtitleProvider(), e.getMessage()), e);
                throw new RuntimeException(e);
            }
        }

        if (success) {
            if (!librarySettings.hasLibraryAction(LibraryActionType.NOTHING)) {
                Path oldLocationFile = release.getPath().resolve(release.getFileName());
                if (oldLocationFile.exists()) {
                    LOGGER.info("Moving/Renaming [{}] to folder [{}] this might take a while... ", videoFileName, path);
                    oldLocationFile.moveToDir(path);
                    if (!librarySettings.hasLibraryOtherFileAction(LibraryOtherFileActionType.NOTHING)) {
                        CleanAction cleanAction = new CleanAction(librarySettings);
                        cleanAction.cleanUpFiles(release, path, videoFileName);
                    }
                    if (librarySettings.isLibraryRemoveEmptyFolders() && release.getPath().isEmptyDir()) {
                        FileUtils.delete(release.getPath());
                    }
                }
            }
            if (librarySettings.isLibraryBackupSubtitle()) {
                String langFolder = subtitle.getLanguage() == null ? Language.ENGLISH.getName() : subtitle.getLanguage().getName();
                Path backupPath = librarySettings.getLibraryBackupSubtitlePath().resolve(langFolder);

                if (!backupPath.exists()) {
                    try {
                        Files.createDirectories(backupPath);
                    } catch (IOException e) {
                        throw new IOException("Download unable to create folder: " + backupPath.toAbsolutePath(), e);
                    }
                }

                if (librarySettings.isLibraryBackupUseWebsiteFileName()) {
                    subFile.copyToDirAndRename(backupPath, subtitle.getFileName());
                } else {
                    subFile.copyToDirAndRename(backupPath, subFileName);
                }
            }
        }
    }
}
