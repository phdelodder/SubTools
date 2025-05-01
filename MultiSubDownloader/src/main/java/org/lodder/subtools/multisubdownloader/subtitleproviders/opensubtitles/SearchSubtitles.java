package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.exception.OpenSubtitlesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param.AiTranslatedEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param.ForeignPartsOnlyEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param.HearingImpairedEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param.MachineTranslatedEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param.MoviehashMatchEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param.OrderDirectionEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param.ParamIntf;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param.SearchSubtitlesEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param.TrustedSourcesEnum;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param.TypeEnum;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.opensubtitles.api.SubtitlesApi;
import org.opensubtitles.invoker.ApiClient;
import org.opensubtitles.model.Subtitles200Response;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
@RequiredArgsConstructor
public final class SearchSubtitles extends OpenSubtitlesExecuter {
    private final Manager manager;
    private final ApiClient apiClient;

    private @Nullable AiTranslatedEnum aiTranslated;

    private @Nullable Integer episode;

    private @Nullable ForeignPartsOnlyEnum foreignPartsOnly;

    private @Nullable HearingImpairedEnum hearingImpaired;

    private @Nullable Integer id;

    private @Nullable Integer imdbId;

    private @Nullable Language language;

    private @Nullable MachineTranslatedEnum machineTranslated;

    private @Nullable String movieHash;

    private @Nullable MoviehashMatchEnum movieHashMatch;

    private @Nullable SearchSubtitlesEnum orderBy;

    private @Nullable OrderDirectionEnum orderDirection;

    private @Nullable Integer page;

    private @Nullable Integer parentFeatureId;

    private @Nullable Integer parentImdbId;

    private @Nullable Integer parentTmdbId;

    private @Nullable String query;

    private @Nullable Integer season;

    private @Nullable Integer tmdbId;

    private @Nullable TrustedSourcesEnum trustedSources;

    private @Nullable TypeEnum type;

    private @Nullable Integer userId;

    private @Nullable Integer year;

    private String userAgent = "SubTools"; // should be set

    public Subtitles200Response searchSubtitles() throws OpenSubtitlesException {
        return manager.getCache(CacheType.MEMORY,
                "OpenSubtitles-subtitles-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s".formatted(
                    id, imdbId, tmdbId, type, query, language, movieHash, userId, hearingImpaired, foreignPartsOnly,
                    trustedSources, machineTranslated, aiTranslated, orderBy, orderDirection, parentFeatureId,
                    parentImdbId, parentTmdbId, season, episode, year, movieHashMatch, page))
            .get(() -> {
                try {
                    return execute(
                        () -> new SubtitlesApi(apiClient).subtitles(id, imdbId, tmdbId, getValue(type), query,
                            language != null ? language.langCode : null, movieHash, userId,
                            getValue(hearingImpaired), getValue(foreignPartsOnly), getValue(trustedSources),
                            getValue(machineTranslated), getValue(aiTranslated),
                            orderBy == null ? null : orderBy.paramName, getValue(orderDirection),
                            parentFeatureId, parentImdbId, parentTmdbId, season, episode, year,
                            getValue(movieHashMatch), page, userAgent));
                } catch (Exception e) {
                    throw new OpenSubtitlesException(e);
                }
            });
    }

    private String getValue(ParamIntf param) {
        return param == null ? null : param.value;
    }
}
