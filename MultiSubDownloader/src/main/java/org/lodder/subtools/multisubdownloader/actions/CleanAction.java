package org.lodder.subtools.multisubdownloader.actions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.util.FilenameContainsFilter;
import org.lodder.subtools.sublibrary.util.FilenameExtensionFilter;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.StringUtils;

public class CleanAction {

  private LibrarySettings librarySettings;
  private final String[] fileFilters = new String[] {"nfo", "jpg", "sfv", "srr", "srs", "nzb",
      "torrent", "txt"};
  private final String[] folderFilters = new String[] {"sample", "Sample"};
  private final String sampleDirName = "sample";

  public CleanAction(LibrarySettings librarySettings) {
    this.librarySettings = librarySettings;
  }

  public void cleanUpFiles(Release release, File path, String videoFileName) throws IOException {
    Logger.instance.trace("Actions", "cleanUpFiles",
        "LibraryOtherFileAction" + librarySettings.getLibraryOtherFileAction());

    final String[] files = release.getPath().list(new FilenameExtensionFilter(fileFilters));

    final String[] folders = release.getPath().list(new FilenameContainsFilter(folderFilters));

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

        if (s.contains(sampleDirName) && !f.isDirectory()) {
          extension = sampleDirName + "." + extension;
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

        if (s.contains(sampleDirName) && !f.isDirectory()) {
          extension = sampleDirName + "." + extension;
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
