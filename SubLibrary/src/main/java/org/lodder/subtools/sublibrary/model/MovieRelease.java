package org.lodder.subtools.sublibrary.model;

import java.nio.file.Path;

import manifold.ext.props.rt.api.var;
import org.jspecify.annotations.Nullable;

public final class MovieRelease extends Release {

    @var String name;
    @var @Nullable Integer year;
    @var @Nullable Integer imdbId;

    public MovieRelease(String name, @Nullable Path file=null, @Nullable String releaseGroup=null,
        @Nullable String quality=null, @Nullable String extension=null, @Nullable Integer year=null) {
        super(VideoType.MOVIE, file, releaseGroup, quality, extension);
        this.name = name;
        this.year = year;
    }

    public String getImdbIdAsString() {
        return "tt%07d".formatted(imdbId);
    }

    @Override
    public String toString() {
        return "${getClass().getSimpleName()}: $name ${quality} ${releaseGroup}";
    }

    @Override
    public String getReleaseDescription() {
        return name;
    }
}
