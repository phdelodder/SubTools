package org.lodder.subtools.multisubdownloader.lib.library;

import java.io.File;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.util.StringUtils;

public class PathLibraryBuilder extends LibraryBuilder {

  public PathLibraryBuilder(LibrarySettings librarySettings) {
    super(librarySettings);
  }

  public String build(Release release) {
    if (librarySettings.getLibraryAction().equals(LibraryActionType.MOVE)
        || librarySettings.getLibraryAction().equals(LibraryActionType.MOVEANDRENAME)) {
      String folder = "";
      if (release.getVideoType() == VideoType.EPISODE)
        folder = buildEpisode((TvRelease) release);
      else if (release.getVideoType() == VideoType.MOVIE)
        folder = buildMovie((MovieRelease) release);
      return new File(librarySettings.getLibraryFolder(), folder).toString();
    } else {
      return release.getPath().toString();
    }
  }

  protected String buildEpisode(TvRelease tvRelease) {
    String folder = librarySettings.getLibraryFolderStructure();
    String show = "";
    if (librarySettings.isLibraryUseTVDBNaming()) {
      final JTheTVDBAdapter jtvdb = JTheTVDBAdapter.getAdapter();
      show = jtvdb.getSerie(tvRelease).getSerieName();
    } else {
      show = tvRelease.getShow();
    }
    if (librarySettings.isLibraryReplaceChars()) {
      show = StringUtils.removeIllegalWindowsChars(show);
    }

    folder = folder.replaceAll("%SHOW NAME%", show);
    // order is important!
    folder = replaceFormatedEpisodeNumber(folder, "%EEX%", tvRelease.getEpisodeNumbers(), true);
    folder = replaceFormatedEpisodeNumber(folder, "%EX%", tvRelease.getEpisodeNumbers(), false);
    folder = folder.replaceAll("%SS%", formatedNumber(tvRelease.getSeason(), true));
    folder = folder.replaceAll("%S%", formatedNumber(tvRelease.getSeason(), false));
    folder = folder.replaceAll("%EE%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), true));
    folder = folder.replaceAll("%E%", formatedNumber(tvRelease.getEpisodeNumbers().get(0), false));
    folder = folder.replaceAll("%TITLE%", tvRelease.getTitle());
    try {
      folder = folder.replaceAll("%SEPARATOR%", File.separator);
    } catch (IndexOutOfBoundsException | IllegalArgumentException ioobe) // windows hack needs "\\" instead of "\"
    {
      folder = folder.replaceAll("%SEPARATOR%", File.separator + File.separator);
    }
    folder = folder.replaceAll("%QUALITY%", tvRelease.getQuality());

    if (librarySettings.isLibraryFolderReplaceSpace()) {
      folder = folder.replaceAll(" ", librarySettings.getLibraryFolderReplacingSpaceSign());
    }

    return folder;
  }

  protected String buildMovie(MovieRelease movieRelease) {
    String folder = librarySettings.getLibraryFolderStructure();
    String title = movieRelease.getTitle();

    if (librarySettings.isLibraryReplaceChars()) {
      title = StringUtils.removeIllegalWindowsChars(title);
    }

    folder = folder.replaceAll("%MOVIE TITLE%", title);
    folder = folder.replaceAll("%YEAR%", Integer.toString(movieRelease.getYear()));
    folder = folder.replaceAll("%SEPARATOR%", File.separator);
    folder = folder.replaceAll("%QUALITY%", movieRelease.getQuality());

    if (librarySettings.isLibraryFolderReplaceSpace()) {
      folder = folder.replaceAll(" ", librarySettings.getLibraryFolderReplacingSpaceSign());
    }

    return folder;
  }

}
