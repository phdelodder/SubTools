package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.JOpenSubtitlesApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesHasher;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.model.OpenSubtitlesMovieDescriptor;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.model.OpenSubtitlesSubtitleDescriptor;
import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.*;

public class JOpenSubAdapter implements JSubAdapter, SubtitleProvider {

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

  @Override
  public String getName() {
    return "OpenSubtitles";
  }

  @Override
  public List<Subtitle> search(Release release, String languageCode) {
    if (release instanceof MovieRelease) {
      return this.searchSubtitles((MovieRelease) release, languageCode);
    } else {
      return this.searchSubtitles((TvRelease) release, languageCode);
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
  public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String... sublanguageids) {
    List<OpenSubtitlesSubtitleDescriptor> lSubtitles =
        new ArrayList<OpenSubtitlesSubtitleDescriptor>();
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    try {
      if (isLoginOk()) {
        if (!movieRelease.getFilename().equals("")) {
          File file = new File(movieRelease.getPath(), movieRelease.getFilename());
          if (file.exists())
            try {
              lSubtitles =
                  joapi.searchSubtitles(OpenSubtitlesHasher.computeHash(file),
                      String.valueOf(file.length()), sublanguageids);
            } catch (Exception e) {
              Logger.instance.error("API OPENSUBTITLES searchSubtitles using file hash: " + e);
            }
        }
        if (movieRelease.getImdbid() != 0) {
          try {
            lSubtitles.addAll(joapi.searchSubtitles(movieRelease.getImdbid(), sublanguageids));
          } catch (Exception e) {
            Logger.instance.error("API OPENSUBTITLES searchSubtitles using imdbid: " + e);
          }
        }
        if (lSubtitles.size() == 0) {
          try {
            lSubtitles.addAll(joapi.searchSubtitles(movieRelease.getTitle(), sublanguageids));
          } catch (Exception e) {
            Logger.instance.error("API OPENSUBTITLES searchSubtitles using title: " + e);
          }
        }
      }
    } catch (Exception e) {
      Logger.instance.error("API OPENSUBTITLES searchSubtitles: " + e);
    }
    for (OpenSubtitlesSubtitleDescriptor ossd : lSubtitles) {
      if (movieRelease.getYear() == ossd.getMovieYear()) {
        listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.OPENSUBTITLES, ossd
            .getSubFileName(), ossd.getSubDownloadLink(), ossd.getISO639(), "",
            SubtitleMatchType.EVERYTHING, ReleaseParser.extractReleasegroup(ossd.getSubFileName()),
            ossd.getUserNickName(), Boolean.valueOf(ossd.getSubHearingImpaired())));
      }
    }
    return listFoundSubtitles;
  }

  public int searchMovieOnIMDB(MovieRelease movieRelease) {
    List<OpenSubtitlesMovieDescriptor> losm = new ArrayList<OpenSubtitlesMovieDescriptor>();
    try {
      if (isLoginOk()) losm = joapi.searchMoviesOnIMDB(movieRelease.getTitle());
    } catch (Exception e) {
      Logger.instance.error("API OPENSUBTITLES searchMovieOnIMDB: " + e.getCause());
    }

    Pattern p = Pattern.compile(movieRelease.getTitle(), Pattern.CASE_INSENSITIVE);
    Matcher m;

    for (OpenSubtitlesMovieDescriptor osm : losm) {
      m = p.matcher(osm.getName());
      if (m.find()) {
        if (movieRelease.getYear() > 0) {
          if (osm.getYear() == movieRelease.getYear()) return osm.getImdbId();
        } else {
          return osm.getImdbId();
        }
      }

    }

    return 0;
  }

  public OpenSubtitlesMovieDescriptor getIMDBMovieDetails(MovieRelease movieRelease) {
    OpenSubtitlesMovieDescriptor osm = null;
    try {
      if (isLoginOk()) osm = joapi.getIMDBMovieDetails(movieRelease.getImdbid());
    } catch (Exception e) {
      Logger.instance.error("API OPENSUBTITLES getIMDBMovieDetails: " + e.getCause());
    }
    return osm;
  }

  @Override
  public List<Subtitle> searchSubtitles(TvRelease tvRelease, String... sublanguageids) {
    List<OpenSubtitlesSubtitleDescriptor> lSubtitles =
        new ArrayList<OpenSubtitlesSubtitleDescriptor>();
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    try {
      if (isLoginOk()) {
        try {

          String showName = "";
          if (tvRelease.getOriginalShowName().length() > 0) {
            showName = tvRelease.getOriginalShowName();
          } else {
            showName = tvRelease.getShow();
          }

          if (showName.length() > 0) {

            lSubtitles.addAll(joapi.searchSubtitles(showName, tvRelease.getSeason(),
                tvRelease.getEpisodeNumbers(), sublanguageids));
          }
        } catch (Exception e) {
          Logger.instance.error("API OPENSUBTITLES searchSubtitles using title: " + e);
        }
      }
    } catch (Exception e) {
      Logger.instance.error("API OPENSUBTITLES searchSubtitles: " + e);
    }
    String name = tvRelease.getShow().replaceAll("[^A-Za-z]", "").toLowerCase();
    String originalName = tvRelease.getOriginalShowName().replaceAll("[^A-Za-z]", "").toLowerCase();
    for (OpenSubtitlesSubtitleDescriptor ossd : lSubtitles) {
      String subFileName = ossd.getSubFileName().replaceAll("[^A-Za-z]", "").toLowerCase();
      if (subFileName.contains(name)
          | (originalName.length() > 0 && subFileName.contains(originalName))) {
        listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.OPENSUBTITLES, ossd
            .getSubFileName(), ossd.getSubDownloadLink(), ossd.getISO639(), "",
            SubtitleMatchType.EVERYTHING, ReleaseParser.extractReleasegroup(ossd.getSubFileName()),
            ossd.getUserNickName(), Boolean.valueOf(ossd.getSubHearingImpaired())));
      }
    }
    return listFoundSubtitles;
  }
}
