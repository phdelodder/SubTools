package org.lodder.subtools.multisubdownloader.actions;

import java.io.File;
import java.io.IOException;

import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.lib.library.PathLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.DetectLanguage;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RenameAction {

    private final LibrarySettings librarySettings;
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(RenameAction.class);

    public void rename(File f, Release release) {
        String filename = switch (librarySettings.getLibraryAction()) {
            case MOVE, NOTHING -> f.getName();
            case MOVEANDRENAME -> getNewFilename(f, release);
            case RENAME -> getNewFilename(f, release);
            default -> "";
        };
        LOGGER.trace("rename: filename [{}]", filename);

        PathLibraryBuilder pathLibraryBuilder = new PathLibraryBuilder(librarySettings, manager, userInteractionHandler);
        final File newDir = new File(pathLibraryBuilder.build(release));
        boolean status = true;
        if (!newDir.exists()) {
            LOGGER.debug("Creating dir [{}]", newDir.getAbsolutePath());
            status = newDir.mkdirs();
        }

        LOGGER.trace("rename: newDir [{}]", newDir);

        if (status) {
            final File file = new File(release.getPath(), release.getFileName());

            try {

                if (LibraryActionType.MOVE.equals(librarySettings.getLibraryAction())
                        || LibraryActionType.MOVEANDRENAME.equals(librarySettings.getLibraryAction())) {
                    LOGGER.info("Moving [{}] to the library folder [{}] , this might take a while... ", filename, newDir);
                    Files.move(file, new File(newDir, filename));
                } else {
                    LOGGER.info("Moving [{}] to the library folder [{}] , this might take a while... ", filename, release.getPath());
                    Files.move(file, new File(release.getPath(), filename));
                }
                if (!LibraryOtherFileActionType.NOTHING.equals(librarySettings.getLibraryOtherFileAction())) {
                    CleanAction cleanAction = new CleanAction(librarySettings);
                    cleanAction.cleanUpFiles(release, newDir, filename);
                }
                File[] listFiles = release.getPath().listFiles();
                if (librarySettings.isLibraryRemoveEmptyFolders() && listFiles != null && listFiles.length == 0) {
                    boolean isDeleted = release.getPath().delete();
                    if (isDeleted) {
                        // do nothing
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Unsuccessfull in moving the file to the libary", e);
            }

        }
    }

    private String getNewFilename(File f, Release release) {
        FilenameLibraryBuilder filenameLibraryBuilder = new FilenameLibraryBuilder(librarySettings, manager, userInteractionHandler);
        String filename = filenameLibraryBuilder.build(release);
        if ("srt".equals(release.getExtension())) {
            Language language = null;
            if (librarySettings.isLibraryIncludeLanguageCode()) {
                language = DetectLanguage.execute(f);
                if (language == null) {
                    LOGGER.error("Unable to detect language, leaving language code blank");
                }
            }
            filename = filenameLibraryBuilder.buildSubtitle(release, filename, language, 0);
        }
        return filename;
    }
}
