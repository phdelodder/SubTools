package org.lodder.subtools.sublibrary.privateRepo.model;

import java.io.Serializable;

import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.model.VideoType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexSubtitle implements Serializable {

    private static final long serialVersionUID = -7687377134723402960L;
    private String name;
    private int season;
    private int episode;
    private String filename;
    private Language language;
    private int tvdbId;
    private String uploader;
    private String originalSource;
    private VideoType videoType;
    private int imdbId;
    private int year;

    public IndexSubtitle() {

    }

    public IndexSubtitle(String name, int season, int episode, String filename, Language language,
            int tvdbid, String uploader, String originalSource, VideoType videoType) {
        setName(name);
        setSeason(season);
        setEpisode(episode);
        setFilename(filename);
        setLanguage(language);
        setTvdbId(tvdbid);
        setUploader(uploader);
        setOriginalSource(originalSource);
        setVideoType(videoType);
    }

    public IndexSubtitle(String name, String filename, Language language, String uploader,
            String originalSource, VideoType videoType, int imdbid, int year) {
        setName(name);
        setFilename(filename);
        setLanguage(language);
        setUploader(uploader);
        setOriginalSource(originalSource);
        setVideoType(videoType);
        setImdbId(imdbid);
        setYear(year);
    }

}
