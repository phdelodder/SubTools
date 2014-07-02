package org.lodder.subtools.sublibrary;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.lodder.subtools.sublibrary.data.thetvdb.TheTVDBApi;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBEpisode;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.util.http.HttpClient;

public class JTheTVDBAdapter {

  private final TheTVDBApi jtvapi;
  private static JTheTVDBAdapter adapter;
  private String exceptions = "";
  private final String splitValue = ": '";

  JTheTVDBAdapter() {
    this.jtvapi = new TheTVDBApi("A1720D2DDFDCE82D");
    try {
      exceptions =
          HttpClient.getHttpClient().downloadText(
              new URL("http://midgetspy.github.io/sb_tvdb_scene_exceptions/exceptions.txt"));
    } catch (IOException e) {
      Logger.instance.error(Logger.stack2String(e));
    }
  }

  public TheTVDBSerie searchSerie(EpisodeFile episode) {
    int tvdbid = this.jtvapi.searchSerie(episode.getShow(), null);
    if (tvdbid == 0) {
      Logger.instance.error("Unknown serie name in tvdb: " + episode.getShow());
      return null;
    }
    return this.jtvapi.getSerie(tvdbid, null);
  }

  public TheTVDBEpisode getEpisode(EpisodeFile episode) {
    return this.jtvapi.getEpisode(episode.getTvdbid(), episode.getSeason(), episode
        .getEpisodeNumbers().get(0), "en");
  }

  public TheTVDBSerie getSerie(EpisodeFile episode) {
    if (episode.getTvdbid() > 0) {
      return this.jtvapi.getSerie(episode.getTvdbid(), null);
    } else {
      int tvdbid = sickbeardTVDBSceneExceptions(episode.getShow());
      if (tvdbid > 0) return this.jtvapi.getSerie(tvdbid, null);
      return searchSerie(episode);
    }
  }

  public TheTVDBSerie getSerie(int tvdbid) {
    return this.jtvapi.getSerie(tvdbid, null);
  }

  public List<TheTVDBEpisode> getAllEpisodes(int tvdbid, String language) {
    return this.jtvapi.getAllEpisodes(tvdbid, language);
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
  public static JTheTVDBAdapter getAdapter() {
    if (adapter == null) adapter = new JTheTVDBAdapter();
    return adapter;
  }
}
