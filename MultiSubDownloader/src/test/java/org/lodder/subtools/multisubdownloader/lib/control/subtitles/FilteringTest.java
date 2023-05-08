package org.lodder.subtools.multisubdownloader.lib.control.subtitles;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

class FilteringTest {

    @Test
    void testExcudeImpairedHearingFiltering() {
        Settings settings = createSettings(false, false, true);

        Filtering filtering = new Filtering(settings);

        Release release = createRelease("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION", "mkv");
        List<Subtitle> listFoundSubtitles = new ArrayList<>();
        listFoundSubtitles.add(createSubtitle("", "", true, ""));
        listFoundSubtitles.add(createSubtitle("", "", false, ""));
        listFoundSubtitles.add(createSubtitle("", "", true, ""));

        List<Subtitle> filtered = filtering.getFiltered(listFoundSubtitles, release);

        assertThat(filtered).hasSize(1);

    }

    @Test
    void testKeywordMatchFilter() {
        Release release = createRelease("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION", "mkv");
        List<Subtitle> listFoundSubtitles = new ArrayList<>();
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.HDTV.XviD-AFG.srt", "AFG", false, ""));
        listFoundSubtitles.add(createSubtitle("criminal.minds.1012.hdtv-lol.srt", "lol", false, ""));
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", true, ""));
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", false, ""));
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", false, "720p HDTV X264"));
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.Anonymous.1080p.WEB-DL.DD5.1.H.264-CtrlHD", "CtrlHD", false, ""));

        // only keyword
        Settings settings = createSettings(true, false, false);
        Filtering filtering = new Filtering(settings);
        List<Subtitle> filtered = filtering.getFiltered(listFoundSubtitles, release);

        assertThat(filtered).hasSize(3);

        // keyword and exclude hearing impaired
        settings = createSettings(true, false, true);
        filtering = new Filtering(settings);
        filtered = filtering.getFiltered(listFoundSubtitles, release);

        assertThat(filtered).hasSize(2);
    }

    @Test
    void testExactMatchFilter() {
        Release release = createRelease("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION", "mkv");
        List<Subtitle> listFoundSubtitles = new ArrayList<>();
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.HDTV.XviD-AFG.srt", "AFG", false, ""));
        listFoundSubtitles.add(createSubtitle("criminal.minds.1012.hdtv-lol.srt", "lol", false, ""));
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", true, ""));
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", false, ""));
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.Anonymous.1080p.WEB-DL.DD5.1.H.264-CtrlHD", "CtrlHD", false, ""));

        // only exact match
        Settings settings = createSettings(false, true, false);
        Filtering filtering = new Filtering(settings);
        List<Subtitle> filtered = filtering.getFiltered(listFoundSubtitles, release);

        assertThat(filtered).hasSize(2);

        // exact match and exclude hearing impaired
        settings = createSettings(false, true, true);
        filtering = new Filtering(settings);
        filtered = filtering.getFiltered(listFoundSubtitles, release);

        assertThat(filtered).hasSize(1);
    }

    @Test
    void testExactMatchAndKeywordMatchFilter() {
        Release release = createRelease("Criminal.Minds.S10E12.Anonymous.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION", "mkv");
        List<Subtitle> listFoundSubtitles = new ArrayList<>();
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.HDTV.XviD-AFG.srt", "AFG", false, ""));
        listFoundSubtitles.add(createSubtitle("criminal.minds.1012.hdtv-lol.srt", "lol", false, ""));
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.Anonymous.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", true, ""));
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.Anonymous.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", false, ""));
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", false, ""));
        listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.Anonymous.1080p.WEB-DL.DD5.1.H.264-CtrlHD", "CtrlHD", false, ""));

        // only exact match
        Settings settings = createSettings(true, true, false);
        Filtering filtering = new Filtering(settings);
        List<Subtitle> filtered = filtering.getFiltered(listFoundSubtitles, release);

        assertThat(filtered).hasSize(2);

        // exact match and exclude hearing impaired
        settings = createSettings(true, true, true);
        filtering = new Filtering(settings);
        filtered = filtering.getFiltered(listFoundSubtitles, release);

        assertThat(filtered).hasSize(1);
    }

    private Settings createSettings(boolean keyword, boolean exact, boolean excludehearing) {
        Settings settings = mock(Settings.class);

        when(settings.isOptionSubtitleExactMatch()).thenReturn(exact);
        when(settings.isOptionSubtitleKeywordMatch()).thenReturn(keyword);
        when(settings.isOptionSubtitleExcludeHearingImpaired()).thenReturn(excludehearing);

        return settings;
    }

    private Release createRelease(String filename, String releasegroup, String extension) {
        Release release = mock(Release.class);

        when(release.getFileName()).thenReturn(filename);
        when(release.getExtension()).thenReturn(extension);
        when(release.getReleaseGroup()).thenReturn(releasegroup);

        return release;
    }

    private Subtitle createSubtitle(String filename, String releasegroup, boolean excludehearing, String quality) {
        Subtitle subtitle = mock(Subtitle.class);

        when(subtitle.getFileName()).thenReturn(filename);
        when(subtitle.getReleaseGroup()).thenReturn(releasegroup);
        when(subtitle.getQuality()).thenReturn(quality);
        when(subtitle.isHearingImpaired()).thenReturn(excludehearing);

        return subtitle;
    }

}
