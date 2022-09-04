package org.lodder.subtools.sublibrary;

import java.util.List;

import org.lodder.subtools.sublibrary.data.thetvdb.TheTVDBApiV2;
import org.lodder.subtools.sublibrary.data.thetvdb.TheTVDBException;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBEpisode;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JTheTVDBAdapter {

  private TheTVDBApiV2 jtvapi;
  private static JTheTVDBAdapter adapter;
  private String exceptions = "";
  private final static String splitValue = ": '";
  private static final Logger LOGGER = LoggerFactory.getLogger(JTheTVDBAdapter.class);

  JTheTVDBAdapter(Manager manager) {
    this.jtvapi = null;
    try {
      this.jtvapi = new TheTVDBApiV2("A1720D2DDFDCE82D");
      exceptions =
          manager
              .downloadText2("https://raw.githubusercontent.com/midgetspy/sb_tvdb_scene_exceptions/gh-pages/exceptions.txt");
    } catch (TheTVDBException | ManagerException e) {
      LOGGER.error(e.getMessage(), e);
    }

  }

  public TheTVDBSerie searchSerie(TvRelease episode) {
    int tvdbid;
    try {
      tvdbid = this.jtvapi.searchSerie(episode.getShow(), null);
      if (tvdbid == 0) {
        LOGGER.error("Unknown serie name in tvdb: " + episode.getShow());
        return null;
      }
      return this.jtvapi.getSerie(tvdbid, null);
    } catch (TheTVDBException e) {
      LOGGER.error(e.getMessage(),e);
    }
    return null;
  }

  public TheTVDBEpisode getEpisode(TvRelease episode) {
    try {
      return this.jtvapi.getEpisode(episode.getTvdbid(), episode.getSeason(), episode
          .getEpisodeNumbers().get(0), "en");
    } catch (TheTVDBException e) {
      LOGGER.error(e.getMessage(),e);
    }
    return null;
  }

  public TheTVDBSerie getSerie(TvRelease episode) {
    try {
      if (episode.getTvdbid() > 0) {
        return this.jtvapi.getSerie(episode.getTvdbid(), null);
      } else {
        int tvdbid = sickbeardTVDBSceneExceptions(episode.getShow());
        if (tvdbid > 0) return this.jtvapi.getSerie(tvdbid, null);
        return searchSerie(episode);
      }
    } catch (TheTVDBException e) {
      LOGGER.error(e.getMessage(),e);
    }
    return null;
  }

  public TheTVDBSerie getSerie(int tvdbid) {
    try {
      return this.jtvapi.getSerie(tvdbid, null);
    } catch (TheTVDBException e) {
      LOGGER.error("getSerie exception",e);
    }
    return null;
  }

  public List<TheTVDBEpisode> getAllEpisodes(int tvdbid, String language) {
    try {
      return this.jtvapi.getAllEpisodes(tvdbid, language);
    } catch (TheTVDBException e) {
      LOGGER.error("getAllEpisodes exception", e);
    }
    return null;
  }

  public int sickbeardTVDBSceneExceptions(String serieName) {
    if (exceptions.isEmpty()) return 0;
    for (String exception : exceptions.split("\n")) {
      int tvdbid = Integer.parseInt(exception.split(splitValue)[0].trim());
      String[] names = exception.split(splitValue)[1].split(",");
      for (String name : names) {
        String a = name.replaceAll("[^A-Za-z]", "");
        String b = serieName.replaceAll("[^A-Za-z]", "");
        if (!a.isEmpty() && a.equals(b)) {
          return tvdbid;
        }
      }
    }
    return 0;
  }

  /**
   * @return JTheTVDBAdapter
   */
  public synchronized static JTheTVDBAdapter getAdapter(Manager manager) {
    if (adapter == null) adapter = new JTheTVDBAdapter(manager);
    return adapter;
  }
}
