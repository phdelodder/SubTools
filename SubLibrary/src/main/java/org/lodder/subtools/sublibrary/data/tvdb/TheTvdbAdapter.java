package org.lodder.subtools.sublibrary.data.tvdb;

import java.util.Optional;
import java.util.OptionalInt;

import javax.swing.JOptionPane;

import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.UserInteractionHandler;
import org.lodder.subtools.sublibrary.data.tvdb.exception.TheTvdbException;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbEpisode;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbSerie;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class TheTvdbAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheTvdbAdapter.class);
    private static TheTvdbAdapter instance;
    private final LazySupplier<TheTvdbApi> jtvapi;

    private TheTvdbAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        jtvapi = new LazySupplier<>(() -> {
            try {
                return new TheTvdbApi(manager, userInteractionHandler, "A1720D2DDFDCE82D");
            } catch (Exception e) {
                LOGGER.error("API TVDB INIT (%s)".formatted(e.getMessage()), e);
            }
            return null;
        });
    }

    private TheTvdbApi getApi() {
        return jtvapi.get();
    }

    public Optional<TheTvdbSerie> getSerie(String serieName) {
        try {
            return getApi().getSerieId(serieName, null, () -> askUserToEnterTvdbId(serieName))
                    .mapToOptionalObj(tvdbId -> getApi().getSerie(tvdbId, null));
        } catch (TheTvdbException e) {
            LOGGER.error("API TVDB getSerie for serie [%s] (%s)".formatted(serieName, e.getMessage()), e);
            return Optional.empty();
        }
    }

    public Optional<TheTvdbEpisode> getEpisode(int tvdbId, int season, int episode) {
        try {
            return getApi().getEpisode(tvdbId, season, episode, Language.ENGLISH);
        } catch (TheTvdbException e) {
            LOGGER.error("API TVDB getEpisode for serie id [%s] %s (%s)".formatted(tvdbId, TvRelease.formatSeasonEpisode(season, episode),
                    e.getMessage()), e);
            return Optional.empty();
        }

    }

    public synchronized static TheTvdbAdapter getInstance(Manager manager, UserInteractionHandler userInteractionHandler) {
        if (instance == null) {
            instance = new TheTvdbAdapter(manager, userInteractionHandler);
        }
        return instance;
    }

    private OptionalInt askUserToEnterTvdbId(String showName) {
        LOGGER.error("Unknown serie name in tvdb: " + showName);
        String tvdbidString = JOptionPane.showInputDialog(null, "Enter tvdb id for serie " + showName);
        if (tvdbidString == null) {
            return OptionalInt.empty();
        }
        try {
            return OptionalInt.of(Integer.parseInt(tvdbidString));
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid tvdb id: " + tvdbidString);
            return askUserToEnterTvdbId(showName);
        }
    }
}
