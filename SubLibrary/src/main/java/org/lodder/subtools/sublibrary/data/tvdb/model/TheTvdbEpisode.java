package org.lodder.subtools.sublibrary.data.tvdb.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.ToString;
import manifold.ext.props.rt.api.var;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.sublibrary.Language;

@ToString
public class TheTvdbEpisode implements Serializable {
    @Serial
    private static final long serialVersionUID = 913790243120597542L;
    @var @Nullable String id;
    @var @Nullable String combinedEpisodeNumber;
    @var @Nullable String combinedSeason;
    @var @Nullable String dvdChapter;
    @var @Nullable String dvdDiscId;
    @var @Nullable String dvdEpisodeNumber;
    @var @Nullable String dvdSeason;
    @var List<String> directors = new ArrayList<>();
    @var @Nullable String epImgFlag;
    @var @Nullable String episodeName;
    @var int episodeNumber;
    @var @Nullable String firstAired;
    @var List<String> guestStars = new ArrayList<>();
    @var @Nullable String imdbId;
    @var @Nullable Language language;
    // @var String overview;
    @var @Nullable String productionCode;
    @var @Nullable String rating;
    @var int seasonNumber;
    @var List<String> writers = new ArrayList<>();
    @var @Nullable String absoluteNumber;
    @var int airsAfterSeason;
    @var int airsBeforeSeason;
    @var int airsBeforeEpisode;
    @var @Nullable String filename;
    @var @Nullable String lastUpdated;
    @var @Nullable String seriesId;
    @var @Nullable String seasonId;
}
