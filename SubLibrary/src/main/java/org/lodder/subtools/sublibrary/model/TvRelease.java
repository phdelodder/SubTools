package org.lodder.subtools.sublibrary.model;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import manifold.ext.props.rt.api.val;
import manifold.ext.props.rt.api.var;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbEpisode;

public final class TvRelease extends Release {

    // parsed from the filename
    @val String name;
    @val int season;
    @val List<Integer> episodes;
    @var @Nullable String title;
    // tvdb name
    @var @Nullable String originalName;
    @val boolean special;
    // custom name which can be used to search subtitle providers
    @val @Nullable String customName;

    public TvRelease(String name, int season, int episode, @Nullable Path file=null, @Nullable String releaseGroup=null,
        @Nullable String quality=null, @Nullable String extension=null, @Nullable String originalName=null,
        @Nullable String customName=null, @Nullable String title=null, boolean special=false) {
        this(name, season, List.of(episode), file, releaseGroup, quality, extension, originalName, customName, title,
            special);
    }

    public TvRelease(String name, int season, List<Integer> episodes, @Nullable Path file=null,
        @Nullable String releaseGroup=null, @Nullable String quality=null, @Nullable String extension=null,
        @Nullable String originalName=null, @Nullable String customName=null, @Nullable String title=null,
        boolean special=false) {
        super(VideoType.EPISODE, file, releaseGroup, quality, extension);
        this.name = name;
        this.title = title;
        this.season = season;
        this.episodes = Collections.unmodifiableList(episodes);
        this.special = special;
        this.originalName = originalName;
        this.customName = customName;
    }

    public String getNameWithSeasonEpisode() {
        return formatName(name, season, episodes.isEmpty() ? -1 : firstEpisode);
    }

    public static String formatName(String serieName, int season, int episode) {
        return serieName + " " + formatSeasonEpisode(season, episode);
    }

    public static String formatSeasonEpisode(int season, int episode) {
        return "S%02dE%02d".formatted(season, episode);
    }

    public void updateTvdbEpisodeInfo(TheTvdbEpisode tvdbEpisode) {
        this.title = tvdbEpisode.episodeName; // update to reflect correct episode title
    }

    public int getFirstEpisode() {
        return episodes.first;
    }

    @Override
    public String toString() {
        return "${getClass().getSimpleName()}: $name s$season e$episodes $quality $releaseGroup";
    }

    @Override
    public String getReleaseDescription() {
        return getNameWithSeasonEpisode();
    }

    public String getDisplayName() {
        return StringUtils.defaultIfBlank(originalName, name);
    }
}
