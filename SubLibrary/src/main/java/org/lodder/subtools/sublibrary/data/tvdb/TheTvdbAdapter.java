package org.lodder.subtools.sublibrary.data.tvdb;

import static manifold.science.util.UnitConstants.*;
import static org.lodder.subtools.multisubdownloader.Messages.*;
import static org.lodder.subtools.sublibrary.Manager.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.tvdb.exception.TheTvdbException;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbEpisode;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbSerie;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderInitException;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheTvdbAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheTvdbAdapter.class);
    private static final String PROVIDER_NAME = "TVDB";
    private static TheTvdbAdapter instance;
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;
    private final LazySupplier<TheTvdbApi> jtvapi;

    private TheTvdbAdapter(Manager manager, UserInteractionHandler userInteractionHandler) {
        this.manager = manager;
        this.userInteractionHandler = userInteractionHandler;
        this.jtvapi = new LazySupplier<>(() -> {
            try {
                return new TheTvdbApi(manager, "A1720D2DDFDCE82D");
            } catch (Exception e) {
                throw new SubtitlesProviderInitException(PROVIDER_NAME, e);
            }
        });
    }

    private TheTvdbApi getApi() {
        return jtvapi.get();
    }

    public Optional<TheTvdbSerie> getSerie(String serieName) {
        String encodedSerieName = URLEncoder.encode(serieName.toLowerCase().replace(" ", "-"), StandardCharsets.UTF_8);

        CacheKey cache = manager.getCache(CacheType.DISK, "$PROVIDER_NAME-tvdbSerie-$encodedSerieName");
        if (cache.isPresent() && (!cache.isTemporaryObject() || !cache.isExpiredTemporary())) {
            return cache.getOptional();
        }

        Optional<TheTvdbSerie> tvdbSerie;
        List<TheTvdbSerie> serieIds;
        try {
            serieIds = getApi().getSeries(encodedSerieName, null);
        } catch (TheTvdbException e) {
            serieIds = List.of();
        }
        if (serieIds.isEmpty()) {
            tvdbSerie = Optional.empty();
        } else if (!userInteractionHandler.settings.optionsConfirmProviderMapping && serieIds.size() == 1) {
            tvdbSerie = Optional.of(serieIds.first);
        } else {
            String formattedSerieName = serieName.replaceAll("[^A-Za-z]", "");
            Comparator<TheTvdbSerie> comparator = Comparator
                .comparing((TheTvdbSerie s) -> formattedSerieName.equalsIgnoreCase(
                        s.serieName.replaceAll("[^A-Za-z]", "")),
                    Comparator.reverseOrder())
                .thenComparing(TheTvdbSerie::getFirstAired, Comparator.reverseOrder());
            try {
                tvdbSerie = userInteractionHandler.selectFromList(
                    serieIds.stream().sorted(comparator).toList(),
                    getText("Prompter.SelectTvdbMatchForSerie", serieName),
                    PROVIDER_NAME,
                    s -> "${s.serieName} (${s.firstAired})");
                if (tvdbSerie.isEmpty()) {
                    LOGGER.error("Unknown serie name in tvdb: $serieName");
                    tvdbSerie = askUserToEnterTvdbId(serieName)
                        .mapToObj(tvdbId -> api.getSerie(tvdbId, null).orElse(null));
                }
            } catch (TheTvdbException e) {
                tvdbSerie = Optional.empty();
            }
        }
        if (tvdbSerie.isEmpty()) {
            cache.store(
                value:Value.ofOptional(tvdbSerie),
                timeToLive:cache.getTemporaryTimeToLive().map(v -> v * 2).orElseGet(() -> 1 day),
                storeAsTempValue:true,
                storeTempNullValue:true);
        } else {
            cache.store(Value.ofOptional(tvdbSerie));
            manager.getCache(CacheType.DISK, "$PROVIDER_NAME-serieId-$encodedSerieName")
                .store(
                    value:Value.ofOptional(
                        tvdbSerie.map(tvdbS -> new SerieMapping(serieName, String.valueOf(tvdbS.id), tvdbS.serieName))),
                    storeTempNullValue:true);
        }
        return tvdbSerie;
    }

    public Optional<TheTvdbEpisode> getEpisode(int tvdbId, int season, int episode) {
        return manager.getCache(CacheType.DISK, "%s-episode-%s-%s-%s".formatted(PROVIDER_NAME, tvdbId, season, episode))
            .getOptional(
                supplier:() -> {
                    try {
                        return getApi().getEpisode(tvdbId, season, episode, Language.ENGLISH);
                    } catch (TheTvdbException e) {
                        LOGGER.error(
                            "API $PROVIDER_NAME getEpisode for serie id [$tvdbId] %s (${e.getMessage()})".formatted(
                                TvRelease.formatSeasonEpisode(season, episode)), e);
                        return Optional.empty();
                    }
                },
                storeTempNullValue:true);
    }

    public static synchronized TheTvdbAdapter getInstance(Manager manager,
        UserInteractionHandler userInteractionHandler) {
        if (instance == null) {
            instance = new TheTvdbAdapter(manager, userInteractionHandler);
        }
        return instance;
    }

    private OptionalInt askUserToEnterTvdbId(String showName) {
        return userInteractionHandler.enter(Messages.getText("InputPanel.EnterTvdbId", showName))
            .map(tvdbidString -> {
                try {
                    return OptionalInt.of(Integer.parseInt(tvdbidString));
                } catch (NumberFormatException e) {
                    LOGGER.error(Messages.getText("InputPanel.invalid.tvdbid", tvdbidString));
                    return askUserToEnterTvdbId(showName);
                }
            }).orElseGet(OptionalInt::empty);
    }
}
