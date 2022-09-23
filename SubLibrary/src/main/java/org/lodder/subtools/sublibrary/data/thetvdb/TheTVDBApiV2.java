package org.lodder.subtools.sublibrary.data.thetvdb;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBEpisode;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uwetrottmann.thetvdb.TheTvdb;
import com.uwetrottmann.thetvdb.entities.Episode;
import com.uwetrottmann.thetvdb.entities.EpisodesResponse;
import com.uwetrottmann.thetvdb.entities.Series;
import com.uwetrottmann.thetvdb.entities.SeriesResponse;
import com.uwetrottmann.thetvdb.entities.SeriesResultsResponse;

import lombok.experimental.ExtensionMethod;
import retrofit2.Response;

@ExtensionMethod({ OptionalExtension.class })
public class TheTVDBApiV2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheTVDBApiV2.class);
    private final Manager manager;
    private final TheTvdb theTvdb;

    public TheTVDBApiV2(Manager manager, String apikey) throws TheTVDBException {
        this.manager = manager;
        this.theTvdb = new TheTvdb(apikey);
    }

    public OptionalInt getSerieId(String seriename, Language language) throws TheTVDBException {
        String encodedSerieName = URLEncoder.encode(seriename.toLowerCase().replace(" ", "-"), StandardCharsets.UTF_8);
        return manager.getValueBuilder()
                .key("TVDB-SerieId-" + encodedSerieName)
                .cacheType(CacheType.DISK)
                .optionalValueSupplier(() -> {
                    try {
                        Response<SeriesResultsResponse> response =
                                theTvdb.search().series(encodedSerieName, null, null, null, language == null ? null : language.getLangCode())
                                        .execute();
                        if (response.isSuccessful()) {
                            return response.body().data.stream().map(serie -> serie.id).findFirst();
                        }
                        return Optional.empty();
                    } catch (IOException e) {
                        throw new TheTVDBException(e);
                    }
                }).getOptional().mapToInt(i -> i);

    }

    public Optional<TheTVDBSerie> getSerie(int tvdbId, Language language) throws TheTVDBException {
        return manager.getValueBuilder()
                .key("TVDB-Serie-" + tvdbId + language)
                .cacheType(CacheType.DISK)
                .optionalValueSupplier(() -> {
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
                        throw new TheTVDBException(e);
                    }
                }).getOptional();
    }

    public List<TheTVDBEpisode> getAllEpisodes(int tvdbid, Language language) throws TheTVDBException {
        return manager.getValueBuilder()
                .key("TVDB-episodes-" + tvdbid + language)
                .cacheType(CacheType.MEMORY)
                .valueSupplier(() -> {
                    try {
                        if (tvdbid != 0) {
                            Response<EpisodesResponse> response =
                                    theTvdb.series().episodes(tvdbid, 1, language == null ? null : language.getLangCode()).execute();
                            if (response.isSuccessful()) {
                                return (ArrayList<TheTVDBEpisode>) response.body().data.stream()
                                        .map(episode -> episodeToTVDBEpisode(episode, language))
                                        .collect(Collectors.toList());
                            }
                        } else {
                            LOGGER.warn("TVDB ID is 0! please fix");
                        }
                        return new ArrayList<TheTVDBEpisode>();
                    } catch (IOException e) {
                        throw new TheTVDBException(e);
                    }
                }).get();
    }

    public Optional<TheTVDBEpisode> getEpisode(int tvdbid, int season, int episode, Language language) throws TheTVDBException {
        return manager.getValueBuilder()
                .key("TVDB-episode-%s-%s-%s-%s".formatted(tvdbid, season, episode, language))
                .cacheType(CacheType.DISK)
                .optionalValueSupplier(() -> {
                    try {
                        Response<EpisodesResponse> response =
                                theTvdb.series().episodesQuery(tvdbid, null, season, episode, null, null, null, null, null,
                                        language == null ? null : language.getLangCode()).execute();
                        if (response.isSuccessful()) {
                            return response.body().data.stream().map(serie -> episodeToTVDBEpisode(serie, language)).findFirst();
                        }
                        throw new TheTVDBException(response.errorBody().string());
                    } catch (IOException e) {
                        throw new TheTVDBException(e);
                    }
                }).getOptional();
    }

    private TheTVDBSerie seriesToTVDBSerie(Series serie, Language lang) {
        TheTVDBSerie TheTVDBSerie = new TheTVDBSerie();

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

    private TheTVDBEpisode episodeToTVDBEpisode(Episode episode, Language lang) {
        TheTVDBEpisode tvdbEpisode = new TheTVDBEpisode();

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
        if (value != null) {
            return value.toString();
        }
        return null;
    }

}
