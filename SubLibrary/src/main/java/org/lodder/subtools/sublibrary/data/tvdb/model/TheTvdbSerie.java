package org.lodder.subtools.sublibrary.data.tvdb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.sublibrary.Language;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class TheTvdbSerie implements Serializable {
    private static final long serialVersionUID = -4036836377513152443L;
    private int id;
    // private String serieId;
    private Language language;
    private String serieName;
    private String banner;
    // private String overview;
    private String firstAired;
    private String imdbId;
    private String zap2ItId;
    private List<String> actors = new ArrayList<>();
    private String airsDayOfWeek;
    private String airsTime;
    private String contentRating;
    private List<String> genres = new ArrayList<>();
    private String network;
    private String rating;
    private String runtime;
    private String status;
    private String fanart;
    private String lastUpdated;
    private String poster;

}
