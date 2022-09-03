package org.lodder.subtools.sublibrary.privateRepo.model;

import java.io.Serializable;

import org.lodder.subtools.sublibrary.model.VideoType;

public class IndexSubtitle implements Serializable {
    /**
     *
     */
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

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *        the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the season
     */
    public int getSeason() {
        return season;
    }

    /**
     * @param season
     *        the season to set
     */
    public void setSeason(int season) {
        this.season = season;
    }

    /**
     * @return the episode
     */
    public int getEpisode() {
        return episode;
    }

    /**
     * @param episode
     *        the episode to set
     */
    public void setEpisode(int episode) {
        this.episode = episode;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename
     *        the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language
     *        the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the tvdbid
     */
    public int getTvdbid() {
        return tvdbid;
    }

    /**
     * @param tvdbid
     *        the tvdbid to set
     */
    public void setTvdbid(int tvdbid) {
        this.tvdbid = tvdbid;
    }

    /**
     * @return the uploader
     */
    public String getUploader() {
        return uploader;
    }

    /**
     * @param uploader
     *        the uploader to set
     */
    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    /**
     * @return the originalSource
     */
    public String getOriginalSource() {
        return originalSource;
    }

    /**
     * @param originalSource
     *        the originalSource to set
     */
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
