package org.lodder.subtools.sublibrary.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;

public class TvRelease extends Release {

    private String showname;
    private String title;
    private int season, tvdbid, tvrageid;
    private List<Integer> episodeNumbers;
    private String originalShowName;
    private boolean special;

    public TvRelease() {
        super(VideoType.EPISODE);
        showname = "";
        title = "";
        season = 0;
        episodeNumbers = new ArrayList<>();
        setTvdbid(0);
        setTvrageid(0);
        setOriginalShowName("");
        setSpecial(false);
    }

    public TvRelease(String show, int season, List<Integer> episodeNumbers, File file, String extension, String description, String team,
            boolean special) {
        super(VideoType.EPISODE, file, extension, description, team);
        this.showname = show;
        title = "";
        this.season = season;
        this.episodeNumbers = episodeNumbers;
        setTvdbid(0);
        setTvrageid(0);
        setOriginalShowName("");
        setSpecial(special);
    }

    public void setShow(String show) {
        this.showname = show;
    }

    public String getShow() {
        return showname;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getSeason() {
        return season;
    }

    public void setEpisodeNumbers(List<Integer> episodeNumbers) {
        this.episodeNumbers = episodeNumbers;
    }

    public List<Integer> getEpisodeNumbers() {
        return episodeNumbers;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getTvdbid() {
        return tvdbid;
    }

    public void setTvdbid(int tvdbid) {
        this.tvdbid = tvdbid;
    }

    public String getOriginalShowName() {
        return originalShowName;
    }

    public void setOriginalShowName(String originalShowName) {
        this.originalShowName = originalShowName;
    }

    public void updateTVRageEpisodeInfo(TVRageEpisode tvrageEpisode) {
        if (tvrageEpisode.getTitle().contains("$")) {
            this.setTitle(tvrageEpisode.getTitle().replace("$", "")); // update to reflect correct episode title and fix for $
        } else {
            this.setTitle(tvrageEpisode.getTitle()); // update to reflect correct episode title
        }
    }

    public void updateTvdbEpisodeInfo(TheTVDBEpisode tvdbEpisode) {
        this.setTitle(tvdbEpisode.getEpisodeName()); // update to reflect correct episode title
    }

    public int getTvrageid() {
        return tvrageid;
    }

    public void setTvrageid(int tvrageid) {
        this.tvrageid = tvrageid;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getShow() + " s" + this.getSeason() + " e"
                + this.getEpisodeNumbers().toString() + " " + this.getQuality() + " " + this.getReleasegroup();
    }
}
