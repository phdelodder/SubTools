package org.lodder.subtools.multisubdownloader.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.control.VideoFileParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.model.MovieFile;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.subtitlesource.opensubtitles.JOpenSubtitlesApi;
import org.lodder.subtools.sublibrary.subtitlesource.opensubtitles.OpenSubtitlesHasher;
import org.lodder.subtools.sublibrary.subtitlesource.opensubtitles.model.OpenSubtitlesMovieDescriptor;
import org.lodder.subtools.sublibrary.subtitlesource.opensubtitles.model.OpenSubtitlesSubtitleDescriptor;

public class JOpenSubAdapter implements JSubAdapter {

  private static JOpenSubtitlesApi joapi;

  public JOpenSubAdapter() {
    try {
      if (joapi == null)
      // joapi = new JOpenSubtitlesApi("OS Test User Agent");
        joapi = new JOpenSubtitlesApi("JBierSubDownloader");
      if (!joapi.isLoggedOn()) {
        joapi.loginAnonymous();
      }
    } catch (Exception e) {
      Logger.instance.error("API OPENSUBTITLES INIT: " + e.getCause());
    }
  }

  protected void finalize() throws Throwable { // do finalization here
    if (joapi.isLoggedOn()) joapi.logout();
    super.finalize(); // not necessar if extending Object.
  }

  public boolean isLoginOk() {
    if (!joapi.isLoggedOn()) {
      try {
        joapi.loginAnonymous();
        return true;
      } catch (Exception e) {
        return false;
      }
    } else {
      return true;
    }
  }

  @Override
  public List<Subtitle> searchSubtitles(MovieFile movieFile, String... sublanguageids) {
    List<OpenSubtitlesSubtitleDescriptor> lSubtitles =
        new ArrayList<OpenSubtitlesSubtitleDescriptor>();
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    try {
      if (isLoginOk()) {
        if (!movieFile.getFilename().equals("")) {
          File file = new File(movieFile.getPath(), movieFile.getFilename());
          if (file.exists())
            try {
              lSubtitles =
                  joapi.searchSubtitles(OpenSubtitlesHasher.computeHash(file),
                      String.valueOf(file.length()), sublanguageids);
            } catch (Exception e) {
              Logger.instance.error("API OPENSUBTITLES searchSubtitles using file hash: " + e);
            }
        }
        if (movieFile.getImdbid() != 0) {
          try {
            lSubtitles.addAll(joapi.searchSubtitles(movieFile.getImdbid(), sublanguageids));
          } catch (Exception e) {
            Logger.instance.error("API OPENSUBTITLES searchSubtitles using imdbid: " + e);
          }
        }
        if (lSubtitles.size() == 0) {
          try {
            lSubtitles.addAll(joapi.searchSubtitles(movieFile.getTitle(), sublanguageids));
          } catch (Exception e) {
            Logger.instance.error("API OPENSUBTITLES searchSubtitles using title: " + e);
          }
        }
      }
    } catch (Exception e) {
      Logger.instance.error("API OPENSUBTITLES searchSubtitles: " + e);
    }
    for (OpenSubtitlesSubtitleDescriptor ossd : lSubtitles) {
      listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.OPENSUBTITLES, ossd
          .getSubFileName(), ossd.getSubDownloadLink(), ossd.getISO639(), "",
          SubtitleMatchType.EVERYTHING, VideoFileParser.extractTeam(ossd.getSubFileName()), ossd
              .getUserNickName(), Boolean.valueOf(ossd.getSubHearingImpaired())));
    }
    return listFoundSubtitles;
  }

  public int searchMovieOnIMDB(MovieFile movieFile) {
    List<OpenSubtitlesMovieDescriptor> losm = new ArrayList<OpenSubtitlesMovieDescriptor>();
    try {
      if (isLoginOk()) losm = joapi.searchMoviesOnIMDB(movieFile.getTitle());
    } catch (Exception e) {
      Logger.instance.error("API OPENSUBTITLES searchMovieOnIMDB: " + e.getCause());
    }

    Pattern p = Pattern.compile(movieFile.getTitle(), Pattern.CASE_INSENSITIVE);
    Matcher m;

    for (OpenSubtitlesMovieDescriptor osm : losm) {
      m = p.matcher(osm.getName());
      if (m.find()) {
        if (movieFile.getYear() > 0) {
          if (osm.getYear() == movieFile.getYear()) return osm.getImdbId();
        } else {
          return osm.getImdbId();
        }
      }

    }

    return 0;
  }

  public OpenSubtitlesMovieDescriptor getIMDBMovieDetails(MovieFile movieFile) {
    OpenSubtitlesMovieDescriptor osm = null;
    try {
      if (isLoginOk()) osm = joapi.getIMDBMovieDetails(movieFile.getImdbid());
    } catch (Exception e) {
      Logger.instance.error("API OPENSUBTITLES getIMDBMovieDetails: " + e.getCause());
    }
    return osm;
  }

  @Override
  public List<Subtitle> searchSubtitles(EpisodeFile episodeFile, String... sublanguageids) {
    List<OpenSubtitlesSubtitleDescriptor> lSubtitles =
        new ArrayList<OpenSubtitlesSubtitleDescriptor>();
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    try {
      if (isLoginOk()) {
        try {

          String showName = "";
          if (episodeFile.getOriginalShowName().length() > 0) {
            showName = episodeFile.getOriginalShowName();
          } else {
            showName = episodeFile.getShow();
          }

          if (showName.length() > 0) {

            lSubtitles.addAll(joapi.searchSubtitles(showName, episodeFile.getSeason(),
                episodeFile.getEpisodeNumbers(), sublanguageids));
          }
        } catch (Exception e) {
          Logger.instance.error("API OPENSUBTITLES searchSubtitles using title: " + e);
        }
      }
    } catch (Exception e) {
      Logger.instance.error("API OPENSUBTITLES searchSubtitles: " + e);
    }
    String name = episodeFile.getShow().replaceAll("[^A-Za-z]", "").toLowerCase();
    String originalName = episodeFile.getOriginalShowName().replaceAll("[^A-Za-z]", "").toLowerCase();
    for (OpenSubtitlesSubtitleDescriptor ossd : lSubtitles) {
      String subFileName = ossd.getSubFileName().replaceAll("[^A-Za-z]", "").toLowerCase();
      if (subFileName.contains(name)
          | (originalName.length() > 0 && subFileName.contains(originalName))) {
        listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.OPENSUBTITLES, ossd
            .getSubFileName(), ossd.getSubDownloadLink(), ossd.getISO639(), "",
            SubtitleMatchType.EVERYTHING, VideoFileParser.extractTeam(ossd.getSubFileName()), ossd
                .getUserNickName(), Boolean.valueOf(ossd.getSubHearingImpaired())));
      }
    }
    return listFoundSubtitles;
  }
}
