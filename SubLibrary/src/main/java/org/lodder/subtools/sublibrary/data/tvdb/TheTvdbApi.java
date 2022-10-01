package org.lodder.subtools.sublibrary.data.tvdb;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.UserInteractionHandler;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.tvdb.exception.TheTvdbException;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbEpisode;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbSerie;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pivovarit.function.ThrowingSupplier;
import com.uwetrottmann.thetvdb.TheTvdb;
import com.uwetrottmann.thetvdb.entities.Episode;
import com.uwetrottmann.thetvdb.entities.EpisodesResponse;
import com.uwetrottmann.thetvdb.entities.Series;
import com.uwetrottmann.thetvdb.entities.SeriesResponse;
import com.uwetrottmann.thetvdb.entities.SeriesResultsResponse;

import lombok.experimental.ExtensionMethod;
import retrofit2.Response;

@ExtensionMethod({ OptionalExtension.class })
class TheTvdbApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheTvdbApi.class);
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;
    private final TheTvdb theTvdb;

    public TheTvdbApi(Manager manager, UserInteractionHandler userInteractionHandler, String apikey) {
        this.manager = manager;
        this.userInteractionHandler = userInteractionHandler;
        this.theTvdb = new TheTvdb(apikey);
    }

    public OptionalInt getSerieId(String seriename, Language language) throws TheTvdbException {
        return getSerieId(seriename, language, null);
    }

    public OptionalInt getSerieId(String seriename, Language language, ThrowingSupplier<Optional<Integer>, TheTvdbException> noResultCallback)
            throws TheTvdbException {
        String encodedSerieName = URLEncoder.encode(seriename.toLowerCase().replace(" ", "-"), StandardCharsets.UTF_8);
        return manager.getValueBuilder()
                .key("TVDB-SerieId-%s-%s".formatted(encodedSerieName, language))
                .cacheType(CacheType.DISK)
                .optionalSupplier(() -> {
                    try {
                        Response<SeriesResultsResponse> response =
                                theTvdb.search().series(encodedSerieName, null, null, null, language == null ? null : language.getLangCode())
                                        .execute();
                        if (response.isSuccessful()) {
                            List<Integer> results = response.body().data.stream().map(serie -> serie.id).toList();
                            Optional<Integer> selectedTvdbId = selectTvdbIdForSerieName(results, encodedSerieName);
                            if (selectedTvdbId.isPresent()) {
                                return selectedTvdbId;
                            }
                        }
                        if (noResultCallback != null) {
                            return noResultCallback.get();
                        }
                        return Optional.empty();
                    } catch (IOException e) {
                        if (noResultCallback != null) {
                            return noResultCallback.get();
                        }
                        throw new TheTvdbException(e);
                    }
                }).getOptional().mapToInt(i -> i);
    }

    private Optional<Integer> selectTvdbIdForSerieName(List<Integer> options, String serieName) {
        if (options.isEmpty()) {
            return Optional.empty();
        } else if (!userInteractionHandler.getSettings().isOptionsConfirmProviderMapping() && options.size() == 1) {
            return Optional.of(options.get(0));
        } else {
            return userInteractionHandler.selectFromList(options,
                    Messages.getString("Prompter.SelectTvdbMatchForSerie").formatted(serieName),
                    "tvdb", String::valueOf);
        }
    }

    public Optional<TheTvdbSerie> getSerie(int tvdbId, Language language) throws TheTvdbException {
        return manager.getValueBuilder()
                .key("TVDB-Serie-%s-%s".formatted(tvdbId, language))
                .cacheType(CacheType.DISK)
                .optionalSupplier(() -> {
                    try {
                        if (tvdbId != 0) {
                            Response<SeriesResponse> response =
                                    theTvdb.series().series(tvdbId, language == null ? null : language.getLangCode()).execute();
                            if (response.isSuccessful()) {
                                return Optional.of(seriesToTVDBSerie(response.body().data, language));
                            }
                        } else {
                            LOGGER.warn("TVDB ID is 0! please fix");
                        }
                        return Optional.empty();
                    } catch (IOException e) {
                        throw new TheTvdbException(e);
                    }
                }).getOptional();
    }

    public List<TheTvdbEpisode> getAllEpisodes(int tvdbId, Language language) throws TheTvdbException {
        return manager.getValueBuilder()
                .key("TVDB-episodes-%s-%s".formatted(tvdbId, language))
                .cacheType(CacheType.MEMORY)
                .collectionSupplier(TheTvdbEpisode.class, () -> {
                    try {
                        if (tvdbId != 0) {
                            Response<EpisodesResponse> response =
                                    theTvdb.series().episodes(tvdbId, 1, language == null ? null : language.getLangCode()).execute();
                            if (response.isSuccessful()) {
                                return response.body().data.stream()
                                        .map(episode -> episodeToTVDBEpisode(episode, language))
                                        .collect(Collectors.toList());
                            }
                        } else {
                            LOGGER.warn("TVDB ID is 0! please fix");
                        }
                        return List.of();
                    } catch (IOException e) {
                        throw new TheTvdbException(e);
                    }
                }).getCollection();
    }

    public Optional<TheTvdbEpisode> getEpisode(int tvdbId, int season, int episode, Language language) throws TheTvdbException {
        return manager.getValueBuilder()
                .key("TVDB-episode-%s-%s-%s-%s".formatted(tvdbId, season, episode, language))
                .cacheType(CacheType.DISK)
                .optionalSupplier(() -> {
                    try {
                        Response<EpisodesResponse> response =
                                theTvdb.series().episodesQuery(tvdbId, null, season, episode, null, null, null, null, null,
                                        language == null ? null : language.getLangCode()).execute();
                        if (response.isSuccessful()) {
                            return response.body().data.stream().map(serie -> episodeToTVDBEpisode(serie, language)).findFirst();
                        }
                        throw new TheTvdbException(response.errorBody().string());
                    } catch (IOException e) {
                        throw new TheTvdbException(e);
                    }
                }).getOptional();
    }

    private TheTvdbSerie seriesToTVDBSerie(Series serie, Language lang) {
        TheTvdbSerie TheTVDBSerie = new TheTvdbSerie();

        TheTVDBSerie.setId(toString(serie.id));
        TheTVDBSerie.setAirsDayOfWeek(serie.airsDayOfWeek);
        TheTVDBSerie.setAirsTime(serie.airsTime);
        TheTVDBSerie.setContentRating(serie.rating);
        TheTVDBSerie.setFirstAired(serie.firstAired);
        TheTVDBSerie.setGenres(serie.genre);
        TheTVDBSerie.setImdbId(serie.imdbId);
        TheTVDBSerie.setLanguage(lang);
        TheTVDBSerie.setNetwork(serie.network);
        TheTVDBSerie.setOverview(serie.overview);
        TheTVDBSerie.setRating(serie.rating);
        TheTVDBSerie.setRuntime(serie.runtime);
        TheTVDBSerie.setSerieId(toString(serie.id));
        TheTVDBSerie.setSerieName(serie.seriesName);
        TheTVDBSerie.setStatus(serie.status);

        return TheTVDBSerie;
    }

    private TheTvdbEpisode episodeToTVDBEpisode(Episode episode, Language lang) {
        TheTvdbEpisode tvdbEpisode = new TheTvdbEpisode();

        tvdbEpisode.setId(toString(episode.id));
        tvdbEpisode.setDvdEpisodeNumber(toString(episode.dvdEpisodeNumber));
        tvdbEpisode.setDvdSeason(toString(episode.dvdSeason));
        tvdbEpisode.setEpisodeName(episode.episodeName);
        tvdbEpisode.setEpisodeNumber(episode.airedEpisodeNumber);
        tvdbEpisode.setFirstAired(episode.firstAired);
        tvdbEpisode.setLanguage(lang);
        tvdbEpisode.setOverview(episode.language.overview);
        tvdbEpisode.setSeasonNumber(episode.airedSeason);
        tvdbEpisode.setAbsoluteNumber(toString(episode.absoluteNumber));
        tvdbEpisode.setLastUpdated(toString(episode.lastUpdated));
        tvdbEpisode.setSeasonId(toString(episode.airedSeasonID));
        tvdbEpisode.setAirsAfterSeason(0);
        tvdbEpisode.setAirsBeforeEpisode(0);

        return tvdbEpisode;
    }

    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }

}
