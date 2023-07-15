package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.model;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @Override
    public boolean equals(Object object) {
        return object instanceof OpenSubtitlesMovieDescriptor other
                && imdbId == other.imdbId && year == other.year && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year, imdbId);
    }

    @Override
    public String toString() {
        if (year < 0) {
            return name;
        }

        return String.format("%s (%d)", name, year);
    }
}
