package org.lodder.subtools.sublibrary.model;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieRelease extends Release {

    private String name;
    private int year;
    private int imdbId;
    private int tvdbId;

    public interface MovieReleaseBuilderName {
        MovieReleaseBuilderOther name(String name);
    }

    public interface MovieReleaseBuilderOther {
        MovieReleaseBuilderOther file(File file);

        MovieReleaseBuilderOther quality(String quality);

        MovieReleaseBuilderOther description(String description);

        MovieReleaseBuilderOther releaseGroup(String releaseGroup);

        MovieReleaseBuilderOther year(int year);

        MovieRelease build();
    }

    public static MovieReleaseBuilderName builder() {
        return new MovieReleaseBuilder();
    }

    public static class MovieReleaseBuilder implements MovieReleaseBuilderOther, MovieReleaseBuilderName {
        private String name;
        private Integer year;

        private String quality;
        private File file;
        private String description;
        private String releaseGroup;

        @Override
        public MovieReleaseBuilder year(int year) {
            this.year = year;
            return this;
        }

        @Override
        public MovieReleaseBuilder quality(String quality) {
            this.quality = quality;
            return this;
        }

        @Override
        public MovieReleaseBuilder file(File file) {
            this.file = file;
            return this;
        }

        @Override
        public MovieReleaseBuilder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public MovieReleaseBuilder releaseGroup(String releaseGroup) {
            this.releaseGroup = releaseGroup;
            return this;
        }

        @Override
        public MovieReleaseBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public MovieRelease build() {
            return new MovieRelease(file, description, releaseGroup, quality, name, year);
        }
    }

    private MovieRelease(File file, String description, String releaseGroup, String quality, String name, int year) {
        super(VideoType.MOVIE, file, description, releaseGroup, quality);
        this.name = name;
        this.year = year;
    }

    public String getImdbidAsString() {
        return "tt" + StringUtils.leftPad(String.valueOf(this.getImdbId()), 7, "0");
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getName() + " " + this.getQuality() + " " + this.getReleaseGroup();
    }
}
