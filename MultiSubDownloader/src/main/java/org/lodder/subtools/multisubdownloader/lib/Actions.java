package org.lodder.subtools.multisubdownloader.lib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.lib.library.PathLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.DetectLanguage;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.util.FilenameContainsFilter;
import org.lodder.subtools.sublibrary.util.FilenameExtensionFilter;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.StringUtils;

public class Actions {

  private final Settings settings;
  private final boolean usingCMD;

  public Actions(Settings settings, final boolean usingCMD) {
    this.settings = settings;
    this.usingCMD = usingCMD;
  }

  public static String buildDisplayLine(Subtitle subtitle) {
    String hearingImpaired = "";
    if (subtitle.isHearingImpaired()) {
      hearingImpaired = " Hearing Impaired";
    }
    String uploader = "";
    if (!subtitle.getUploader().isEmpty())
      uploader = " (Uploader: " + subtitle.getUploader() + ") ";
    return "Scrore:" + subtitle.getScore() + "% " + subtitle.getFilename() + hearingImpaired
        + uploader + " (Source: " + subtitle.getSubtitleSource() + ") ";
  }

  public static void rename(LibrarySettings librarySettings, File f, Release release) {
    Logger.instance
        .trace("Actions", "rename", "LibraryAction" + librarySettings.getLibraryAction());
    String filename = "";
    if (librarySettings.getLibraryAction().equals(LibraryActionType.RENAME)
        || librarySettings.getLibraryAction().equals(LibraryActionType.MOVEANDRENAME)) {
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
    } else {
      filename = f.getName();
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
          cleanUpFiles(librarySettings, release, newDir, filename);
        }
        if (librarySettings.isLibraryRemoveEmptyFolders()
            && release.getPath().listFiles().length == 0) {
          release.getPath().delete();
        }
      } catch (IOException e) {
        Logger.instance.error("Unsuccessfull in moving the file to the libary");
      }

    }
  }

  public static void cleanUpFiles(LibrarySettings librarySettings, Release release, File path,
      String videoFileName) throws IOException {
    Logger.instance.trace("Actions", "cleanUpFiles",
        "LibraryOtherFileAction" + librarySettings.getLibraryOtherFileAction());
    final List<String> fileFilters = new ArrayList<String>();
    fileFilters.add("nfo");
    fileFilters.add("jpg");
    fileFilters.add("sfv");
    fileFilters.add("srr");
    fileFilters.add("srs");
    fileFilters.add("nzb");
    fileFilters.add("torrent");
    fileFilters.add("txt");
    final String[] files =
        release.getPath().list(
            new FilenameExtensionFilter(fileFilters.toArray(new String[fileFilters.size()])));

    final List<String> folderFilters = new ArrayList<String>();
    folderFilters.add("sample");
    folderFilters.add("Sample");
    final String[] folders =
        release.getPath().list(
            new FilenameContainsFilter(folderFilters.toArray(new String[folderFilters.size()])));

    // remove duplicates using set
    final Set<String> list =
        new LinkedHashSet<String>(Arrays.asList(StringUtils.join(files, folders)));

    if (librarySettings.getLibraryOtherFileAction().equals(LibraryOtherFileActionType.REMOVE)) {
      for (String s : list) {
        final File file = new File(release.getPath(), s);
        if (file.isDirectory()) {
          FileUtils.deleteDirectory(file);
        } else {
          file.delete();
        }
      }
    } else if (librarySettings.getLibraryOtherFileAction().equals(LibraryOtherFileActionType.MOVE)) {
      for (String s : list) {
        Files.move(new File(release.getPath(), s), new File(path, s));
      }
    } else if (librarySettings.getLibraryOtherFileAction().equals(
        LibraryOtherFileActionType.MOVEANDRENAME)) {
      for (String s : list) {
        String extension = ReleaseParser.extractFileNameExtension(s);

        File f = new File(release.getPath(), s);

        if (s.contains("sample") && !f.isDirectory()) {
          extension = "sample." + extension;
        }

        if (f.isFile()) {
          final String filename =
              videoFileName.substring(0, videoFileName.lastIndexOf(".")).concat("." + extension);
          Files.move(f, new File(path, filename));
        } else {
          Files.move(f, new File(path, s));
        }
      }
    } else if (librarySettings.getLibraryOtherFileAction()
        .equals(LibraryOtherFileActionType.RENAME)) {
      for (String s : files) {
        String extension = ReleaseParser.extractFileNameExtension(s);

        File f = new File(release.getPath(), s);

        if (s.contains("sample") && !f.isDirectory()) {
          extension = "sample." + extension;
        }

        if (f.isFile()) {
          final String filename =
              videoFileName.substring(0, videoFileName.lastIndexOf(".")).concat("." + extension);
          Files.move(f, new File(release.getPath(), filename));
        } else {
          Files.move(f, new File(path, s));
        }
      }
    }
  }
}
