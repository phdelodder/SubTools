package org.lodder.subtools.multisubdownloader.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
import org.lodder.subtools.sublibrary.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ FileUtils.class, Files.class })
@RequiredArgsConstructor
public class RenameAction {

    private final LibrarySettings librarySettings;
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(RenameAction.class);

    public void rename(Path f, Release release) {
        String filename = switch (librarySettings.getLibraryAction()) {
            case MOVE, NOTHING -> f.getFileNameAsString();
            case MOVEANDRENAME, RENAME -> getNewFilename(f, release);
        };
        LOGGER.trace("rename: filename [{}]", filename);

        
        Path newDir = switch (librarySettings.getLibraryAction()) {
            case MOVE, MOVEANDRENAME -> PathLibraryBuilder.fromSettings(librarySettings, manager, userInteractionHandler).build(release);
            case RENAME, NOTHING -> release.getPath();
        };
        if (!newDir.exists()) {
            LOGGER.debug("Creating dir [{}]", newDir.toAbsolutePath());
            try {
                Files.createDirectories(newDir);
            } catch (IOException e) {
                LOGGER.error("Could not create dir [%s]".formatted(newDir), e);
                return;
            }
        }
        LOGGER.trace("rename: newDir [{}]", newDir);

        Path file = release.getPath().resolve(release.getFileName());

        try {
            if (librarySettings.hasLibraryAction(LibraryActionType.MOVE) || librarySettings.hasLibraryAction(LibraryActionType.MOVEANDRENAME)) {
                LOGGER.info("Moving [{}] to the library folder [{}] , this might take a while... ", filename, newDir);
                file.moveToDirAndRename(newDir, filename);
            } else {
                LOGGER.info("Moving [{}] to the library folder [{}] , this might take a while... ", filename, release.getPath());
                file.moveToDirAndRename(release.getPath(), filename);
            }
            if (!librarySettings.hasLibraryOtherFileAction(LibraryOtherFileActionType.NOTHING)) {
                new CleanAction(librarySettings).cleanUpFiles(release, newDir, filename);
            }

            if (librarySettings.isLibraryRemoveEmptyFolders() && release.getPath().isEmptyDir()) {
                Files.delete(release.getPath());
            }
        } catch (IOException e) {
            LOGGER.error("Unsuccessful in moving the file to the library", e);
        }
    }

    private String getNewFilename(Path f, Release release) {
        FilenameLibraryBuilder filenameLibraryBuilder = FilenameLibraryBuilder.fromSettings(librarySettings, manager, userInteractionHandler);
        String filename = filenameLibraryBuilder.build(release).toString();
        if (release.hasExtension("srt")) {
            Language language = null;
            if (librarySettings.isLibraryIncludeLanguageCode()) {
                language = DetectLanguage.execute(f);
                if (language == null) {
                    LOGGER.error("Unable to detect language, leaving language code blank");
                }
            }
            return filenameLibraryBuilder.buildSubtitle(release, filename, language, 0);
        }
        return filename;
    }
}
