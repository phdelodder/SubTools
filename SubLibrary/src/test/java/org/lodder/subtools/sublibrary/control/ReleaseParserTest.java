package org.lodder.subtools.sublibrary.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoType;

public class ReleaseParserTest {

  protected ReleaseParser releaseparser;

  @Before
  public void setUp() throws Exception {
    releaseparser = null;
  }

  @Test
  public void testTV() throws Exception {
    releaseparser = new ReleaseParser();

    File basedir = new File("/tmp/Serie/");
    File file = new File("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv");
    Release release = releaseparser.parse(file, basedir);

    assertSame(release.getVideoType(), VideoType.EPISODE);
    assertEquals(release.getExtension(), "mkv");
    assertEquals(release.getFilename(), "Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv");
    assertEquals(release.getReleasegroup(), "DIMENSION");
    assertEquals(release.getQuality(), "720p HDTV X264");

    TvRelease tvrelease = (TvRelease) release;

    assertEquals(tvrelease.getSeason(), 10);
    assertEquals(tvrelease.getEpisodeNumbers().size(), 1);
    assertEquals((int) tvrelease.getEpisodeNumbers().get(0), 12);

    file = new File("S04E02 - White Collar - Most Wanted.mkv");
    release = releaseparser.parse(file, basedir);

    assertSame(release.getVideoType(), VideoType.EPISODE);
    assertEquals(release.getExtension(), "mkv");
    assertEquals(release.getFilename(), "S04E02 - White Collar - Most Wanted.mkv");
    assertEquals(release.getReleasegroup(), "");
    assertEquals(release.getQuality(), "");

    tvrelease = (TvRelease) release;

    assertEquals(tvrelease.getSeason(), 4);
    assertEquals(tvrelease.getEpisodeNumbers().size(), 1);
    assertEquals((int) tvrelease.getEpisodeNumbers().get(0), 2);

    file = new File("Spartacus.Gods.of.The.Arena.Pt.I.720p.HDTV.X264-DIMENSION.mkv");
    release = releaseparser.parse(file, basedir);

    assertSame(release.getVideoType(), VideoType.EPISODE);
    assertEquals(release.getExtension(), "mkv");
    assertEquals(release.getFilename(),
        "Spartacus.Gods.of.The.Arena.Pt.I.720p.HDTV.X264-DIMENSION.mkv");
    assertEquals(release.getReleasegroup(), "DIMENSION");
    assertEquals(release.getQuality(), "720p HDTV X264");

    tvrelease = (TvRelease) release;

    assertEquals(tvrelease.getSeason(), 1);
    assertEquals(tvrelease.getEpisodeNumbers().size(), 1);
    assertEquals((int) tvrelease.getEpisodeNumbers().get(0), 1);

    file = new File("hawaii.five-0.2010.410.hdtv-lol.mp4");
    release = releaseparser.parse(file, basedir);

    assertSame(release.getVideoType(), VideoType.EPISODE);
    assertEquals(release.getExtension(), "mp4");
    assertEquals(release.getFilename(), "hawaii.five-0.2010.410.hdtv-lol.mp4");
    assertEquals(release.getReleasegroup(), "lol");
    assertEquals(release.getQuality(), "hdtv");

    tvrelease = (TvRelease) release;

    assertEquals(tvrelease.getSeason(), 4);
    assertEquals(tvrelease.getEpisodeNumbers().size(), 1);
    assertEquals((int) tvrelease.getEpisodeNumbers().get(0), 10);

  }

}
