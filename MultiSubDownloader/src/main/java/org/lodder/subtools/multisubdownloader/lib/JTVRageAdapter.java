package org.lodder.subtools.multisubdownloader.lib;

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
        return searchShow(newEp.getShowName());
    }

    public TVRageShowInfo searchShow(String showName) {
        return trapi.searchShow(showName).stream().filter(show -> show.getShowName().equals(showName)).findAny().orElse(null);
    }

    public TVRageEpisode getEpisodeInfo(int showID, int seasonId, Integer episodeId) {
        return trapi.getEpisodeInfo(String.valueOf(showID), String.valueOf(seasonId), String.valueOf(episodeId));
    }
}
