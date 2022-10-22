package org.lodder.subtools.multisubdownloader.subtitleproviders.adapters;

import static org.lodder.subtools.sublibrary.util.OptionalExtension.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesHasher;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.ValueBuilderIsPresentIntf;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @param <T> type of the subtitle objects returned by the api
 * @param <S> type of the ProviderSerieId
 * @param <X> type of the exception thrown by the api
 */
public interface Adapter<T, S extends ProviderSerieId, X extends Exception> extends SubtitleProvider {
    Logger LOGGER = LoggerFactory.getLogger(Adapter.class);

    Manager getManager();

    default UserInteractionSettingsIntf getUserInteractionSettings() {
        return getUserInteractionHandler().getSettings();
    }

    UserInteractionHandler getUserInteractionHandler();

    @Override
    default Set<Subtitle> searchSubtitles(MovieRelease movieRelease, Language language) {
        Set<T> subtitles = new HashSet<>();
        if (StringUtils.isNotBlank(movieRelease.getFileName())) {
            File file = new File(movieRelease.getPath(), movieRelease.getFileName());
            if (file.exists()) {
                try {
                    searchMovieSubtitlesWithHash(OpenSubtitlesHasher.computeHash(file), language).forEach(subtitles::add);
                } catch (IOException e) {
                    LOGGER.error("Error calculating file hash", e);
                } catch (Exception e) {
                    LOGGER.error("API %s searchSubtitles using file hash for movie [%s] (%s)".formatted(getSubtitleSource().getName(),
                            movieRelease.getName(), e.getMessage()), e);
                }
            }
        }
        movieRelease.getImdbId().ifPresent(imdbId -> {
            try {
                searchMovieSubtitlesWithId(imdbId, language).forEach(subtitles::add);
            } catch (Exception e) {
                LOGGER.error("API %s searchSubtitles using imdbid [%s] for movie [%s] (%s)".formatted(getSubtitleSource().getName(),
                        imdbId, movieRelease.getName(), e.getMessage()), e);
            }
        });
        if (subtitles.isEmpty()) {
            try {
                searchMovieSubtitlesWithName(movieRelease.getName(), movieRelease.getYear(), language).forEach(subtitles::add);
            } catch (Exception e) {
                LOGGER.error("API %s searchSubtitles using title for movie [%s] (%s)".formatted(getSubtitleSource().getName(),
                        movieRelease.getName(), movieRelease.getName(), e.getMessage()), e);
            }
        }
        return convertToSubtitles(movieRelease, subtitles, language);
    }

    Set<Subtitle> convertToSubtitles(MovieRelease movieRelease, Set<T> subtitles, Language language);

    Collection<T> searchMovieSubtitlesWithHash(String hash, Language language) throws X;

    Collection<T> searchMovieSubtitlesWithId(int tvdbId, Language language) throws X;

    Collection<T> searchMovieSubtitlesWithName(String name, int year, Language language) throws X;

    @Override
    default Set<Subtitle> searchSubtitles(TvRelease tvRelease, Language language) {
        try {
            return convertToSubtitles(tvRelease, searchSerieSubtitles(tvRelease, language), language);
        } catch (Exception e) {
            String displayName = StringUtils.isNotBlank(tvRelease.getOriginalName()) ? tvRelease.getOriginalName() : tvRelease.getName();
            LOGGER.error("API %s searchSubtitles for serie [%s] (%s)".formatted(getSubtitleSource().getName(),
                    TvRelease.formatName(displayName, tvRelease.getSeason(), tvRelease.getFirstEpisodeNumber()), e.getMessage()), e);
            return Set.of();
        }
    }

    Collection<T> searchSerieSubtitles(TvRelease tvRelease, Language language) throws X;

    Set<Subtitle> convertToSubtitles(TvRelease tvRelease, Collection<T> subtitles, Language language);

    List<S> getSortedProviderSerieIds(String serieName, int season) throws X;

    String getProviderName();

    default Optional<SerieMapping> getProviderSerieId(String serieName, String displayName, int season, OptionalInt tvdbIdOptional) throws X {
        Supplier<ValueBuilderIsPresentIntf<Serializable>> tvdbIdValueBuilder =
                () -> mapToObj(tvdbIdOptional, tvdbId -> getManager().valueBuilder().cacheType(CacheType.DISK)
                        .key("%s-serieName-tvdbId:%s-%s".formatted(getProviderName(), tvdbId,
                                useSeasonForSerieId() ? season : -1))).orElseThrow();
        if (tvdbIdOptional.isPresent() && tvdbIdValueBuilder.get().isPresent()) {
            // if value using the tvdbId is present, return it
            return tvdbIdValueBuilder.get().returnType(SerieMapping.class).getOptional();
        }
        if (StringUtils.isBlank(serieName)) {
            return Optional.empty();
        }
        ValueBuilderIsPresentIntf<Serializable> serieNameValueBuilder = getManager().valueBuilder()
                .cacheType(CacheType.DISK)
                .key("%s-serieName-name:%s-%s".formatted(getProviderName(), serieName.toLowerCase(), useSeasonForSerieId() ? season : 0));

        if (serieNameValueBuilder.isPresent()) {
            boolean returnValue;
            Optional<SerieMapping> value;
            if (serieNameValueBuilder.isTemporaryObject()) {
                returnValue = !serieNameValueBuilder.isExpiredTemporary();
                value = Optional.empty();
            } else {
                value = serieNameValueBuilder.returnType(SerieMapping.class).getOptional();
                returnValue = true;
            }
            if (returnValue) {
                // if value using the name is present, return it
                // if tvdbId is known, also persist the value using the tvdbId
                return ifPresentDo(value,
                        providerSerieName -> tvdbIdOptional.ifPresent(tvdbId -> tvdbIdValueBuilder.get().value(providerSerieName).store()));
            }
        }

        List<S> providerSerieIds = getSortedProviderSerieIds(serieName, season);
        if (providerSerieIds.isEmpty()) {
            // if no provider serie id's could be found, store a temporary null value with expiration time of 1 day
            // (so the provider isn't contacted every time this method is being called)
            // If a temporary expired value was already found, persist the null value with a doubled expiration time
            serieNameValueBuilder
                    .value(new SerieMapping(serieName, null, null))
                    .storeTempNullValue()
                    .timeToLive(OptionalExtension
                            .map(serieNameValueBuilder.getTemporaryTimeToLive(), v -> v * 2)
                            .orElseGet(() -> TimeUnit.SECONDS.convert(1, TimeUnit.DAYS)))
                    .storeAsTempValue();
            return Optional.empty();
        } else if (!getUserInteractionSettings().isOptionsConfirmProviderMapping() && providerSerieIds.size() == 1) {
            return Optional.of(new SerieMapping(serieName, providerSerieIds.get(0).getId(), providerSerieIds.get(0).getName()));
        }
        ValueBuilderIsPresentIntf<Serializable> previousResultsValueBuilder = getManager().valueBuilder()
                .cacheType(CacheType.MEMORY)
                .key("%s-serieName-prev-results:%s-%s".formatted(getProviderName(), displayName.toLowerCase(), useSeasonForSerieId() ? season : 0));

        boolean previousResultsPresent = previousResultsValueBuilder.isPresent();
        Optional<S> uriForSerie;
        // Check if the previous results were the same for the service. If so, don't ask the user to select again
        if (previousResultsPresent
                && previousResultsValueBuilder.returnType((Class<List<S>>) null, (Class<S>) null).getCollection().equals(providerSerieIds)) {
            uriForSerie = Optional.empty();
        } else {
            // let the user select the correct provider serie id
            uriForSerie = getUserInteractionHandler().selectFromList(providerSerieIds,
                    useSeasonForSerieId() ? Messages.getString("SelectDialog.SelectSerieNameForNameWithSeason").formatted(displayName, season)
                            : Messages.getString("SelectDialog.SelectSerieNameForName").formatted(displayName),
                    getProviderName(),
                    this::providerSerieIdToDisplayString);
        }
        if (uriForSerie.isEmpty()) {
            // if no provider serie id was selected, store a temporary null value with expiration time of 1 day,
            // or the doubled previously temporary value (if present)
            serieNameValueBuilder
                    .value(new SerieMapping(serieName, null, null))
                    .storeTempNullValue()
                    .timeToLive(OptionalExtension
                            .map(serieNameValueBuilder.getTemporaryTimeToLive(), v -> v * 2)
                            .orElseGet(() -> TimeUnit.SECONDS.convert(1, TimeUnit.DAYS)))
                    .storeAsTempValue();
            previousResultsValueBuilder.collectionValue(providerSerieIds).store();
            return Optional.empty();
        }
        // create a serieMapping for the selected value
        SerieMapping serieMapping = new SerieMapping(serieName, uriForSerie.get().getId(), uriForSerie.get().getName());
        if (tvdbIdOptional.isPresent()) {
            tvdbIdValueBuilder.get().value(serieMapping).store();
        } else {
            serieNameValueBuilder.value(serieMapping).store();
        }
        return Optional.of(serieMapping);
    }

    boolean useSeasonForSerieId();

    String providerSerieIdToDisplayString(S providerSerieId);
}
