package org.lodder.subtools.multisubdownloader.actions;

import java.io.File;

import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.lib.library.PathLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.privateRepo.PrivateRepoIndex;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.http.DropBoxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadAction.class);

    private final Settings settings;
    private final Manager manager;

    /**
     *
     * @param settings
     */
    public DownloadAction(Settings settings, Manager manager) {
        this.settings = settings;
        this.manager = manager;
    }

    /**
     *
     * @param release
     * @param subtitle
     * @param version
     * @throws Exception
     */
    public void download(Release release, Subtitle subtitle, int version) throws Exception {
        if (VideoType.EPISODE.equals(release.getVideoType())) {
            download(release, subtitle, settings.getEpisodeLibrarySettings(), version);
        } else if (VideoType.MOVIE.equals(release.getVideoType())) {
            download(release, subtitle, settings.getMovieLibrarySettings(), version);
        }
    }

    /**
     *
     * @param release
     * @param subtitle
     * @throws Exception
     */
    public void download(Release release, Subtitle subtitle) throws Exception {
        LOGGER.info("Downloading subtitle: [{}] for release: [{}]", subtitle.getFilename(),
                release.getFilename());
        download(release, subtitle, 0);
    }

    /**
     * @param release
     * @param subtitle
     * @param librarySettings
     * @param version
     * @throws Exception
     */

    private void download(Release release, Subtitle subtitle, LibrarySettings librarySettings,
            int version) throws Exception {
        LOGGER.trace("cleanUpFiles: LibraryAction", librarySettings.getLibraryAction());
        PathLibraryBuilder pathLibraryBuilder = new PathLibraryBuilder(librarySettings, manager);
        final File path = new File(pathLibraryBuilder.build(release));
        if (!path.exists()) {
            LOGGER.debug("Download creating folder [{}] ", path.getAbsolutePath());
            if (!path.mkdirs()) {
                throw new Exception("Download unable to create folder: " + path.getAbsolutePath());
            }
        }

        FilenameLibraryBuilder filenameLibraryBuilder =
                new FilenameLibraryBuilder(librarySettings, manager);
        final String videoFileName = filenameLibraryBuilder.build(release);
        final String subFileName =
                filenameLibraryBuilder.buildSubtitle(release, subtitle, videoFileName, version);
        final File subFile = new File(path, subFileName);

        boolean success;
        if (subtitle.getSourceLcation() == Subtitle.SourceLocation.FILE) {
            Files.copy(subtitle.getFile(), subFile);
            success = true;
        } else {
            String url = subtitle.getSourceLcation() == Subtitle.SourceLocation.URL ? subtitle.getUrl() : subtitle.getUrlSupplier().get();
            success = manager.store(url, subFile);
            LOGGER.debug("doDownload file was [{}] ", success);
        }

        if (ReleaseParser.getQualityKeyword(release.getFilename()).split(" ").length > 1) {
            String dropBoxName = "";
            if (subtitle.getSubtitleSource() == SubtitleSource.LOCAL) {
                dropBoxName =
                        PrivateRepoIndex.getFullFilename(
                                FilenameLibraryBuilder.changeExtension(release.getFilename(), ".srt"), "?",
                                subtitle.getSubtitleSource().toString());
            } else {
                dropBoxName =
                        PrivateRepoIndex.getFullFilename(
                                FilenameLibraryBuilder.changeExtension(release.getFilename(), ".srt"),
                                subtitle.getUploader(), subtitle.getSubtitleSource().toString());
            }
            DropBoxClient.getDropBoxClient().put(subFile, dropBoxName, subtitle.getLanguagecode());
        }

        if (success) {
            if (!LibraryActionType.NOTHING.equals(librarySettings.getLibraryAction())) {
                final File oldLocationFile = new File(release.getPath(), release.getFilename());
                if (oldLocationFile.exists()) {
                    final File newLocationFile = new File(path, videoFileName);
                    LOGGER.info("Moving/Renaming [{}] to folder [{}] this might take a while... ", videoFileName, path.getPath());
                    Files.move(oldLocationFile, newLocationFile);
                    if (!LibraryOtherFileActionType.NOTHING.equals(
                            librarySettings.getLibraryOtherFileAction())) {
                        CleanAction cleanAction = new CleanAction(librarySettings);
                        cleanAction.cleanUpFiles(release, path, videoFileName);
                    }
                    File[] listFiles = release.getPath().listFiles();
                    if (librarySettings.isLibraryRemoveEmptyFolders() && listFiles != null
                            && listFiles.length == 0) {
                        boolean isDeleted = release.getPath().delete();
                        if (isDeleted) {
                            // do nothing
                        }
                    }
                }
            }
            if (librarySettings.isLibraryBackupSubtitle()) {
                String langFolder = "";
                if ("nl".equals(subtitle.getLanguagecode())) {
                    langFolder = "Nederlands";
                } else {
                    langFolder = "Engels";
                }
                File backupPath =
                        new File(librarySettings.getLibraryBackupSubtitlePath() + File.separator + langFolder
                                + File.separator);

                if (!backupPath.exists() && !backupPath.mkdirs()) {
                    throw new Exception("Download unable to create folder: " + backupPath.getAbsolutePath());
                }

                if (librarySettings.isLibraryBackupUseWebsiteFileName()) {
                    Files.copy(subFile, new File(backupPath, subtitle.getFilename()));
                } else {
                    Files.copy(subFile, new File(backupPath, subFileName));
                }
            }
        }
    }
}
