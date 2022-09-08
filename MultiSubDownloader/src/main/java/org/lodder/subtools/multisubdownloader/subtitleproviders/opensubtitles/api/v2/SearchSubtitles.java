package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param.AiTranslatedEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param.ForeignPartsOnlyEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param.HearingImpairedEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param.MachineTranslatedEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param.MoviehashMatchEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param.OrderDirectionEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param.ParamIntf;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param.SearchSubtitlesEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param.TrustedSourcesEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param.TypeEnum;
import org.opensubtitles.api.SubtitlesApi;
import org.opensubtitles.invoker.ApiClient;
import org.opensubtitles.invoker.ApiException;
import org.opensubtitles.model.Subtitles200Response;

public class SearchSubtitles extends OpenSubtitlesExecuter {
    private final ApiClient apiClient;

    private AiTranslatedEnum aiTranslated;

    private Integer episode;

    private ForeignPartsOnlyEnum foreignPartsOnly;

    private HearingImpairedEnum hearingImpaired;

    private Integer id;

    private Integer imdbId;

    private String language;

    private MachineTranslatedEnum machineTranslated;

    private String moviehash;

    private MoviehashMatchEnum moviehashMatch;

    private SearchSubtitlesEnum orderBy;

    private OrderDirectionEnum orderDirection;

    private Integer page;

    private Integer parentFeatureId;

    private Integer parentImdbId;

    private Integer parentTmdbId;

    private String query;

    private Integer season;

    private Integer tmdbId;

    private TrustedSourcesEnum trustedSources;

    private TypeEnum type;

    private Integer userId;

    private Integer year;

    public SearchSubtitles(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public SearchSubtitles aiTranslated(AiTranslatedEnum aiTranslated) {
        this.aiTranslated = aiTranslated;
        return this;
    }

    public SearchSubtitles episode(int episode) {
        this.episode = episode;
        return this;
    }

    public SearchSubtitles foreignPartsOnly(ForeignPartsOnlyEnum foreignPartsOnly) {
        this.foreignPartsOnly = foreignPartsOnly;
        return this;
    }

    public SearchSubtitles hearingImpaired(HearingImpairedEnum hearingImpaired) {
        this.hearingImpaired = hearingImpaired;
        return this;
    }

    public SearchSubtitles id(int id) {
        this.id = id;
        return this;
    }

    public SearchSubtitles imdbId(int imdbId) {
        this.imdbId = imdbId;
        return this;
    }

    public SearchSubtitles addLanguage(String language) {
        this.language = StringUtils.isBlank(language) ? language : language + "," + language;
        return this;
    }

    public SearchSubtitles language(String languages) {
        this.language = languages;
        return this;
    }

    public SearchSubtitles machineTranslated(MachineTranslatedEnum machineTranslated) {
        this.machineTranslated = machineTranslated;
        return this;
    }

    public SearchSubtitles moviehash(String moviehash) {
        this.moviehash = moviehash;
        return this;
    }

    public SearchSubtitles moviehashMatch(MoviehashMatchEnum moviehashMatch) {
        this.moviehashMatch = moviehashMatch;
        return this;
    }

    public SearchSubtitles orderBy(SearchSubtitlesEnum orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public SearchSubtitles orderDirection(OrderDirectionEnum orderDirection) {
        this.orderDirection = orderDirection;
        return this;
    }

    public SearchSubtitles page(int page) {
        this.page = page;
        return this;
    }

    public SearchSubtitles parentFeatureId(int parentFeatureId) {
        this.parentFeatureId = parentFeatureId;
        return this;
    }

    public SearchSubtitles parentImdbId(int parentImdbId) {
        this.parentImdbId = parentImdbId;
        return this;
    }

    public SearchSubtitles parentTmdbId(int parentTmdbId) {
        this.parentTmdbId = parentTmdbId;
        return this;
    }

    public SearchSubtitles query(String query) {
        this.query = query;
        return this;
    }

    public SearchSubtitles season(int season) {
        this.season = season;
        return this;
    }

    public SearchSubtitles tmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
        return this;
    }

    public SearchSubtitles trustedSources(TrustedSourcesEnum trustedSources) {
        this.trustedSources = trustedSources;
        return this;
    }

    public SearchSubtitles type(TypeEnum type) {
        this.type = type;
        return this;
    }

    public SearchSubtitles userId(int userId) {
        this.userId = userId;
        return this;
    }

    public SearchSubtitles year(int year) {
        this.year = year;
        return this;
    }

    public Subtitles200Response searchSubtitles() throws ApiException {
        return execute(() -> new SubtitlesApi(apiClient).subtitles(id, imdbId, tmdbId, getValue(type), query, language, moviehash,
                userId, getValue(hearingImpaired), getValue(foreignPartsOnly), getValue(trustedSources), getValue(machineTranslated),
                getValue(aiTranslated), orderBy == null ? null : orderBy.getParamName(), getValue(orderDirection), parentFeatureId, parentImdbId,
                parentTmdbId, season, episode, year, getValue(moviehashMatch), page));
    }

    private String getValue(ParamIntf param) {
        return param == null ? null : param.getValue();
    }
}
