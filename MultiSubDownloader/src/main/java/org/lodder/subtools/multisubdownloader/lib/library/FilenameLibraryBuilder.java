package org.lodder.subtools.multisubdownloader.lib.library;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.VideoFile;
import org.lodder.subtools.sublibrary.util.StringUtils;

public class FilenameLibraryBuilder extends LibraryBuilder {

  public FilenameLibraryBuilder(LibrarySettings librarySettings) {
    super(librarySettings);
  }

  public String buildFileName(VideoFile videoFile) {
    String filename = "";
    if (((librarySettings.getLibraryAction().equals(LibraryActionType.RENAME) || librarySettings
        .getLibraryAction().equals(LibraryActionType.MOVEANDRENAME)))
        && videoFile instanceof EpisodeFile
        && !librarySettings.getLibraryFilenameStructure().isEmpty()) {
      EpisodeFile episodeFile = (EpisodeFile) videoFile;

      String show = "";
      if (librarySettings.isLibraryUseTVDBNaming()) {
        final JTheTVDBAdapter jtvdb = JTheTVDBAdapter.getAdapter();
        show = jtvdb.getSerie(episodeFile).getSerieName();
      } else {
        show = episodeFile.getShow();
      }

      filename = librarySettings.getLibraryFilenameStructure();
      // order is important!
      filename = filename.replaceAll("%SHOW NAME%", show);
      filename =
          replaceFormatedEpisodeNumber(filename, "%EEX%", episodeFile.getEpisodeNumbers(), true);
      filename =
          replaceFormatedEpisodeNumber(filename, "%EX%", episodeFile.getEpisodeNumbers(), false);
      filename = filename.replaceAll("%SS%", formatedNumber(episodeFile.getSeason(), true));
      filename = filename.replaceAll("%S%", formatedNumber(episodeFile.getSeason(), false));
      filename =
          filename.replaceAll("%EE%", formatedNumber(episodeFile.getEpisodeNumbers().get(0), true));
      filename =
          filename.replaceAll("%E%", formatedNumber(episodeFile.getEpisodeNumbers().get(0), false));
      filename = filename.replaceAll("%TITLE%", episodeFile.getTitle());
      filename = filename.replaceAll("%QUALITY%", videoFile.getQuality());
      filename = filename.replaceAll("%DESCRIPTION%", videoFile.getDescription());

      filename += "." + videoFile.getExtension();
    } else {
      filename = videoFile.getFilename();
    }
    if (librarySettings.isLibraryReplaceChars()) {
      filename = StringUtils.removeIllegalWindowsChars(filename);
    }
    if (librarySettings.isLibraryFilenameReplaceSpace()) {
      filename = filename.replaceAll(" ", librarySettings.getLibraryFilenameReplacingSpaceSign());
    }
    return filename;
  }

  public String buildSubFileName(VideoFile videoFile, Subtitle sub, String filename, int version) {
    return buildSubFileName(videoFile, filename, sub.getLanguagecode(), version);
  }

  public String buildSubFileName(VideoFile videoFile, String filename, String languageCode,
      int version) {
    final String extension = "." + videoFile.getExtension();
    if (version > 0) {
      filename =
          filename.substring(0, filename.indexOf(extension)) + "-v" + version + "."
              + videoFile.getExtension();
    }
    if (librarySettings.isLibraryIncludeLanguageCode()) {
      if (languageCode.equals("nl")) {
        if (librarySettings.getDefaultNlText().equals("")) {
          filename = changeExtension(filename, ".nld.srt");
        } else {
          final String ext = "." + librarySettings.getDefaultNlText() + ".srt";
          filename = changeExtension(filename, ext);
        }
      } else if (languageCode.equals("en")) {
        if (librarySettings.getDefaultEnText().equals("")) {
          filename = changeExtension(filename, ".eng.srt");
        } else {
          final String ext = "." + librarySettings.getDefaultEnText() + ".srt";
          filename = changeExtension(filename, ext);
        }
      } else {
        filename = changeExtension(filename, ".nld.srt");
      }
    } else {

      filename = changeExtension(filename, ".srt");
    }
    if (librarySettings.isLibraryReplaceChars()) {
      filename = StringUtils.removeIllegalWindowsChars(filename);
    }
    if (librarySettings.isLibraryFilenameReplaceSpace()) {
      filename = filename.replaceAll(" ", librarySettings.getLibraryFilenameReplacingSpaceSign());
    }
    return filename;
  }

  // ============================================== changeExtension
  // changes extension to new extension
  // example: x = changeExtension("data.txt", ".java")
  // will assign "data.java" to x.
  public static String changeExtension(String originalName, String newExtension) {
    int lastDot = originalName.lastIndexOf(".");
    if (lastDot != -1) {
      return originalName.substring(0, lastDot) + newExtension;
    } else {
      return originalName + newExtension;
    }
  }
}
