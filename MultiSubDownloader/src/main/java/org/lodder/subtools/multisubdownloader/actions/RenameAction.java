package org.lodder.subtools.multisubdownloader.actions;

import java.io.File;
import java.io.IOException;

import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.lib.library.PathLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.DetectLanguage;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.util.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenameAction {

  private LibrarySettings librarySettings;
  private Manager manager;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(RenameAction.class);

  public RenameAction(LibrarySettings librarySettings, Manager manager) {
    this.librarySettings = librarySettings;
    this.manager =manager;
  }

  public void rename(File f, Release release) {
    String filename = "";

    switch (librarySettings.getLibraryAction()) {
      case MOVE:
        filename = f.getName();
        break;
      case MOVEANDRENAME:
        filename = getNewFilename(f, release);
        break;
      case NOTHING:
        filename = f.getName();
        break;
      case RENAME:
        filename = getNewFilename(f, release);
        break;
      default:
        break;
    }
    LOGGER.trace("rename: filename [{}]", filename);

    PathLibraryBuilder pathLibraryBuilder = new PathLibraryBuilder(librarySettings, manager);
    final File newDir = new File(pathLibraryBuilder.build(release));
    boolean status = true;
    if (!newDir.exists()) {
      LOGGER.debug("Creating dir [{}]", newDir.getAbsolutePath());
      status = newDir.mkdirs();
    }

    LOGGER.trace("rename: newDir [{}]", newDir);

    if (status) {
      final File file = new File(release.getPath(), release.getFilename());

      try {

        if (librarySettings.getLibraryAction().equals(LibraryActionType.MOVE)
            || librarySettings.getLibraryAction().equals(LibraryActionType.MOVEANDRENAME)) {
          LOGGER.info("Moving [{}] to the library folder [{}] , this might take a while... ", filename, newDir);
          Files.move(file, new File(newDir, filename));
        } else {
          LOGGER.info("Moving [{}] to the library folder [{}] , this might take a while... ", filename, release.getPath());
          Files.move(file, new File(release.getPath(), filename));
        }
        if (!librarySettings.getLibraryOtherFileAction().equals(LibraryOtherFileActionType.NOTHING)) {
          CleanAction cleanAction = new CleanAction(librarySettings);
          cleanAction.cleanUpFiles(release, newDir, filename);
        }
        File[] listFiles = release.getPath().listFiles();
        if (librarySettings.isLibraryRemoveEmptyFolders() && listFiles != null
            && listFiles.length == 0) {
          boolean isDeleted = release.getPath().delete();
          if (isDeleted){
            //do nothing
          }
        }
      } catch (IOException e) {
        LOGGER.error("Unsuccessfull in moving the file to the libary", e);
      }

    }
  }

  private String getNewFilename(File f, Release release) {
    String filename = "";
    FilenameLibraryBuilder filenameLibraryBuilder = new FilenameLibraryBuilder(librarySettings, manager);
    filename = filenameLibraryBuilder.build(release);
    if (release.getExtension().equals("srt")) {
      String languageCode = "";
      try {
        if (librarySettings.isLibraryIncludeLanguageCode()) {
          languageCode = DetectLanguage.execute(f);
        }
      } catch (final Exception e) {
        LOGGER.error("Unable to detect language, leaving language code blank", e);
      }

      filename = filenameLibraryBuilder.buildSubtitle(release, filename, languageCode, 0);
    }
    return filename;
  }
}
