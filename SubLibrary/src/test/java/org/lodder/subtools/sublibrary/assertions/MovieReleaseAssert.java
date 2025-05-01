package org.lodder.subtools.sublibrary.assertions;

import static org.assertj.core.api.Assertions.*;

import manifold.ext.rt.api.Self;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.VideoType;

public class MovieReleaseAssert extends ReleaseAssert<MovieRelease> {

    public MovieReleaseAssert(MovieRelease actual) {
        super(actual);
    }

    public @Self MovieReleaseAssert hasYear(int year) {
        isNotNull();
        assertThat(actual.year).isEqualTo(year);
        return this;
    }

    public @Self MovieReleaseAssert withoutYear() {
        isNotNull();
        assertThat(actual.year).isNull();
        return this;
    }

    public @Self MovieReleaseAssert hasName(String name) {
        isNotNull();
        assertThat(actual.name).isEqualTo(name);
        return this;
    }

    public @Self MovieReleaseAssert hasMovieVideoType() {
        isNotNull();
        assertThat(actual.videoType).isEqualTo(VideoType.MOVIE);
        return this;
    }
}
