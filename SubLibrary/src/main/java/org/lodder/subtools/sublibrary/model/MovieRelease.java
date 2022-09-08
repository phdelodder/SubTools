package org.lodder.subtools.sublibrary.model;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieRelease extends Release {

    private String title;
    private int year;
    private int imdbId;

    public MovieRelease() {
        super(VideoType.MOVIE);
        this.title = "";
        this.year = 0;
        this.imdbId = 0;
    }

    public MovieRelease(String title, Integer year, File file, String extension, String description, String team) {
        super(VideoType.MOVIE, file, extension, description, team);
        this.title = title;
        this.year = year;
        this.imdbId = 0;
    }

    public String getImdbidAsString() {
        return "tt" + StringUtils.leftPad(String.valueOf(this.getImdbId()), 7, "0");
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getTitle() + " " + this.getQuality() + " " + this.getReleasegroup();
    }
}
