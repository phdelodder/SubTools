package org.lodder.subtools.sublibrary.control;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoType;

class ReleaseParserTest {

    @Test
    void testReleaseGroup() throws Exception {
        String releaseGroup = ReleaseParser.extractReleasegroup("The.Following.S03E01.HDTV.XviD-AFG", false);
        assertThat(releaseGroup).isEqualTo("AFG");

        releaseGroup = ReleaseParser.extractReleasegroup("The.Following.S03E01.HDTV.XviD-AFG", true);
        assertThat(releaseGroup).isEqualTo("A");

        releaseGroup = ReleaseParser.extractReleasegroup("The.Following.S03E01.HDTV.XviD-AFG.srt", false);
        assertThat(releaseGroup).isEqualTo("");

        releaseGroup = ReleaseParser.extractReleasegroup("The.Following.S03E01.HDTV.XviD-AFG.srt", true);
        assertThat(releaseGroup).isEqualTo("AFG");
    }

    @Test
    void testListGetQualityKeyWords() throws Exception {
        ReleaseParser releaseparser = new ReleaseParser();

        Path file = Path.of("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv");
        Release release = releaseparser.parse(file);

        List<String> q = ReleaseParser.getQualityKeyWords(release.getQuality());

        assertThat(q).containsExactly("720p", "hdtv", "x264");

        file = Path.of("The.Drop.2014.1080p.WEB-DL.DD5.1.H264-RARBG.mkv");
        release = releaseparser.parse(file);

        q = ReleaseParser.getQualityKeyWords(release.getQuality());

        assertThat(q).containsExactly("1080p", "web-dl", "dd5 1", "h264");
    }

    @Test
    void testTV() throws Exception {
        ReleaseParser releaseparser = new ReleaseParser();

        Path file = Path.of("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv");
        Release release = releaseparser.parse(file);

        assertThat(release.getVideoType()).isEqualTo(VideoType.EPISODE);
        assertThat(release.getExtension()).isEqualTo("mkv");
        assertThat(release.getFileName()).isEqualTo("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv");
        assertThat(release.getReleaseGroup()).isEqualTo("DIMENSION");
        assertThat(release.getQuality()).isEqualTo("720p hdtv x264");

        TvRelease tvrelease = (TvRelease) release;

        assertThat(tvrelease.getSeason()).isEqualTo(10);
        assertThat(tvrelease.getEpisodeNumbers().size()).isEqualTo(1);
        assertThat((int) tvrelease.getEpisodeNumbers().get(0)).isEqualTo(12);

        file = Path.of("S04E02 - White Collar - Most Wanted.mkv");
        release = releaseparser.parse(file);

        assertThat(release.getVideoType()).isEqualTo(VideoType.EPISODE);
        assertThat(release.getExtension()).isEqualTo("mkv");
        assertThat(release.getFileName()).isEqualTo("S04E02 - White Collar - Most Wanted.mkv");
        assertThat(release.getReleaseGroup()).isEqualTo("");
        assertThat(release.getQuality()).isEqualTo("");

        tvrelease = (TvRelease) release;

        assertThat(tvrelease.getSeason()).isEqualTo(4);
        assertThat(tvrelease.getEpisodeNumbers().size()).isEqualTo(1);
        assertThat((int) tvrelease.getEpisodeNumbers().get(0)).isEqualTo(2);

        file = Path.of("Spartacus.Gods.of.The.Arena.Pt.I.720p.HDTV.X264-DIMENSION.mkv");
        release = releaseparser.parse(file);

        assertThat(release.getVideoType()).isEqualTo(VideoType.EPISODE);
        assertThat(release.getExtension()).isEqualTo("mkv");
        assertEquals(release.getFileName(),
                "Spartacus.Gods.of.The.Arena.Pt.I.720p.HDTV.X264-DIMENSION.mkv");
        assertThat(release.getReleaseGroup()).isEqualTo("DIMENSION");
        assertThat(release.getQuality()).isEqualTo("720p hdtv x264");

        tvrelease = (TvRelease) release;

        assertThat(tvrelease.getSeason()).isEqualTo(1);
        assertThat(tvrelease.getEpisodeNumbers().size()).isEqualTo(1);
        assertThat((int) tvrelease.getEpisodeNumbers().get(0)).isEqualTo(1);

        file = Path.of("hawaii.five-0.2010.410.hdtv-lol.mp4");
        release = releaseparser.parse(file);

        assertThat(release.getVideoType()).isEqualTo(VideoType.EPISODE);
        assertThat(release.getExtension()).isEqualTo("mp4");
        assertThat(release.getFileName()).isEqualTo("hawaii.five-0.2010.410.hdtv-lol.mp4");
        assertThat(release.getReleaseGroup()).isEqualTo("lol");
        assertThat(release.getQuality()).isEqualTo("hdtv");

        tvrelease = (TvRelease) release;

        assertThat(tvrelease.getSeason()).isEqualTo(4);
        assertThat(tvrelease.getEpisodeNumbers().size()).isEqualTo(1);
        assertThat((int) tvrelease.getEpisodeNumbers().get(0)).isEqualTo(10);

        file = Path.of("Greys.Anatomy.S10E01E02.720p.HDTV.X264-DIMENSION.mkv");
        release = releaseparser.parse(file);

        assertThat(release.getVideoType()).isEqualTo(VideoType.EPISODE);
        assertThat(release.getExtension()).isEqualTo("mkv");
        assertThat(release.getFileName()).isEqualTo("Greys.Anatomy.S10E01E02.720p.HDTV.X264-DIMENSION.mkv");
        assertThat(release.getReleaseGroup()).isEqualTo("DIMENSION");
        assertThat(release.getQuality()).isEqualTo("720p hdtv x264");

        tvrelease = (TvRelease) release;

        assertThat(tvrelease.getSeason()).isEqualTo(10);
        assertThat(tvrelease.getEpisodeNumbers().size()).isEqualTo(2);
        assertThat((int) tvrelease.getEpisodeNumbers().get(0)).isEqualTo(1);
        assertThat((int) tvrelease.getEpisodeNumbers().get(1)).isEqualTo(2);
    }

    @Test
    void testReleaseParseExceptionMessage() throws ReleaseParseException {
        Path file = Path.of("exceptiontesting.mkv");

        assertThatExceptionOfType(ReleaseParseException.class)
                .isThrownBy(() -> new ReleaseParser().parse(file))
                .withMessage("Unknown format, can't be parsed: " + file.toAbsolutePath());
    }

    @Test
    void testMovie() throws Exception {
        ReleaseParser releaseparser = new ReleaseParser();

        Path file = Path.of("Back.to.the.Future.Part.II.1989.720p.BluRay.X264-AMIABLE.mkv");
        Release release = releaseparser.parse(file);

        assertThat(release.getVideoType()).isEqualTo(VideoType.MOVIE);
        assertThat(release.getExtension()).isEqualTo("mkv");
        assertThat(release.getFileName()).isEqualTo("Back.to.the.Future.Part.II.1989.720p.BluRay.X264-AMIABLE.mkv");
        assertThat(release.getReleaseGroup()).isEqualTo("AMIABLE");
        assertThat(release.getQuality()).isEqualTo("720p bluray x264");

        MovieRelease movieRelease = (MovieRelease) release;

        assertThat((int) movieRelease.getYear()).isEqualTo(1989);
        assertThat(movieRelease.getName()).isEqualTo("Back to the Future Part II");

        file = Path.of("The.Equalizer.2014.720p.BluRay.x264-SPARKS.mkv");
        release = releaseparser.parse(file);

        assertThat(release.getVideoType()).isEqualTo(VideoType.MOVIE);
        assertThat(release.getExtension()).isEqualTo("mkv");
        assertThat(release.getFileName()).isEqualTo("The.Equalizer.2014.720p.BluRay.x264-SPARKS.mkv");
        assertThat(release.getReleaseGroup()).isEqualTo("SPARKS");
        assertThat(release.getQuality()).isEqualTo("720p bluray x264");

        movieRelease = (MovieRelease) release;

        assertThat((int) movieRelease.getYear()).isEqualTo(2014);
        assertThat(movieRelease.getName()).isEqualTo("The Equalizer");

        file = Path.of("The.Trip.to.Italy.2014.LIMITED.720p.BluRay.x264-GECKOS.mkv");
        release = releaseparser.parse(file);

        assertThat(release.getVideoType()).isEqualTo(VideoType.MOVIE);
        assertThat(release.getExtension()).isEqualTo("mkv");
        assertEquals(release.getFileName(),
                "The.Trip.to.Italy.2014.LIMITED.720p.BluRay.x264-GECKOS.mkv");
        assertThat(release.getReleaseGroup()).isEqualTo("GECKOS");
        assertThat(release.getQuality()).isEqualTo("720p bluray x264");

        movieRelease = (MovieRelease) release;

        assertThat((int) movieRelease.getYear()).isEqualTo(2014);
        assertThat(movieRelease.getName()).isEqualTo("The Trip to Italy");
    }
}
