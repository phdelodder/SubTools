package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2;

import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.exception.OpenSubtitlesException;
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
import org.lodder.subtools.sublibrary.Language;
import org.opensubtitles.api.SubtitlesApi;
import org.opensubtitles.invoker.ApiClient;
import org.opensubtitles.model.Subtitles200Response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
@RequiredArgsConstructor
public class SearchSubtitles extends OpenSubtitlesExecuter {
    private final ApiClient apiClient;

    private AiTranslatedEnum aiTranslated;

    private Integer episode;

    private ForeignPartsOnlyEnum foreignPartsOnly;

    private HearingImpairedEnum hearingImpaired;

    private Integer id;

    private Integer imdbId;

    private Language language;

    private MachineTranslatedEnum machineTranslated;

    private String movieHash;

    private MoviehashMatchEnum movieHashMatch;

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

    public Subtitles200Response searchSubtitles() throws OpenSubtitlesException {
        try {
            return execute(() -> new SubtitlesApi(apiClient).subtitles(id, imdbId, tmdbId, getValue(type), query,
                    language != null ? language.getLangCode() : null, movieHash,
                    userId, getValue(hearingImpaired), getValue(foreignPartsOnly), getValue(trustedSources), getValue(machineTranslated),
                    getValue(aiTranslated), orderBy == null ? null : orderBy.getParamName(), getValue(orderDirection), parentFeatureId, parentImdbId,
                    parentTmdbId, season, episode, year, getValue(movieHashMatch), page));
        } catch (Exception e) {
            throw new OpenSubtitlesException(e);
        }
    }

    private String getValue(ParamIntf param) {
        return param == null ? null : param.getValue();
    }
}
