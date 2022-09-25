package org.lodder.subtools.multisubdownloader.lib;

import java.util.Optional;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvrage.TVRageApi;
import org.lodder.subtools.sublibrary.data.tvrage.TvrageException;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageShowInfo;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JTVRageAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JTVRageAdapter.class);

    private final TVRageApi trapi;

    public JTVRageAdapter(Manager manager) {
        trapi = new TVRageApi(manager);
    }

    public Optional<TVRageShowInfo> getShowInfo(String showName) {
        try {
            return trapi.searchShow(showName).stream().filter(show -> show.getShowName().equals(showName)).findAny();
        } catch (TvrageException e) {
            LOGGER.error("API TVRage showInfo using name [%s] (%s)".formatted(showName, e.getMessage()), e);
            return Optional.empty();
        }
    }

    public Optional<TVRageEpisode> getEpisodeInfo(int showId, int seasonId, int episodeId) {
        try {
            return trapi.getEpisodeInfo(String.valueOf(showId), seasonId, episodeId);
        } catch (TvrageException e) {
            LOGGER.error("API TVRage episodeInfo using id [%s] %s (%s)".formatted(showId, TvRelease.formatSeasonEpisode(seasonId, episodeId),
                    e.getMessage()), e);
            return Optional.empty();
        }
    }
}
