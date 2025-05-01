package org.lodder.subtools.sublibrary.data.tvdb;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import com.uwetrottmann.thetvdb.TheTvdb;
import com.uwetrottmann.thetvdb.entities.Episode;
import com.uwetrottmann.thetvdb.entities.EpisodesResponse;
import com.uwetrottmann.thetvdb.entities.Series;
import com.uwetrottmann.thetvdb.entities.SeriesResponse;
import com.uwetrottmann.thetvdb.entities.SeriesResultsResponse;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.tvdb.exception.TheTvdbException;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbEpisode;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbSerie;
import retrofit2.Response;

public class TheTvdbApi {

    private final Manager manager;
    private final TheTvdb theTvdb;

    public TheTvdbApi(Manager manager, String apikey) {
        this.manager = manager;
        this.theTvdb = new TheTvdb(apikey);
    }

    public List<TheTvdbSerie> getSeries(String serieName, Language language) throws TheTvdbException {
        return manager.getCache(CacheType.MEMORY, "TVDB-series-$serieName-$language")
            .getCollection(() -> {
                String encodedSerieName =
                    URLEncoder.encode(serieName.toLowerCase().replace(" ", "-"), StandardCharsets.UTF_8);
                try {
                    Response<SeriesResultsResponse> response =
                        theTvdb.search()
                            .series(encodedSerieName, null, null, null,
                                language == null ? null : language.langCode)
                            .execute();
                    if (response.isSuccessful()) {
                        return response.body().data.stream()
                            .map(series -> seriesToTVDBSerie(series, language))
                            .toList();
                    }
                    return List.of();
                } catch (IOException e) {
                    throw new TheTvdbException(e);
                }
            });
    }

    public Optional<TheTvdbSerie> getSerie(int tvdbId, Language language) throws TheTvdbException {
        try {
            Response<SeriesResponse> response =
                theTvdb.series()
                    .series(tvdbId, language == null ? null : language.langCode)
                    .execute();
            if (response.isSuccessful()) {
                return Optional.of(seriesToTVDBSerie(response.body().data, language));
            }
            return Optional.empty();
        } catch (IOException e) {
            throw new TheTvdbException(e);
        }
    }

    public Optional<TheTvdbEpisode> getEpisode(int tvdbId, int season, int episode, Language language)
        throws TheTvdbException {
        return manager.getCache(CacheType.MEMORY, "TVDB-episode-$tvdbId-$season-$episode-$language")
            .getOptional(() -> {
                try {
                    Response<EpisodesResponse> response =
                        theTvdb.series()
                            .episodesQuery(tvdbId, null, season, episode, null, null, null, null, null,
                                language == null ? null : language.langCode)
                            .execute();
                    if (response.isSuccessful()) {
                        if (response.body().data == null) {
                            return Optional.empty();
                        }
                        return response.body().data.stream()
                            .map(serie -> episodeToTVDBEpisode(serie, language))
                            .findFirst();
                    }
                    throw new TheTvdbException(response.errorBody().string());
                } catch (IOException e) {
                    throw new TheTvdbException(e);
                }
            });
    }

    private TheTvdbSerie seriesToTVDBSerie(Series serie, Language lang) {
        TheTvdbSerie theTVDBSerie = new TheTvdbSerie();

        theTVDBSerie.id = serie.id;
        theTVDBSerie.airsDayOfWeek = serie.airsDayOfWeek;
        theTVDBSerie.airsTime = serie.airsTime;
        theTVDBSerie.contentRating = serie.rating;
        theTVDBSerie.firstAired = serie.firstAired;
        theTVDBSerie.genres = serie.genre;
        theTVDBSerie.imdbId = serie.imdbId;
        theTVDBSerie.language = lang;
        theTVDBSerie.network = serie.network;
        // theTVDBSerie.overview = serie.overview;
        theTVDBSerie.rating = serie.rating;
        theTVDBSerie.runtime = serie.runtime;
        // theTVDBSerie.serieId = toString(serie.id);
        theTVDBSerie.serieName = serie.seriesName;
        theTVDBSerie.status = serie.status;

        return theTVDBSerie;
    }

    private TheTvdbEpisode episodeToTVDBEpisode(Episode episode, Language lang) {
        TheTvdbEpisode tvdbEpisode = new TheTvdbEpisode();

        tvdbEpisode.id = toString(episode.id);
        tvdbEpisode.dvdEpisodeNumber = toString(episode.dvdEpisodeNumber);
        tvdbEpisode.dvdSeason = toString(episode.dvdSeason);
        tvdbEpisode.episodeName = episode.episodeName;
        tvdbEpisode.episodeNumber = episode.airedEpisodeNumber;
        tvdbEpisode.firstAired = episode.firstAired;
        tvdbEpisode.language = lang;
        // tvdbEpisode.setOverview(episode.language.overview);
        tvdbEpisode.seasonNumber = episode.airedSeason;
        tvdbEpisode.absoluteNumber = toString(episode.absoluteNumber);
        tvdbEpisode.lastUpdated = toString(episode.lastUpdated);
        tvdbEpisode.seasonId = toString(episode.airedSeasonID);
        tvdbEpisode.airsAfterSeason = 0;
        tvdbEpisode.airsBeforeEpisode = 0;

        return tvdbEpisode;
    }

    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }

}
