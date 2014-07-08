package org.lodder.subtools.multisubdownloader.lib.library;

import java.io.File;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.model.MovieFile;
import org.lodder.subtools.sublibrary.model.VideoFile;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.util.StringUtils;

public class PathLibraryBuilder extends LibraryBuilder {

  public PathLibraryBuilder(LibrarySettings librarySettings) {
    super(librarySettings);
  }

  public File buildPath(VideoFile videoFile) {
    if (librarySettings.getLibraryAction().equals(LibraryActionType.MOVE)
        || librarySettings.getLibraryAction().equals(LibraryActionType.MOVEANDRENAME)) {
      String folder = "";
      if (videoFile.getVideoType() == VideoType.EPISODE)
        folder = episodeBuildPath((EpisodeFile) videoFile);
      else if (videoFile.getVideoType() == VideoType.MOVIE)
        folder = movieBuildPath((MovieFile) videoFile);
      return new File(librarySettings.getLibraryFolder(), folder);
    } else {
      return videoFile.getPath();
    }
  }

  protected String episodeBuildPath(EpisodeFile episodeFile) {
    String folder = librarySettings.getLibraryFolderStructure();
    String show = "";
    if (librarySettings.isLibraryUseTVDBNaming()) {
      final JTheTVDBAdapter jtvdb = JTheTVDBAdapter.getAdapter();
      show = jtvdb.getSerie(episodeFile).getSerieName();
    } else {
      show = episodeFile.getShow();
    }
    if (librarySettings.isLibraryReplaceChars()) {
      show = StringUtils.removeIllegalWindowsChars(show);
    }

    folder = folder.replaceAll("%SHOW NAME%", show);
    // order is important!
    folder = replaceFormatedEpisodeNumber(folder, "%EEX%", episodeFile.getEpisodeNumbers(), true);
    folder = replaceFormatedEpisodeNumber(folder, "%EX%", episodeFile.getEpisodeNumbers(), false);
    folder = folder.replaceAll("%SS%", formatedNumber(episodeFile.getSeason(), true));
    folder = folder.replaceAll("%S%", formatedNumber(episodeFile.getSeason(), false));
    folder =
        folder.replaceAll("%EE%", formatedNumber(episodeFile.getEpisodeNumbers().get(0), true));
    folder =
        folder.replaceAll("%E%", formatedNumber(episodeFile.getEpisodeNumbers().get(0), false));
    folder = folder.replaceAll("%TITLE%", episodeFile.getTitle());
    try {
      folder = folder.replaceAll("%SEPARATOR%", File.separator);
    } catch (IndexOutOfBoundsException ioobe) // windows hack needs "\\" instead of "\"
    {
      folder = folder.replaceAll("%SEPARATOR%", File.separator + File.separator);
    }
    folder = folder.replaceAll("%QUALITY%", episodeFile.getQuality());

    if (librarySettings.isLibraryFolderReplaceSpace()) {
      folder = folder.replaceAll(" ", librarySettings.getLibraryFolderReplacingSpaceSign());
    }

    return folder;
  }

  protected String movieBuildPath(MovieFile movieFile) {
    String folder = librarySettings.getLibraryFolderStructure();
    String title = movieFile.getTitle();

    if (librarySettings.isLibraryReplaceChars()) {
      title = StringUtils.removeIllegalWindowsChars(title);
    }

    folder = folder.replaceAll("%MOVIE TITLE%", title);
    folder = folder.replaceAll("%YEAR%", Integer.toString(movieFile.getYear()));
    folder = folder.replaceAll("%SEPARATOR%", File.separator);
    folder = folder.replaceAll("%QUALITY%", movieFile.getQuality());

    if (librarySettings.isLibraryFolderReplaceSpace()) {
      folder = folder.replaceAll(" ", librarySettings.getLibraryFolderReplacingSpaceSign());
    }

    return folder;
  }

}
