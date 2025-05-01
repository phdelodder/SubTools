package org.lodder.subtools.sublibrary.assertions;

import static org.assertj.core.api.Assertions.*;

import manifold.ext.rt.api.Self;
import org.assertj.core.api.AbstractAssert;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;

public class ReleaseAssert<R extends Release> extends AbstractAssert<ReleaseAssert<R>, R> {

    public ReleaseAssert(R actual) {
        super(actual, ReleaseAssert.class);
    }

    public MovieReleaseAssert isMovie() {
        assertThat(actual).isInstanceOf(MovieRelease.class);
        return new MovieReleaseAssert((MovieRelease) actual);
    }

    public TvReleaseAssert isSerie() {
        assertThat(actual).isInstanceOf(TvRelease.class);
        return new TvReleaseAssert((TvRelease) actual);
    }

    public @Self ReleaseAssert<R> hasFileName(String fileName) {
        isNotNull();
        assertThat(actual.fileName).isEqualTo(fileName);
        return this;
    }

    public @Self ReleaseAssert<R> hasExtension(String extension) {
        isNotNull();
        assertThat(actual.extension).isEqualTo(extension);
        return this;
    }

    public @Self ReleaseAssert<R> hasReleaseGroup(String releaseGroup) {
        isNotNull();
        assertThat(actual.releaseGroup).isEqualTo(releaseGroup);
        return this;
    }

    public @Self ReleaseAssert<R> withoutReleaseGroup() {
        isNotNull();
        assertThat(actual.releaseGroup).isEmpty();
        return this;
    }

    public @Self ReleaseAssert<R> hasQuality(String quality) {
        isNotNull();
        assertThat(actual.quality).isEqualTo(quality);
        return this;
    }

    public @Self ReleaseAssert<R> withoutQuality() {
        isNotNull();
        assertThat(actual.quality).isEmpty();
        return this;
    }
}
