package org.lodder.subtools.sublibrary.model;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbEpisode;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
public class TvRelease extends Release {

    // parsed from the filename
    private final String name;
    private String title;
    private final int season;
    private int tvdbId;
    private final List<Integer> episodeNumbers;
    // tvdb name
    private String originalName;
    private boolean special;

    public String getNameWithSeasonEpisode() {
        return formatName(name, season, episodeNumbers.isEmpty() ? -1 : episodeNumbers.get(0));
    }

    public static String formatName(String serieName, int season, int episode) {
        return serieName + " " + formatSeasonEpisode(season, episode);
    }

    public static String formatSeasonEpisode(int season, int episode) {
        return "S%sE%s".formatted(StringUtils.leftPad(String.valueOf(season), 2, "0"),
                StringUtils.leftPad(String.valueOf(episode), 2, "0"));
    }

    public interface TvReleaseBuilderShowName {
        TvReleaseBuilderSeason name(String name);
    }

    public interface TvReleaseBuilderSeason {
        TvReleaseBuilderEpisode season(int season);
    }

    public interface TvReleaseBuilderEpisode {
        TvReleaseBuilderOther episode(int episode);

        TvReleaseBuilderOther episodes(List<Integer> episodes);
    }

    public interface TvReleaseBuilderOther {
        TvReleaseBuilderOther file(File file);

        TvReleaseBuilderOther quality(String quality);

        TvReleaseBuilderOther description(String description);

        TvReleaseBuilderOther special(boolean special);

        TvReleaseBuilderOther releaseGroup(String releaseGroup);

        TvReleaseBuilderOther title(String title);

        TvRelease build();
    }

    public static TvReleaseBuilderShowName builder() {
        return new TvReleaseBuilder();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class TvReleaseBuilder
            implements TvReleaseBuilderOther, TvReleaseBuilderEpisode, TvReleaseBuilderSeason, TvReleaseBuilderShowName {
        private String name;
        private String title;
        private int season;
        private List<Integer> episodes;
        private boolean special;
        private String quality;
        private File file;
        private String description;
        private String releaseGroup;

        @Override
        public TvReleaseBuilder episode(int episode) {
            this.episodes = List.of(episode);
            return this;
        }

        @Override
        public TvReleaseBuilder episodes(List<Integer> episodes) {
            this.episodes = Collections.unmodifiableList(episodes);
            return this;
        }

        @Override
        public TvRelease build() {
            return new TvRelease(file, description, releaseGroup, quality, name, title, season, episodes, special);
        }
    }

    private TvRelease(File file, String description, String releaseGroup, String quality, String name, String title, int season,
            List<Integer> episodeNumbers, boolean special) {
        super(VideoType.EPISODE, file, description, releaseGroup, quality);
        this.name = name;
        this.title = title;
        this.season = season;
        this.episodeNumbers = episodeNumbers;
        this.originalName = "";
        this.special = special;
    }

    public void updateTvdbEpisodeInfo(TheTvdbEpisode tvdbEpisode) {
        this.title = tvdbEpisode.getEpisodeName(); // update to reflect correct episode title
    }

    public OptionalInt getTvdbId() {
        return tvdbId == 0 ? OptionalInt.empty() : OptionalInt.of(tvdbId);
    }

    public int getFirstEpisodeNumber() {
        return episodeNumbers.get(0);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getName() + " s" + this.getSeason() + " e"
                + this.getEpisodeNumbers().toString() + " " + this.getQuality() + " " + this.getReleaseGroup();
    }

    @Override
    public String getReleaseDescription() {
        return getNameWithSeasonEpisode();
    }

    public String getDisplayName() {
        return StringUtils.isNotBlank(getOriginalName()) ? getOriginalName() : getName();
    }
}
