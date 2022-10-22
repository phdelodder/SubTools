package org.lodder.subtools.multisubdownloader.actions;

import java.io.File;
import java.io.IOException;

import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.lib.library.PathLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.privateRepo.PrivateRepoIndex;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.http.DropBoxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

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
        LOGGER.trace("cleanUpFiles: LibraryAction", librarySettings.getLibraryAction());
        PathLibraryBuilder pathLibraryBuilder = new PathLibraryBuilder(librarySettings, manager, userInteractionHandler);
        final File path = new File(pathLibraryBuilder.build(release));
        if (!path.exists()) {
            LOGGER.debug("Download creating folder [{}] ", path.getAbsolutePath());
            if (!path.mkdirs()) {
                throw new IOException("Download unable to create folder: " + path.getAbsolutePath());
            }
        }

        FilenameLibraryBuilder filenameLibraryBuilder = new FilenameLibraryBuilder(librarySettings, manager, userInteractionHandler);
        final String videoFileName = filenameLibraryBuilder.build(release);
        final String subFileName = filenameLibraryBuilder.buildSubtitle(release, subtitle, videoFileName, version);
        final File subFile = new File(path, subFileName);

        boolean success;
        if (subtitle.getSourceLocation() == Subtitle.SourceLocation.FILE) {
            Files.copy(subtitle.getFile(), subFile);
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

        if (ReleaseParser.getQualityKeyword(release.getFileName()).split(" ").length > 1) {
            String dropBoxName = "";
            if (subtitle.getSubtitleSource() == SubtitleSource.LOCAL) {
                dropBoxName = PrivateRepoIndex.getFullFilename(FilenameLibraryBuilder.changeExtension(release.getFileName(), ".srt"), "?",
                        subtitle.getSubtitleSource().toString());
            } else {
                dropBoxName = PrivateRepoIndex.getFullFilename(FilenameLibraryBuilder.changeExtension(release.getFileName(), ".srt"),
                        subtitle.getUploader(), subtitle.getSubtitleSource().toString());
            }
            DropBoxClient.getDropBoxClient().put(subFile, dropBoxName, subtitle.getLanguage());
        }

        if (success) {
            if (!LibraryActionType.NOTHING.equals(librarySettings.getLibraryAction())) {
                final File oldLocationFile = new File(release.getPath(), release.getFileName());
                if (oldLocationFile.exists()) {
                    final File newLocationFile = new File(path, videoFileName);
                    LOGGER.info("Moving/Renaming [{}] to folder [{}] this might take a while... ", videoFileName, path.getPath());
                    Files.move(oldLocationFile, newLocationFile);
                    if (!LibraryOtherFileActionType.NOTHING.equals(librarySettings.getLibraryOtherFileAction())) {
                        CleanAction cleanAction = new CleanAction(librarySettings);
                        cleanAction.cleanUpFiles(release, path, videoFileName);
                    }
                    File[] listFiles = release.getPath().listFiles();
                    if (librarySettings.isLibraryRemoveEmptyFolders() && listFiles != null && listFiles.length == 0) {
                        boolean isDeleted = release.getPath().delete();
                        if (isDeleted) {
                            // do nothing
                        }
                    }
                }
            }
            if (librarySettings.isLibraryBackupSubtitle()) {
                String langFolder = subtitle.getLanguage() == null ? Language.ENGLISH.getName() : subtitle.getLanguage().getName();
                File backupPath = new File(librarySettings.getLibraryBackupSubtitlePath() + File.separator + langFolder + File.separator);

                if (!backupPath.exists() && !backupPath.mkdirs()) {
                    throw new IOException("Download unable to create folder: " + backupPath.getAbsolutePath());
                }

                if (librarySettings.isLibraryBackupUseWebsiteFileName()) {
                    Files.copy(subFile, new File(backupPath, subtitle.getFileName()));
                } else {
                    Files.copy(subFile, new File(backupPath, subFileName));
                }
            }
        }
    }
}
