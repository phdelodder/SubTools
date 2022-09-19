package org.lodder.subtools.sublibrary.model;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TvRelease extends Release {

    // parsed from the filename
    private final String name;
    private String title;
    private final int season;
    private int tvdbId;
    private int tvrageId;
    private final List<Integer> episodeNumbers;
    // tvdb name
    private String originalShowName;
    private boolean special;

    public interface TvReleaseBuilderShowName {
        TvReleaseBuilderSeason name(String name);
    }

    public interface TvReleaseBuilderSeason {
        TvReleaseBuilderEpisode season(int season);
    }

    public interface TvReleaseBuilderEpisode {
        TvReleaseBuilderOther episode(int episode);

        TvReleaseBuilderOther episode(List<Integer> episodes);
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
        public TvReleaseBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public TvReleaseBuilder season(int season) {
            this.season = season;
            return this;
        }

        @Override
        public TvReleaseBuilder episode(int episode) {
            this.episodes = List.of(episode);
            return this;
        }

        @Override
        public TvReleaseBuilder episode(List<Integer> episodes) {
            this.episodes = Collections.unmodifiableList(episodes);
            return this;
        }

        @Override
        public TvReleaseBuilder quality(String quality) {
            this.quality = quality;
            return this;
        }

        @Override
        public TvReleaseBuilder file(File file) {
            this.file = file;
            return this;
        }

        @Override
        public TvReleaseBuilder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public TvReleaseBuilder special(boolean special) {
            this.special = special;
            return this;
        }

        @Override
        public TvReleaseBuilder releaseGroup(String releaseGroup) {
            this.releaseGroup = releaseGroup;
            return this;
        }

        @Override
        public TvReleaseBuilder title(String title) {
            this.title = title;
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
        this.originalShowName = "";
        this.special = special;
    }

    public void updateTVRageEpisodeInfo(TVRageEpisode tvrageEpisode) {
        if (tvrageEpisode.getTitle().contains("$")) {
            this.title = tvrageEpisode.getTitle().replace("$", ""); // update to reflect correct episode title and fix for $
        } else {
            this.title = tvrageEpisode.getTitle(); // update to reflect correct episode title
        }
    }

    public void updateTvdbEpisodeInfo(TheTVDBEpisode tvdbEpisode) {
        this.title = tvdbEpisode.getEpisodeName(); // update to reflect correct episode title
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.getName() + " s" + this.getSeason() + " e"
                + this.getEpisodeNumbers().toString() + " " + this.getQuality() + " " + this.getReleaseGroup();
    }
}
