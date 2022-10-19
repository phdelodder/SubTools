package org.lodder.subtools.sublibrary.control;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
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
    public void testReleaseGroup() throws Exception {
        String releaseGroup =
                ReleaseParser.extractReleasegroup("The.Following.S03E01.HDTV.XviD-AFG", false);
        assertEquals(releaseGroup, "AFG");

        releaseGroup = ReleaseParser.extractReleasegroup("The.Following.S03E01.HDTV.XviD-AFG", true);
        assertEquals(releaseGroup, "A");

        releaseGroup =
                ReleaseParser.extractReleasegroup("The.Following.S03E01.HDTV.XviD-AFG.srt", false);
        assertEquals(releaseGroup, "");

        releaseGroup =
                ReleaseParser.extractReleasegroup("The.Following.S03E01.HDTV.XviD-AFG.srt", true);
        assertEquals(releaseGroup, "AFG");
    }

    @Test
    public void testListGetQualityKeyWords() throws Exception {
        releaseparser = new ReleaseParser();

        File file = new File("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv");
        Release release = releaseparser.parse(file);

        List<String> q = ReleaseParser.getQualityKeyWords(release.getQuality());

        assertEquals(q.get(0), "720p");
        assertEquals(q.get(1), "hdtv");
        assertEquals(q.get(2), "x264");

        file = new File("The.Drop.2014.1080p.WEB-DL.DD5.1.H264-RARBG.mkv");
        release = releaseparser.parse(file);

        q = ReleaseParser.getQualityKeyWords(release.getQuality());

        assertEquals(q.get(0), "1080p");
        assertEquals(q.get(1), "web-dl");
        assertEquals(q.get(2), "dd5 1");
        assertEquals(q.get(3), "h264");
    }

    @Test
    public void testTV() throws Exception {
        releaseparser = new ReleaseParser();

        File file = new File("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv");
        Release release = releaseparser.parse(file);

        assertSame(release.getVideoType(), VideoType.EPISODE);
        assertEquals(release.getExtension(), "mkv");
        assertEquals(release.getFileName(), "Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv");
        assertEquals(release.getReleaseGroup(), "DIMENSION");
        assertEquals(release.getQuality(), "720p HDTV X264");

        TvRelease tvrelease = (TvRelease) release;

        assertEquals(tvrelease.getSeason(), 10);
        assertEquals(tvrelease.getEpisodeNumbers().size(), 1);
        assertEquals((int) tvrelease.getEpisodeNumbers().get(0), 12);

        file = new File("S04E02 - White Collar - Most Wanted.mkv");
        release = releaseparser.parse(file);

        assertSame(release.getVideoType(), VideoType.EPISODE);
        assertEquals(release.getExtension(), "mkv");
        assertEquals(release.getFileName(), "S04E02 - White Collar - Most Wanted.mkv");
        assertEquals(release.getReleaseGroup(), "");
        assertEquals(release.getQuality(), "");

        tvrelease = (TvRelease) release;

        assertEquals(tvrelease.getSeason(), 4);
        assertEquals(tvrelease.getEpisodeNumbers().size(), 1);
        assertEquals((int) tvrelease.getEpisodeNumbers().get(0), 2);

        file = new File("Spartacus.Gods.of.The.Arena.Pt.I.720p.HDTV.X264-DIMENSION.mkv");
        release = releaseparser.parse(file);

        assertSame(release.getVideoType(), VideoType.EPISODE);
        assertEquals(release.getExtension(), "mkv");
        assertEquals(release.getFileName(),
                "Spartacus.Gods.of.The.Arena.Pt.I.720p.HDTV.X264-DIMENSION.mkv");
        assertEquals(release.getReleaseGroup(), "DIMENSION");
        assertEquals(release.getQuality(), "720p HDTV X264");

        tvrelease = (TvRelease) release;

        assertEquals(tvrelease.getSeason(), 1);
        assertEquals(tvrelease.getEpisodeNumbers().size(), 1);
        assertEquals((int) tvrelease.getEpisodeNumbers().get(0), 1);

        file = new File("hawaii.five-0.2010.410.hdtv-lol.mp4");
        release = releaseparser.parse(file);

        assertSame(release.getVideoType(), VideoType.EPISODE);
        assertEquals(release.getExtension(), "mp4");
        assertEquals(release.getFileName(), "hawaii.five-0.2010.410.hdtv-lol.mp4");
        assertEquals(release.getReleaseGroup(), "lol");
        assertEquals(release.getQuality(), "hdtv");

        tvrelease = (TvRelease) release;

        assertEquals(tvrelease.getSeason(), 4);
        assertEquals(tvrelease.getEpisodeNumbers().size(), 1);
        assertEquals((int) tvrelease.getEpisodeNumbers().get(0), 10);

        file = new File("Greys.Anatomy.S10E01E02.720p.HDTV.X264-DIMENSION.mkv");
        release = releaseparser.parse(file);

        assertSame(release.getVideoType(), VideoType.EPISODE);
        assertEquals(release.getExtension(), "mkv");
        assertEquals(release.getFileName(), "Greys.Anatomy.S10E01E02.720p.HDTV.X264-DIMENSION.mkv");
        assertEquals(release.getReleaseGroup(), "DIMENSION");
        assertEquals(release.getQuality(), "720p HDTV X264");

        tvrelease = (TvRelease) release;

        assertEquals(tvrelease.getSeason(), 10);
        assertEquals(tvrelease.getEpisodeNumbers().size(), 2);
        assertEquals((int) tvrelease.getEpisodeNumbers().get(0), 1);
        assertEquals((int) tvrelease.getEpisodeNumbers().get(1), 2);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testReleaseParseExceptionMessage() throws ReleaseParseException {
        File file = new File("exceptiontesting.mkv");
        releaseparser = new ReleaseParser();

        thrown.expect(ReleaseParseException.class);
        thrown.expectMessage("Unknow format, can't be parsed: " + file.getAbsolutePath());
        releaseparser.parse(file);

        fail("Expected an ReleaseParseException to be thrown");
    }

    @Test
    public void testMovie() throws Exception {
        releaseparser = new ReleaseParser();

        File file = new File("Back.to.the.Future.Part.II.1989.720p.BluRay.X264-AMIABLE.mkv");
        Release release = releaseparser.parse(file);

        assertSame(release.getVideoType(), VideoType.MOVIE);
        assertEquals(release.getExtension(), "mkv");
        assertEquals(release.getFileName(),
                "Back.to.the.Future.Part.II.1989.720p.BluRay.X264-AMIABLE.mkv");
        assertEquals(release.getReleaseGroup(), "AMIABLE");
        assertEquals(release.getQuality(), "720p BluRay X264");

        MovieRelease movieRelease = (MovieRelease) release;

        assertEquals((int) movieRelease.getYear(), 1989);
        assertEquals(movieRelease.getName(), "Back to the Future Part II");

        file = new File("The.Equalizer.2014.720p.BluRay.x264-SPARKS.mkv");
        release = releaseparser.parse(file);

        assertSame(release.getVideoType(), VideoType.MOVIE);
        assertEquals(release.getExtension(), "mkv");
        assertEquals(release.getFileName(), "The.Equalizer.2014.720p.BluRay.x264-SPARKS.mkv");
        assertEquals(release.getReleaseGroup(), "SPARKS");
        assertEquals(release.getQuality(), "720p BluRay x264");

        movieRelease = (MovieRelease) release;

        assertEquals((int) movieRelease.getYear(), 2014);
        assertEquals(movieRelease.getName(), "The Equalizer");

        file = new File("The.Trip.to.Italy.2014.LIMITED.720p.BluRay.x264-GECKOS.mkv");
        release = releaseparser.parse(file);

        assertSame(release.getVideoType(), VideoType.MOVIE);
        assertEquals(release.getExtension(), "mkv");
        assertEquals(release.getFileName(),
                "The.Trip.to.Italy.2014.LIMITED.720p.BluRay.x264-GECKOS.mkv");
        assertEquals(release.getReleaseGroup(), "GECKOS");
        assertEquals(release.getQuality(), "720p BluRay x264");

        movieRelease = (MovieRelease) release;

        assertEquals((int) movieRelease.getYear(), 2014);
        assertEquals(movieRelease.getName(), "The Trip to Italy");

    }

}
