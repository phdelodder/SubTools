package org.lodder.subtools.multisubdownloader.actions;

import java.io.File;
import java.io.IOException;

import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.lib.library.PathLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.DetectLanguage;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.util.Files;

public class RenameAction {

  private LibrarySettings librarySettings;

  public RenameAction(LibrarySettings librarySettings) {
    this.librarySettings = librarySettings;
  }

  @SuppressWarnings("unused")
  public void rename(File f, Release release) {
    Logger.instance
        .trace("Actions", "rename", "LibraryAction" + librarySettings.getLibraryAction());
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
    Logger.instance.trace("Actions", "rename", "filename" + filename);

    PathLibraryBuilder pathLibraryBuilder = new PathLibraryBuilder(librarySettings);
    final File newDir = new File(pathLibraryBuilder.build(release));
    boolean status = true;
    if (!newDir.exists()) {
      Logger.instance.debug("Creating dir: " + newDir.getAbsolutePath());
      status = newDir.mkdirs();
    }

    Logger.instance.trace("Actions", "rename", "newDir" + newDir);

    if (status) {
      final File file = new File(release.getPath(), release.getFilename());

      try {

        if (librarySettings.getLibraryAction().equals(LibraryActionType.MOVE)
            || librarySettings.getLibraryAction().equals(LibraryActionType.MOVEANDRENAME)) {
          Logger.instance.log("Moving " + filename + " to the library folder " + newDir
              + " , this might take a while... ");
          Files.move(file, new File(newDir, filename));
        } else {
          Logger.instance.log("Moving " + filename + " to the library folder " + release.getPath()
              + " , this might take a while... ");
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
        }
      } catch (IOException e) {
        Logger.instance.error("Unsuccessfull in moving the file to the libary");
      }

    }
  }

  private String getNewFilename(File f, Release release) {
    String filename = "";
    FilenameLibraryBuilder filenameLibraryBuilder = new FilenameLibraryBuilder(librarySettings);
    filename = filenameLibraryBuilder.build(release);
    if (release.getExtension().equals("srt")) {
      String languageCode = "";
      try {
        if (librarySettings.isLibraryIncludeLanguageCode()) {
          languageCode = DetectLanguage.execute(f);
        }
      } catch (final Exception e) {
        Logger.instance.error("Unable to detect language, leaving language code blank");
      }

      filename = filenameLibraryBuilder.buildSubtitle(release, filename, languageCode, 0);
    }
    return filename;
  }
}
