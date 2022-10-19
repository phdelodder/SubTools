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
public class TheTvdbEpisode implements Serializable {
    private static final long serialVersionUID = 913790243120597542L;
    private String id;
    private String combinedEpisodeNumber;
    private String combinedSeason;
    private String dvdChapter;
    private String dvdDiscId;
    private String dvdEpisodeNumber;
    private String dvdSeason;
    private List<String> directors = new ArrayList<>();
    private String epImgFlag;
    private String episodeName;
    private int episodeNumber;
    private String firstAired;
    private List<String> guestStars = new ArrayList<>();
    private String imdbId;
    private Language language;
    // private String overview;
    private String productionCode;
    private String rating;
    private int seasonNumber;
    private List<String> writers = new ArrayList<>();
    private String absoluteNumber;
    private int airsAfterSeason;
    private int airsBeforeSeason;
    private int airsBeforeEpisode;
    private String filename;
    private String lastUpdated;
    private String seriesId;
    private String seasonId;
}
