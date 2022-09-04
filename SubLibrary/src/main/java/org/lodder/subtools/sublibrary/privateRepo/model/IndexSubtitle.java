package org.lodder.subtools.sublibrary.privateRepo.model;

import java.io.Serializable;

import org.lodder.subtools.sublibrary.model.VideoType;

public class IndexSubtitle implements Serializable {

    private static final long serialVersionUID = -7687377134723402960L;
    private String name;
    private int season;
    private int episode;
    private String filename;
    private String language;
    private String uploader;
    private String originalSource;
    private int tvdbid;
    private VideoType videoType;
    private int imdbid;
    private int year;

    public IndexSubtitle() {

    }

    public IndexSubtitle(String name, int season, int episode, String filename,
            String language, int tvdbid, String uploader,
            String originalSource, VideoType videoType) {
        setName(name);
        setSeason(season);
        setEpisode(episode);
        setFilename(filename);
        setLanguage(language);
        setTvdbid(tvdbid);
        setUploader(uploader);
        setOriginalSource(originalSource);
        setVideoType(videoType);
    }

    public IndexSubtitle(String name, String filename, String language,
            String uploader, String originalSource, VideoType videoType,
            int imdbid, int year) {
        setName(name);
        setFilename(filename);
        setLanguage(language);
        setUploader(uploader);
        setOriginalSource(originalSource);
        setVideoType(videoType);
        setImdbid(imdbid);
        setYear(year);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getTvdbid() {
        return tvdbid;
    }

    public void setTvdbid(int tvdbid) {
        this.tvdbid = tvdbid;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getOriginalSource() {
        return originalSource;
    }

    public void setOriginalSource(String originalSource) {
        this.originalSource = originalSource;
    }

    public VideoType getVideoType() {
        return videoType;
    }

    public void setVideoType(VideoType videoType) {
        this.videoType = videoType;
    }

    public int getImdbid() {
        return imdbid;
    }

    public void setImdbid(int imdbid) {
        this.imdbid = imdbid;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
