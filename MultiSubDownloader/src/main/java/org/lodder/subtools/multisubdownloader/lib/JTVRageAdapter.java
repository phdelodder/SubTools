package org.lodder.subtools.multisubdownloader.lib;

import java.util.List;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvrage.TVRageApi;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageShowInfo;
import org.lodder.subtools.sublibrary.model.TvRelease;

public class JTVRageAdapter {

  private final TVRageApi trapi;

  public JTVRageAdapter(Manager manager) {
    trapi = new TVRageApi(manager);
  }

  public TVRageShowInfo searchShow(TvRelease newEp) {
    return searchShow(newEp.getShow());
  }

  public TVRageShowInfo searchShow(String showName) {
    List<TVRageShowInfo> tvrageShowInfos = trapi.searchShow(showName);
    for (TVRageShowInfo show : tvrageShowInfos) {
      if (show.getShowName().equals(showName)) {
        return show;
      }
    }
    return null;
  }

  public TVRageEpisode getEpisodeInfo(int showID, int seasonId, Integer episodeId) {
    return trapi.getEpisodeInfo(String.valueOf(showID), String.valueOf(seasonId),
        String.valueOf(episodeId));
  }
}
