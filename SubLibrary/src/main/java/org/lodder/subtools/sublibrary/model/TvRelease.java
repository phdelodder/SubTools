package org.lodder.subtools.sublibrary.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TvRelease extends Release {

    private String showName;
    private String title;
    private int season;
    private int tvdbId;
    private int tvrageId;
    private List<Integer> episodeNumbers;
    private String originalShowName;
    private boolean special;

    public TvRelease() {
        super(VideoType.EPISODE);
        this.showName = "";
        this.title = "";
        this.season = 0;
        this.episodeNumbers = new ArrayList<>();
        this.tvdbId = 0;
        this.tvrageId = 0;
        this.originalShowName = "";
        this.special = false;
    }

    public TvRelease(String showName, int season, List<Integer> episodeNumbers, File file, String extension, String description, String team,
            boolean special) {
        super(VideoType.EPISODE, file, extension, description, team);
        this.showName = showName;
        this.title = "";
        this.season = season;
        this.episodeNumbers = episodeNumbers;
        this.tvdbId = 0;
        this.tvrageId = 0;
        this.originalShowName = "";
        this.special = special;
    }

    public void updateTVRageEpisodeInfo(TVRageEpisode tvrageEpisode) {
        if (tvrageEpisode.getTitle().contains("$")) {
            this.title = tvrageEpisode.getTitle().replace("$", ""); // update to reflect correct episode title and fix for $
        } else {
            this.title = tvrageEpisode.getTitle(); // update to reflect correct episode title
        }
    }

    public void updateTvdbEpisodeInfo(TheTVDBEpisode tvdbEpisode) {
        this.title = tvdbEpisode.getEpisodeName(); // update to reflect correct episode title
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getShowName() + " s" + this.getSeason() + " e"
                + this.getEpisodeNumbers().toString() + " " + this.getQuality() + " " + this.getReleasegroup();
    }
}
