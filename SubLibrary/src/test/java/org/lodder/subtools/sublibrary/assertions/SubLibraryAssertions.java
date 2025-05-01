package org.lodder.subtools.sublibrary.assertions;

import lombok.experimental.UtilityClass;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;

@UtilityClass
public class SubLibraryAssertions {

    public static TvReleaseAssert assertThat(TvRelease actual) {
        return new TvReleaseAssert(actual);
    }

    public static MovieReleaseAssert assertThat(MovieRelease actual) {
        return new MovieReleaseAssert(actual);
    }

    public static ReleaseAssert<?> assertThat(Release actual) {
        return new ReleaseAssert<>(actual);
    }
}
