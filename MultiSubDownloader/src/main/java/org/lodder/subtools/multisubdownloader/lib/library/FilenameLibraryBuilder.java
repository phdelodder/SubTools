package org.lodder.subtools.multisubdownloader.lib.library;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.util.StringUtils;

public class FilenameLibraryBuilder extends LibraryBuilder {

  public FilenameLibraryBuilder(LibrarySettings librarySettings) {
    super(librarySettings);
  }

  public String buildFileName(Release release) {
    String filename = "";
    if (((librarySettings.getLibraryAction().equals(LibraryActionType.RENAME) || librarySettings
        .getLibraryAction().equals(LibraryActionType.MOVEANDRENAME)))
        && release instanceof TvRelease
        && !librarySettings.getLibraryFilenameStructure().isEmpty()) {
      TvRelease tvRelease = (TvRelease) release;

      String show = "";
      if (librarySettings.isLibraryUseTVDBNaming()) {
        final JTheTVDBAdapter jtvdb = JTheTVDBAdapter.getAdapter();
        show = jtvdb.getSerie(tvRelease).getSerieName();
      } else {
        show = tvRelease.getShow();
      }

      filename = librarySettings.getLibraryFilenameStructure();
      // order is important!
      filename = filename.replaceAll("%SHOW NAME%", show);
      filename =
          replaceFormatedEpisodeNumber(filename, "%EEX%", tvRelease.getEpisodeNumbers(), true);
      filename =
          replaceFormatedEpisodeNumber(filename, "%EX%", tvRelease.getEpisodeNumbers(), false);
      filename = filename.replaceAll("%SS%", formatedNumber(tvRelease.getSeason(), true));
      filename = filename.replaceAll("%S%", formatedNumber(tvRelease.getSeason(), false));
      filename =
          filename.replaceAll("%EE%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), true));
      filename =
          filename.replaceAll("%E%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), false));
      filename = filename.replaceAll("%TITLE%", tvRelease.getTitle());
      filename = filename.replaceAll("%QUALITY%", release.getQuality());
      filename = filename.replaceAll("%DESCRIPTION%", release.getDescription());

      filename += "." + release.getExtension();
    } else {
      filename = release.getFilename();
    }
    if (librarySettings.isLibraryReplaceChars()) {
      filename = StringUtils.removeIllegalWindowsChars(filename);
    }
    if (librarySettings.isLibraryFilenameReplaceSpace()) {
      filename = filename.replaceAll(" ", librarySettings.getLibraryFilenameReplacingSpaceSign());
    }
    return filename;
  }

  public String buildSubFileName(Release release, Subtitle sub, String filename, int version) {
    return buildSubFileName(release, filename, sub.getLanguagecode(), version);
  }

  public String buildSubFileName(Release release, String filename, String languageCode,
      int version) {
    final String extension = "." + release.getExtension();
    if (version > 0) {
      filename =
          filename.substring(0, filename.indexOf(extension)) + "-v" + version + "."
              + release.getExtension();
    }
    if (librarySettings.isLibraryIncludeLanguageCode()) {
      switch (languageCode) {
        case "nl":
          if (librarySettings.getDefaultNlText().equals("")) {
            filename = changeExtension(filename, ".nld.srt");
          } else {
            final String ext = "." + librarySettings.getDefaultNlText() + ".srt";
            filename = changeExtension(filename, ext);
          }
          break;
        case "en":
          if (librarySettings.getDefaultEnText().equals("")) {
            filename = changeExtension(filename, ".eng.srt");
          } else {
            final String ext = "." + librarySettings.getDefaultEnText() + ".srt";
            filename = changeExtension(filename, ext);
          }
          break;
        default:
          filename = changeExtension(filename, ".nld.srt");
          break;
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
