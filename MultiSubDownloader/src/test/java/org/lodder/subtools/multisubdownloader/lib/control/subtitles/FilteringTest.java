package org.lodder.subtools.multisubdownloader.lib.control.subtitles;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class FilteringTest {

  protected Filtering filtering;

  @Before
  public void setUp() throws Exception {
    filtering = null;
  }

  @Test
  public void testExcudeImpairedHearingFiltering() {
    Settings settings = createSettings(false, false, true);

    filtering = new Filtering(settings);

    Release release =
        createRelease("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION", "mkv");
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    listFoundSubtitles.add(createSubtitle("", "", true, ""));
    listFoundSubtitles.add(createSubtitle("", "", false, ""));
    listFoundSubtitles.add(createSubtitle("", "", true, ""));

    List<Subtitle> filtered = filtering.getFiltered(listFoundSubtitles, release);

    assertEquals(filtered.size(), 1);

  }

  @Test
  public void testKeywordMatchFilter() {
    Release release =
        createRelease("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION", "mkv");
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.HDTV.XviD-AFG.srt", "AFG", false, ""));
    listFoundSubtitles.add(createSubtitle("criminal.minds.1012.hdtv-lol.srt", "lol", false, ""));
    listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt",
        "DIMENSION", true, ""));
    listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt",
        "DIMENSION", false, ""));
    listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt",
      "DIMENSION", false, "720p HDTV X264"));
    listFoundSubtitles.add(createSubtitle(
        "Criminal.Minds.S10E12.Anonymous.1080p.WEB-DL.DD5.1.H.264-CtrlHD", "CtrlHD", false, ""));

    // only keyword
    Settings settings = createSettings(true, false, false);
    filtering = new Filtering(settings);
    List<Subtitle> filtered = filtering.getFiltered(listFoundSubtitles, release);

    assertEquals(filtered.size(), 3);

    // keyword and exclude hearing impaired
    settings = createSettings(true, false, true);
    filtering = new Filtering(settings);
    filtered = filtering.getFiltered(listFoundSubtitles, release);

    assertEquals(filtered.size(), 2);
  }

  @Test
  public void testExactMatchFilter() {
    Release release =
        createRelease("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION", "mkv");
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.HDTV.XviD-AFG.srt", "AFG", false, ""));
    listFoundSubtitles.add(createSubtitle("criminal.minds.1012.hdtv-lol.srt", "lol", false, ""));
    listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt",
        "DIMENSION", true, ""));
    listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt",
        "DIMENSION", false, ""));
    listFoundSubtitles.add(createSubtitle(
        "Criminal.Minds.S10E12.Anonymous.1080p.WEB-DL.DD5.1.H.264-CtrlHD", "CtrlHD", false, ""));

    // only exact match
    Settings settings = createSettings(false, true, false);
    filtering = new Filtering(settings);
    List<Subtitle> filtered = filtering.getFiltered(listFoundSubtitles, release);

    assertEquals(filtered.size(), 2);

    // exact match and exclude hearing impaired
    settings = createSettings(false, true, true);
    filtering = new Filtering(settings);
    filtered = filtering.getFiltered(listFoundSubtitles, release);

    assertEquals(filtered.size(), 1);
  }

  @Test
  public void testExactMatchAndKeywordMatchFilter() {
    Release release =
        createRelease("Criminal.Minds.S10E12.Anonymous.720p.HDTV.X264-DIMENSION.mkv", "DIMENSION",
            "mkv");
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.HDTV.XviD-AFG.srt", "AFG", false, ""));
    listFoundSubtitles.add(createSubtitle("criminal.minds.1012.hdtv-lol.srt", "lol", false, ""));
    listFoundSubtitles.add(createSubtitle(
        "Criminal.Minds.S10E12.Anonymous.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", true, ""));
    listFoundSubtitles.add(createSubtitle(
        "Criminal.Minds.S10E12.Anonymous.720p.HDTV.X264-DIMENSION.srt", "DIMENSION", false, ""));
    listFoundSubtitles.add(createSubtitle("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.srt",
        "DIMENSION", false, ""));
    listFoundSubtitles.add(createSubtitle(
        "Criminal.Minds.S10E12.Anonymous.1080p.WEB-DL.DD5.1.H.264-CtrlHD", "CtrlHD", false,""));

    // only exact match
    Settings settings = createSettings(true, true, false);
    filtering = new Filtering(settings);
    List<Subtitle> filtered = filtering.getFiltered(listFoundSubtitles, release);

    assertEquals(filtered.size(), 2);

    // exact match and exclude hearing impaired
    settings = createSettings(true, true, true);
    filtering = new Filtering(settings);
    filtered = filtering.getFiltered(listFoundSubtitles, release);

    assertEquals(filtered.size(), 1);
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

    when(release.getFilename()).thenReturn(filename);
    when(release.getExtension()).thenReturn(extension);
    when(release.getReleasegroup()).thenReturn(releasegroup);

    return release;
  }

  private Subtitle createSubtitle(String filename, String releasegroup, boolean excludehearing, String quality) {
    Subtitle subtitle = mock(Subtitle.class);

    when(subtitle.getFilename()).thenReturn(filename);
    when(subtitle.getReleasegroup()).thenReturn(releasegroup);
    when(subtitle.getQuality()).thenReturn(quality);
    when(subtitle.isHearingImpaired()).thenReturn(excludehearing);

    return subtitle;
  }

}
