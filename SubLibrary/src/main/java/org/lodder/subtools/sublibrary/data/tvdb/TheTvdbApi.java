package org.lodder.subtools.sublibrary.data.tvdb;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvdb.exception.TheTvdbException;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbEpisode;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbSerie;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uwetrottmann.thetvdb.TheTvdb;
import com.uwetrottmann.thetvdb.entities.Episode;
import com.uwetrottmann.thetvdb.entities.EpisodesResponse;
import com.uwetrottmann.thetvdb.entities.Series;
import com.uwetrottmann.thetvdb.entities.SeriesResponse;
import com.uwetrottmann.thetvdb.entities.SeriesResultsResponse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import retrofit2.Response;

@ExtensionMethod({ OptionalExtension.class })
public class TheTvdbApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheTvdbApi.class);
    @Getter(value = AccessLevel.PRIVATE)
    private final Manager manager;
    private final TheTvdb theTvdb;

    public TheTvdbApi(Manager manager, String apikey) {
        this.manager = manager;
        this.theTvdb = new TheTvdb(apikey);
    }

    public List<TheTvdbSerie> getSeries(String seriename, Language language) throws TheTvdbException {
        return getManager().valueBuilder()
                .memoryCache()
                .key("%s-series-%s-%s".formatted("TVDB", seriename, language))
                .collectionSupplier(TheTvdbSerie.class, () -> {
                    String encodedSerieName = URLEncoder.encode(seriename.toLowerCase().replace(" ", "-"), StandardCharsets.UTF_8);
                    try {
                        Response<SeriesResultsResponse> response =
                                theTvdb.search().series(encodedSerieName, null, null, null, language == null ? null : language.getLangCode())
                                        .execute();
                        if (response.isSuccessful()) {
                            return response.body().data.stream().map(series -> seriesToTVDBSerie(series, language)).toList();
                        }
                        return List.of();
                    } catch (IOException e) {
                        throw new TheTvdbException(e);
                    }
                }).getCollection();
    }

    public Optional<TheTvdbSerie> getSerie(int tvdbId, Language language) throws TheTvdbException {
        try {
            Response<SeriesResponse> response =
                    theTvdb.series()
                            .series(tvdbId, language == null ? null : language.getLangCode())
                            .execute();
            if (response.isSuccessful()) {
                return Optional.of(seriesToTVDBSerie(response.body().data, language));
            }
            return Optional.empty();
        } catch (IOException e) {
            throw new TheTvdbException(e);
        }
    }

    public Optional<TheTvdbEpisode> getEpisode(int tvdbId, int season, int episode, Language language) throws TheTvdbException {
        return getManager().valueBuilder()
                .memoryCache()
                .key("%s-episode-%s-%s-%s-%s".formatted("TVDB", tvdbId, season, episode, language))
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

        TheTVDBSerie.setId(serie.id);
        TheTVDBSerie.setAirsDayOfWeek(serie.airsDayOfWeek);
        TheTVDBSerie.setAirsTime(serie.airsTime);
        TheTVDBSerie.setContentRating(serie.rating);
        TheTVDBSerie.setFirstAired(serie.firstAired);
        TheTVDBSerie.setGenres(serie.genre);
        TheTVDBSerie.setImdbId(serie.imdbId);
        TheTVDBSerie.setLanguage(lang);
        TheTVDBSerie.setNetwork(serie.network);
        // TheTVDBSerie.setOverview(serie.overview);
        TheTVDBSerie.setRating(serie.rating);
        TheTVDBSerie.setRuntime(serie.runtime);
        // TheTVDBSerie.setSerieId(toString(serie.id));
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
        // tvdbEpisode.setOverview(episode.language.overview);
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
