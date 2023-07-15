package org.lodder.subtools.multisubdownloader.lib.control.subtitles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

class SubtitleFilteringTest {

    @Test
    void testExcludeImpairedHearingFiltering() {
        Subtitle subtitle1 = createSubtitle("", "", true, "");
        Subtitle subtitle2 = createSubtitle("", "", false, "");
        Subtitle subtitle3 = createSubtitle("", "", true, "");

        assertThatFilter(new SubtitleFiltering(createSettings(false, false, true)))
                .appliedOnSubtitles(subtitle1, subtitle2, subtitle3)
                .forRelease(createRelease("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION"))
                .matchesSubtitles(subtitle2);
    }

    @Test
    void testKeywordMatchFilter() {
        Release release = createRelease("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION");
        Subtitle subtitle1 = createSubtitle("Criminal.Minds.S10E12.HDTV.XviD-AFG.srt", "AFG", false, "");
        Subtitle subtitle2 = createSubtitle("criminal.minds.1012.hdtv-lol.srt", "lol", false, "");
        Subtitle subtitle3 = createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", true, "");
        Subtitle subtitle4 = createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", false, "");
        Subtitle subtitle5 = createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", false, "720p HDTV X264");
        Subtitle subtitle6 = createSubtitle("Criminal.Minds.S10E12.Anonymous.1080p.WEB-DL.DD5.1.H.264-CtrlHD", "CtrlHD", false, "");

        // only keyword
        assertThatFilter(new SubtitleFiltering(createSettings(true, false, false)))
                .appliedOnSubtitles(subtitle1, subtitle2, subtitle3, subtitle4, subtitle5, subtitle6)
                .forRelease(release)
                .matchesSubtitles(subtitle3, subtitle4, subtitle5);

        // keyword and exclude hearing impaired
        assertThatFilter(new SubtitleFiltering(createSettings(true, false, true)))
                .appliedOnSubtitles(subtitle1, subtitle2, subtitle3, subtitle4, subtitle5, subtitle6)
                .forRelease(release)
                .matchesSubtitles(subtitle4, subtitle5);
    }

    @Test
    void testExactMatchFilter() {
        Release release = createRelease("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION");
        Subtitle subtitle1 = createSubtitle("Criminal.Minds.S10E12.HDTV.XviD-AFG.srt", "AFG", false, "");
        Subtitle subtitle2 = createSubtitle("criminal.minds.1012.hdtv-lol.srt", "lol", false, "");
        Subtitle subtitle3 = createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", true, "");
        Subtitle subtitle4 = createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", false, "");
        Subtitle subtitle5 = createSubtitle("Criminal.Minds.S10E12.Anonymous.1080p.WEB-DL.DD5.1.H.264-CtrlHD", "CtrlHD", false, "");

        // only exact match
        assertThatFilter(new SubtitleFiltering(createSettings(false, true, false)))
                .appliedOnSubtitles(subtitle1, subtitle2, subtitle3, subtitle4, subtitle5)
                .forRelease(release)
                .matchesSubtitles(subtitle3, subtitle4);

        // exact match and exclude hearing impaired
        assertThatFilter(new SubtitleFiltering(createSettings(false, true, true)))
                .appliedOnSubtitles(subtitle1, subtitle2, subtitle3, subtitle4, subtitle5)
                .forRelease(release)
                .matchesSubtitles(subtitle4);
    }

    @Test
    void testExactMatchAndKeywordMatchFilter() {
        Release release = createRelease("Criminal.Minds.S10E12.Anonymous.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION");
        Subtitle subtitle1 = createSubtitle("Criminal.Minds.S10E12.HDTV.XviD-AFG.srt", "AFG", false, "");
        Subtitle subtitle2 = createSubtitle("criminal.minds.1012.hdtv-lol.srt", "lol", false, "");
        Subtitle subtitle3 = createSubtitle("Criminal.Minds.S10E12.Anonymous.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", true, "");
        Subtitle subtitle4 = createSubtitle("Criminal.Minds.S10E12.Anonymous.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", false, "");
        Subtitle subtitle5 = createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", false, "");
        Subtitle subtitle6 = createSubtitle("Criminal.Minds.S10E12.Anonymous.1080p.WEB-DL.DD5.1.H.264-CtrlHD", "CtrlHD", false, "");

        // only exact match
        assertThatFilter(new SubtitleFiltering(createSettings(true, true, false)))
                .appliedOnSubtitles(subtitle1, subtitle2, subtitle3, subtitle4, subtitle5, subtitle6)
                .forRelease(release)
                .matchesSubtitles(subtitle3, subtitle4);

        // exact match and exclude hearing impaired
        assertThatFilter(new SubtitleFiltering(createSettings(true, true, true)))
                .appliedOnSubtitles(subtitle1, subtitle2, subtitle3, subtitle4, subtitle5, subtitle6)
                .forRelease(release)
                .matchesSubtitles(subtitle4);
    }

    private Settings createSettings(boolean keyword, boolean exact, boolean excludehearing) {
        Settings settings = mock(Settings.class);

        when(settings.isOptionSubtitleExactMatch()).thenReturn(exact);
        when(settings.isOptionSubtitleKeywordMatch()).thenReturn(keyword);
        when(settings.isOptionSubtitleExcludeHearingImpaired()).thenReturn(excludehearing);

        return settings;
    }

    private Release createRelease(String filename, String releasegroup) {
        Release release = mock(Release.class);

        when(release.getFileName()).thenReturn(filename);
        when(release.getExtension()).thenReturn("mkv");
        when(release.getReleaseGroup()).thenReturn(releasegroup);

        return release;
    }

    private Subtitle createSubtitle(String filename, String releaseGroup, boolean excludeHearing, String quality) {
        Subtitle subtitle = mock(Subtitle.class);

        when(subtitle.getFileName()).thenReturn(filename);
        when(subtitle.getReleaseGroup()).thenReturn(releaseGroup);
        when(subtitle.getQuality()).thenReturn(quality);
        when(subtitle.isHearingImpaired()).thenReturn(excludeHearing);

        return subtitle;
    }


    private TestSetupSubtitlesIntf assertThatFilter(SubtitleFiltering filter) {
        return new TestSetupFiltering().assertThatFilter(filter);
    }

    private interface TestSetupSubtitlesIntf {
        TestSetupReleaseIntf appliedOnSubtitles(Subtitle... subtitles);
    }


    private interface TestSetupReleaseIntf {
        TestSetupMatchesIntf forRelease(Release release);
    }

    private interface TestSetupMatchesIntf {
        void matchesSubtitles(Subtitle... subtitles);
    }

    private static class TestSetupFiltering implements TestSetupSubtitlesIntf, TestSetupReleaseIntf, TestSetupMatchesIntf {
        private SubtitleFiltering filter;
        private List<Subtitle> subtitles;
        private Release release;

        public TestSetupFiltering assertThatFilter(SubtitleFiltering filter) {
            this.filter = filter;
            return this;
        }

        public TestSetupFiltering appliedOnSubtitles(Subtitle... subtitles) {
            this.subtitles = Arrays.stream(subtitles).toList();
            return this;
        }

        public TestSetupFiltering forRelease(Release release) {
            this.release = release;
            return this;
        }

        public void matchesSubtitles(Subtitle... subtitles) {
            List<Subtitle> filteredSubtitles = this.subtitles.stream().filter(subtitle -> filter.useSubtitle(subtitle, release)).toList();
            assertThat(filteredSubtitles)
                    .withFailMessage("Expected the filtered subtitles to contain exactly %s, but found %s".formatted(
                            Arrays.stream(subtitles).map(Subtitle::getFileName).toList(),
                            filteredSubtitles.stream().map(Subtitle::getFileName).toList()))
                    .containsExactlyInAnyOrder(subtitles);
        }
    }
}
