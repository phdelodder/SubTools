package org.lodder.subtools.sublibrary.model;

import java.io.File;
import java.util.OptionalInt;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
public class MovieRelease extends Release {

    private String name;
    private Integer year;
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

        MovieReleaseBuilderOther year(Integer year);

        MovieRelease build();
    }

    public static MovieReleaseBuilderName builder() {
        return new MovieReleaseBuilder();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class MovieReleaseBuilder implements MovieReleaseBuilderOther, MovieReleaseBuilderName {
        private String name;
        private Integer year;

        private String quality;
        private File file;
        private String description;
        private String releaseGroup;

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

    public String getImdbIdAsString() {
        return "tt" + StringUtils.leftPad(String.valueOf(imdbId), 7, "0");
    }

    public OptionalInt getTvdbId() {
        return tvdbId == 0 ? OptionalInt.of(tvdbId) : OptionalInt.of(tvdbId);
    }

    public OptionalInt getImdbId() {
        return imdbId == 0 ? OptionalInt.of(imdbId) : OptionalInt.of(imdbId);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getName() + " " + this.getQuality() + " " + this.getReleaseGroup();
    }

    @Override
    public String getReleaseDescription() {
        return getName();
    }
}
