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
public class TheTvdbSerie implements Serializable {
    @Serial
    private static final long serialVersionUID = -4036836377513152443L;
    @var int id;
    //@var String serieId;
    @var @Nullable Language language;
    @var @Nullable String serieName;
    @var @Nullable String banner;
    //@var String overview;
    @var @Nullable String firstAired;
    @var @Nullable String imdbId;
    @var @Nullable String zap2ItId;
    @var List<String> actors = new ArrayList<>();
    @var @Nullable String airsDayOfWeek;
    @var @Nullable String airsTime;
    @var @Nullable String contentRating;
    @var List<String> genres = new ArrayList<>();
    @var @Nullable String network;
    @var @Nullable String rating;
    @var @Nullable String runtime;
    @var @Nullable String status;
    @var @Nullable String fanArt;
    @var @Nullable String lastUpdated;
    @var @Nullable String poster;
}
