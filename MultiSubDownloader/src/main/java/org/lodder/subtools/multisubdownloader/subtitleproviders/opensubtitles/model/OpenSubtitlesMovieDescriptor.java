package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.model;

import java.util.Arrays;

public class OpenSubtitlesMovieDescriptor {

    private final int year;
    private final int imdbId;
    private String name;

    public OpenSubtitlesMovieDescriptor(String name, int imdbId) {
        this(name, -1, imdbId);
    }

    public OpenSubtitlesMovieDescriptor(String name, int year, int imdbId) {
        this.name = name;
        this.year = year;
        this.imdbId = imdbId;
    }

    public int getYear() {
        return year;
    }

    public int getImdbId() {
        return imdbId;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof OpenSubtitlesMovieDescriptor other) {
            return imdbId == other.imdbId && year == other.year && name.equals(other.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { name, year, imdbId });
    }

    @Override
    public String toString() {
        if (year < 0) {
            return name;
        }

        return String.format("%s (%d)", name, year);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
