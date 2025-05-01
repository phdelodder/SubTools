package org.lodder.subtools.sublibrary.control;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.*;
import static org.lodder.subtools.sublibrary.assertions.SubLibraryAssertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.model.Release;

class ReleaseParserTest {

    @Test
    void testReleaseGroup() {
        String releaseGroup = ReleaseParser.extractReleaseGroup("The.Following.S03E01.HDTV.XviD-AFG", false);
        assertThat(releaseGroup).isEqualTo("AFG");

        releaseGroup = ReleaseParser.extractReleaseGroup("The.Following.S03E01.HDTV.XviD-AFG", true);
        assertThat(releaseGroup).isEmpty();

        releaseGroup = ReleaseParser.extractReleaseGroup("The.Following.S03E01.HDTV.XviD-AFG.srt", false);
        assertThat(releaseGroup).isEmpty();

        releaseGroup = ReleaseParser.extractReleaseGroup("The.Following.S03E01.HDTV.XviD-AFG.srt", true);
        assertThat(releaseGroup).isEqualTo("AFG");
    }

    @Test
    void testListGetQualityKeyWords() throws Exception {
        ReleaseParser releaseparser = new ReleaseParser();

        Path file = Path.of("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv");
        Release release = releaseparser.parse(file);

        assertThat(ReleaseParser.getQualityKeyWords(release.quality)).containsExactly("720p", "hdtv", "x264");

        file = Path.of("The.Drop.2014.1080p.WEB-DL.DD5.1.H264-RARBG.mkv");
        release = releaseparser.parse(file);

        assertThat(ReleaseParser.getQualityKeyWords(release.quality))
            .containsExactly("1080p", "web-dl", "dd5.1", "x264");
    }

    @Nested
    class testTV {

        private final ReleaseParser releaseparser = new ReleaseParser();

        @Nested
        class StartsWithSeasonEpisode {


            @Test
            void StartsWithSeasonEpisode1() throws Exception {
                Path file = Path.of("S04E02 - White Collar - Most Wanted.mkv");
                Release release = releaseparser.parse(file);

                assertThat(release)
                    .isSerie()
                    .hasEpisodeVideoType()
                    .hasExtension("mkv")
                    .hasFileName("S04E02 - White Collar - Most Wanted.mkv")
                    .withoutReleaseGroup()
                    .withoutQuality()
                    .hasSeason(4)
                    .hasEpisodes(2)
                    .hasName("White Collar")
                    .hasTitle("Most Wanted");
            }

            @Test
            void StartsWithSeasonEpisode2() throws Exception {
                Path file = Path.of("(S04E02) - White Collar - Most Wanted.mkv");
                Release release = releaseparser.parse(file);

                assertThat(release)
                    .isSerie()
                    .hasEpisodeVideoType()
                    .hasExtension("mkv")
                    .hasFileName("(S04E02) - White Collar - Most Wanted.mkv")
                    .withoutReleaseGroup()
                    .withoutQuality()
                    .hasSeason(4)
                    .hasEpisodes(2)
                    .hasName("White Collar")
                    .hasTitle("Most Wanted");
            }

            @Test
            void StartsWithSeasonEpisode3() throws Exception {
                Path file = Path.of("(S04E02) White Collar - Most Wanted.mkv");
                Release release = releaseparser.parse(file);

                assertThat(release)
                    .isSerie()
                    .hasEpisodeVideoType()
                    .hasExtension("mkv")
                    .hasFileName("(S04E02) White Collar - Most Wanted.mkv")
                    .withoutReleaseGroup()
                    .withoutQuality()
                    .hasSeason(4)
                    .hasEpisodes(2)
                    .hasName("White Collar")
                    .hasTitle("Most Wanted");
            }
        }


        @Test
        void testTV1() throws Exception {
            Path file = Path.of("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isSerie()
                .hasEpisodeVideoType()
                .hasExtension("mkv")
                .hasFileName("Criminal.Minds.S10E12.720p.HDTV.X264-DIMENSION.mkv")
                .hasReleaseGroup("DIMENSION")
                .hasQuality("720p hdtv x264")
                .hasSeason(10)
                .hasEpisodes(12)
                .hasName("Criminal Minds")
                .withoutTitle();
        }

        @Test
        void testTV4() throws Exception {
            Path file = Path.of("Spartacus.Gods.of.The.Arena.Pt.IV.720p.HDTV.X264-DIMENSION.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isSerie()
                .hasEpisodeVideoType()
                .hasExtension("mkv")
                .hasFileName("Spartacus.Gods.of.The.Arena.Pt.IV.720p.HDTV.X264-DIMENSION.mkv")
                .hasReleaseGroup("DIMENSION")
                .hasQuality("720p hdtv x264")
                .hasSeason(1)
                .hasEpisodes(4)
                .hasName("Spartacus Gods of The Arena")
                .withoutTitle();
        }

        @Test
        void testTV5() throws Exception {
            Path file = Path.of("hawaii.five-0.2010.410.hdtv-lol.mp4");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isSerie()
                .hasEpisodeVideoType()
                .hasExtension("mp4")
                .hasFileName("hawaii.five-0.2010.410.hdtv-lol.mp4")
                .hasReleaseGroup("lol")
                .hasQuality("hdtv")
                .hasSeason(4)
                .hasEpisodes(10)
                .hasName("hawaii five-0")
                .withoutTitle();
        }

        @Test
        void testTV6() throws Exception {
            Path file = Path.of("Greys.Anatomy.S10E01E02.720p.HDTV.X264-DIMENSION.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isSerie()
                .hasEpisodeVideoType()
                .hasExtension("mkv")
                .hasFileName("Greys.Anatomy.S10E01E02.720p.HDTV.X264-DIMENSION.mkv")
                .hasReleaseGroup("DIMENSION")
                .hasQuality("720p hdtv x264")
                .hasSeason(10)
                .hasEpisodes(1, 2)
                .hasName("Greys Anatomy")
                .withoutTitle();
        }

        @Test
        void testTV7() throws Exception {
            Path file = Path.of("Greys.Anatomy.S10E01E02 Seal Our Fate 720p.HDTV.X264-DIMENSION.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isSerie()
                .hasEpisodeVideoType()
                .hasExtension("mkv")
                .hasFileName("Greys.Anatomy.S10E01E02 Seal Our Fate 720p.HDTV.X264-DIMENSION.mkv")
                .hasReleaseGroup("DIMENSION")
                .hasQuality("720p hdtv x264")
                .hasSeason(10)
                .hasEpisodes(1, 2)
                .hasName("Greys Anatomy")
                .hasTitle("Seal Our Fate");
        }


        @Test
        void testTV8() throws Exception {
            Path file = Path.of("(2-11) Joey and the High School Friend.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isSerie()
                .hasEpisodeVideoType()
                .hasExtension("mkv")
                .hasFileName("(2-11) Joey and the High School Friend.mkv")
                .withoutReleaseGroup()
                .withoutQuality()
                .hasSeason(2)
                .hasEpisodes(11)
                .hasName("Joey and the High School Friend")
                .withoutTitle();
        }

        @Test
        void testTV9() throws Exception {
            Path file = Path.of("The.Boys.S04E05.Beware.the.jabberwock.my.son.1080p.web.dl.hevc.x265.rmteam.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isSerie()
                .hasEpisodeVideoType()
                .hasExtension("mkv")
                .hasFileName("The.Boys.S04E05.Beware.the.jabberwock.my.son.1080p.web.dl.hevc.x265.rmteam.mkv")
                .hasReleaseGroup("rmteam")
                .hasQuality("1080p web-dl x265")
                .hasSeason(4)
                .hasEpisodes(5)
                .hasName("The Boys")
                .hasTitle("Beware the jabberwock my son");
        }
    }

    @Test
    void testReleaseParseExceptionMessage() {
        Path file = Path.of("exceptiontesting.mkv");

        assertThatExceptionOfType(ReleaseParseException.class).isThrownBy(() -> new ReleaseParser().parse(file))
            .withMessage("Unknown format, can't be parsed: " + file.toAbsolutePath());
    }

    @Nested
    class TestMovie {
        private final ReleaseParser releaseparser = new ReleaseParser();

        @Test
        void testMovie1() throws Exception {
            Path file = Path.of("Back.to.the.Future.Part.II.1989.720p.BluRay.X264-AMIABLE.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isMovie()
                .hasMovieVideoType()
                .hasExtension("mkv")
                .hasFileName("Back.to.the.Future.Part.II.1989.720p.BluRay.X264-AMIABLE.mkv")
                .hasReleaseGroup("AMIABLE")
                .hasQuality("720p bluray x264")
                .hasYear(1989)
                .hasName("Back to the Future Part II");
        }

        @Test
        void testMovie2() throws Exception {
            Path file = Path.of("Back.to.the.Future.Part.21.1989.720p.BluRay.X264-AMIABLE.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isMovie()
                .hasMovieVideoType()
                .hasExtension("mkv")
                .hasFileName("Back.to.the.Future.Part.21.1989.720p.BluRay.X264-AMIABLE.mkv")
                .hasReleaseGroup("AMIABLE")
                .hasQuality("720p bluray x264")
                .hasYear(1989)
                .hasName("Back to the Future Part 21");
        }

        @Test
        void testMovie3() throws Exception {
            Path file = Path.of("The.Equalizer.2014.720p.BluRay.x264-SPARKS.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isMovie()
                .hasMovieVideoType()
                .hasExtension("mkv")
                .hasFileName("The.Equalizer.2014.720p.BluRay.x264-SPARKS.mkv")
                .hasReleaseGroup("SPARKS")
                .hasQuality("720p bluray x264")
                .hasYear(2014)
                .hasName("The Equalizer");
        }

        @Test
        void testMovie4() throws Exception {
            Path file = Path.of("The.Trip.to.Italy.2014.LIMITED.720p.BluRay.x264-GECKOS.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isMovie()
                .hasMovieVideoType()
                .hasExtension("mkv")
                .hasFileName("The.Trip.to.Italy.2014.LIMITED.720p.BluRay.x264-GECKOS.mkv")
                .hasReleaseGroup("GECKOS")
                .hasQuality("720p bluray x264")
                .hasYear(2014)
                .hasName("The Trip to Italy");
        }

        @Test
        void testMovie5() throws Exception {
            Path file = Path.of("Final.Destination.5.720p.Bluray.x264-TWiZTED.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isMovie()
                .hasMovieVideoType()
                .hasExtension("mkv")
                .hasFileName("Final.Destination.5.720p.Bluray.x264-TWiZTED.mkv")
                .hasReleaseGroup("TWiZTED")
                .hasQuality("720p bluray x264")
                .withoutYear()
                .hasName("Final Destination 5");
        }

        @Test
        void testMovie6() throws Exception {
            Path file = Path.of("Final.Destination.5.2011.720p.Bluray.x264-TWiZTED.mkv");
            Release release = releaseparser.parse(file);

            assertThat(release)
                .isMovie()
                .hasMovieVideoType()
                .hasExtension("mkv")
                .hasFileName("Final.Destination.5.2011.720p.Bluray.x264-TWiZTED.mkv")
                .hasReleaseGroup("TWiZTED")
                .hasQuality("720p bluray x264")
                .hasYear(2011)
                .hasName("Final Destination 5");
        }
    }
}
