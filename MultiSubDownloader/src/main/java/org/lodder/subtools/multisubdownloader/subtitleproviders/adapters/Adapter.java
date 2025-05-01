package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import static manifold.science.util.UnitConstants.*;
import static org.lodder.subtools.multisubdownloader.Messages.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import lombok.experimental.ExtensionMethod;
import manifold.ext.props.rt.api.val;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesHasher;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager.CacheKey;
import org.lodder.subtools.sublibrary.Manager.Value;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <T> type of the subtitle objects returned by the api
 * @param <S> type of the ProviderSerieId
 * @param <X> type of the exception thrown by the api
 */
@ExtensionMethod({Files.class})
public interface Adapter<T, S extends ProviderSerieId, X extends Exception> extends SubtitleProvider {
    Logger LOGGER = LoggerFactory.getLogger(Adapter.class);

    default UserInteractionSettingsIntf getUserInteractionSettings() {
        return getUserInteractionHandler().settings;
    }

    UserInteractionHandler getUserInteractionHandler();

    @Override
    default Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language) {
        Set<T> subtitles = new HashSet<>();
        if (StringUtils.isNotBlank(movieRelease.fileName)) {
            Path file = movieRelease.getPath().resolve(movieRelease.fileName);
            if (file.exists()) {
                try {
                    subtitles.addAll(searchMovieSubtitlesWithHash(OpenSubtitlesHasher.computeHash(file), language));
                } catch (IOException e) {
                    LOGGER.error("Error calculating file hash", e);
                } catch (Exception e) {
                    LOGGER.error("API %s searchSubtitles using file hash for movie [%s] (%s)".formatted(
                        subtitleSource.name, movieRelease.name, e.getMessage()), e);
                }
            }
        }
        if (movieRelease.imdbId != null) {
            try {
                subtitles.addAll(searchMovieSubtitlesWithId(movieRelease.imdbId, language));
            } catch (Exception e) {
                LOGGER.error("API %s searchSubtitles using imdbid [%s] for movie [%s] (%s)".formatted(
                    subtitleSource.name, movieRelease.imdbId, movieRelease.name, e.getMessage()), e);
            }
        }
        if (subtitles.isEmpty()) {
            try {
                subtitles.addAll(searchMovieSubtitlesWithName(movieRelease.name, movieRelease.year, language));
            } catch (Exception e) {
                LOGGER.error("API %s searchSubtitles using title for movie [%s] (%s)".formatted(subtitleSource.name,
                    movieRelease.name, e.getMessage()), e);
            }
        }
        return convertToSubtitles(movieRelease, subtitles, language);
    }

    Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<T> subtitles, Language language);

    Collection<T> searchMovieSubtitlesWithHash(String hash, Language language) throws X;

    Collection<T> searchMovieSubtitlesWithId(int tvdbId, Language language) throws X;

    Collection<T> searchMovieSubtitlesWithName(String name, @Nullable Integer year, Language language) throws X;

    @Override
    default Set<Subtitle> searchSubtitles(TvRelease tvRelease, Language language) {
        try {
            return convertToSubtitles(tvRelease, searchSerieSubtitles(tvRelease, language), language);
        } catch (Exception e) {
            String displayName = StringUtils.defaultIfBlank(tvRelease.originalName, tvRelease.name);
            LOGGER.error("API %s searchSubtitles for serie [%s] (%s)".formatted(subtitleSource.name,
                TvRelease.formatName(displayName, tvRelease.season, tvRelease.firstEpisode), e.getMessage()), e);
            return Set.of();
        }
    }

    Collection<T> searchSerieSubtitles(TvRelease tvRelease, Language language) throws X;

    Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<T> subtitles, Language language);

    List<S> getSortedProviderSerieIds(@Nullable Integer tvdbId, String serieName, int season) throws X;

    @Override
    default Optional<SerieMapping> getProviderSerieId(TvRelease tvRelease) throws X {
        if (StringUtils.isNotBlank(tvRelease.customName)) {
            return getProviderSerieId(tvRelease, TvRelease::getOriginalName, TvRelease::getCustomName);
        } else {
            Optional<SerieMapping> providerSerieId = getProviderSerieId(tvRelease, TvRelease::getOriginalName);
            return providerSerieId.isPresent() ? providerSerieId : getProviderSerieId(tvRelease, TvRelease::getName);
        }
    }

    default Optional<SerieMapping> getProviderSerieId(TvRelease tvRelease, Function<TvRelease, String> nameFunction)
        throws X {
        return getProviderSerieId(tvRelease, nameFunction, nameFunction);
    }

    default Optional<SerieMapping> getProviderSerieId(TvRelease tvRelease, Function<TvRelease, String> nameFunction,
        Function<TvRelease, String> customNameFunction) throws X {
        return getProviderSerieId(nameFunction.apply(tvRelease), customNameFunction.apply(tvRelease),
            tvRelease.displayName, tvRelease.season, tvRelease.tvdbId);
    }

    default Optional<SerieMapping> getProviderSerieId(String serieName, String serieNameToSearchFor, String displayName,
        int season, Integer tvdbId) throws X {

        LazySupplier<CacheKey> tvdbIdCacheFunction = new LazySupplier<>(() -> manager.getCache(CacheType.DISK,
            "%s-serieName-tvdbId:%s-%s".formatted(providerName, tvdbId, useSeasonForSerieId ? season : -1)));
        if (tvdbId != null) {
            CacheKey tvdbIdCache = tvdbIdCacheFunction.get();
            if (tvdbIdCache.isPresent()) {
                // if value using the tvdbId is present, return it
                return tvdbIdCache.getOptional();
            }
        }
        if (StringUtils.isBlank(serieNameToSearchFor)) {
            return Optional.empty();
        }

        int seasonToUse = useSeasonForSerieId ? season : 0;
        CacheKey serieNameCache = manager.getCache(CacheType.DISK,
            "%s-serieName-name:%s-%s".formatted(providerName, serieName.toLowerCase(), seasonToUse));
        if (StringUtils.equals(serieNameToSearchFor, serieName) && serieNameCache.isPresent()) {
            if (serieNameCache.isTemporaryObject()) {
                if (!serieNameCache.isExpiredTemporary()) {
                    return Optional.empty();
                }
            } else {
                Optional<SerieMapping> serieMapping = serieNameCache.getOptional();
                if (tvdbId != null) {
                    serieMapping.map(Value::of).ifPresent(tvdbIdCacheFunction.get()::store);
                }
                return serieMapping;
            }
        }

        List<S> providerSerieIds = getSortedProviderSerieIds(tvdbId, serieNameToSearchFor, seasonToUse);
        if (providerSerieIds.isEmpty()) {
            // If no provider serie id's could be found, store a temporary null value with expiration time of 1 day
            // (so the provider isn't contacted every time this method is being called).
            // If a temporary expired value was already found, persist the null value with a doubled expiration time.
            serieNameCache.store(
                value:Value.of(new SerieMapping(serieName, null, null, seasonToUse)),
                timeToLive:serieNameCache.getTemporaryTimeToLive().map(v -> v * 2).orElse(1 day),
                storeAsTempValue:true,
                storeTempNullValue:true);
            return Optional.empty();
        }

        SerieMapping serieMapping;
        if (!getUserInteractionSettings().optionsConfirmProviderMapping && providerSerieIds.size() == 1) {
            serieMapping =
                new SerieMapping(serieName, providerSerieIds.first.id, providerSerieIds.first.name, seasonToUse);
        } else {
            CacheKey previousResultsCache = manager.getCache(CacheType.MEMORY,
                "%s-serieName-prev-results:%s-%s".formatted(providerName, displayName.toLowerCase(), seasonToUse));

            boolean previousResultsPresent = previousResultsCache.isPresent();
            Optional<S> uriForSerie;
            // Check if the previous results were the same for the service. If so, don't ask the user to select again
            if (previousResultsPresent && providerSerieIds.equals(previousResultsCache.getCollection(null))) {
                uriForSerie = Optional.empty();
            } else {
                // let the user select the correct provider serie id
                uriForSerie = getUserInteractionHandler().selectFromList(
                    providerSerieIds,
                    useSeasonForSerieId ?
                        getText("SelectDialog.SelectSerieNameForNameWithSeason", displayName, seasonToUse) :
                        getText("SelectDialog.SelectSerieNameForName", displayName),
                    providerName,
                    this::providerSerieIdToDisplayString);
            }
            if (uriForSerie.isEmpty()) {
                if (serieNameToSearchFor.equals(serieName)) {
                    // if no provider serie id was selected, store a temporary null value with expiration time of 1 day,
                    // or the doubled previously temporary value (if present)
                    serieNameCache.store(
                        value:Value.of(new SerieMapping(serieNameToSearchFor, null, null, seasonToUse)),
                        timeToLive:serieNameCache.getTemporaryTimeToLive().map(v -> v * 2).orElse(1 day),
                        storeAsTempValue:true,
                        storeTempNullValue:true);
                    previousResultsCache.store(Value.ofCollection(providerSerieIds));
                }
                return Optional.empty();
            }
            // create a serieMapping for the selected value
            serieMapping = new SerieMapping(serieName, uriForSerie.get().id, uriForSerie.get().name, seasonToUse);
        }
        if (tvdbId != null) {
            tvdbIdCacheFunction.get().store(Value.of(serieMapping));
        } else {
            serieNameCache.store(Value.of(serieMapping));
        }
        return Optional.of(serieMapping);
    }

    @val boolean useSeasonForSerieId;

    String providerSerieIdToDisplayString(S providerSerieId);
}
