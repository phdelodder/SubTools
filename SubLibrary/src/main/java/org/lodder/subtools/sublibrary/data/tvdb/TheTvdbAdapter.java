package org.lodder.subtools.sublibrary.data.tvdb;

import java.util.Optional;

import javax.swing.JOptionPane;

import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvdb.exception.TheTvdbException;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbEpisode;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbSerie;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class TheTvdbAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheTvdbAdapter.class);
    private static JTheTvdbAdapter adapter;
    private final TheTvdbApiV2 theTvdbApi;

    public TheTvdbAdapter(Manager manager) {
        this.theTvdbApi = new TheTvdbApiV2(manager, "A1720D2DDFDCE82D");
    }

    public Optional<TheTvdbSerie> getSerie(String serieName) {
        try {
            return this.theTvdbApi.getSerieId(serieName, null, () -> askUserToEnterTvdbId(serieName))
                    .mapToOptionalObj(tvdbId -> theTvdbApi.getSerie(tvdbId, null));
        } catch (TheTvdbException e) {
            LOGGER.error("API TVDB getSerie for serie [%s] (%s)".formatted(serieName, e.getMessage()), e);
            return Optional.empty();
        }
    }

    public Optional<TheTvdbEpisode> getEpisode(int tvdbId, int season, int episode) {
        try {
            return this.theTvdbApi.getEpisode(tvdbId, season, episode, Language.ENGLISH);
        } catch (TheTvdbException e) {
            LOGGER.error("API TVDB getEpisode for serie id [%s] %s (%s)".formatted(tvdbId, TvRelease.formatSeasonEpisode(season, episode),
                    e.getMessage()), e);
            return Optional.empty();
        }
    }

    public synchronized static JTheTvdbAdapter getAdapter(Manager manager) {
        if (adapter == null) {
            adapter = new JTheTvdbAdapter(manager);
        }
        return adapter;
    }

    private Optional<Integer> askUserToEnterTvdbId(String showName) {
        LOGGER.error("Unknown serie name in tvdb: " + showName);
        String tvdbidString = JOptionPane.showInputDialog(null, "Enter tvdb id for serie " + showName);
        if (tvdbidString == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(tvdbidString));
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid tvdb id: " + tvdbidString);
            return askUserToEnterTvdbId(showName);
        }
    }

}
