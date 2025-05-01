package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;

class ScoreCalculatorTest {

    @Test
    void test_it_calculates_the_score_for_subtitle() {
        SortWeight weights = createWeights("DVDRip XviD", "MEDiEVAL");

        ScoreCalculator calculator = new ScoreCalculator(weights);
        Subtitle subtitle;

        subtitle = createSubtitle("Arrested.Development.S01E01.DVDRip.XviD-MEDiEVAL.srt", "dvdrip xvid", "medieval");
        assertThat(calculator.calculate(subtitle)).isEqualTo(100);

        subtitle = createSubtitle("Arrested Development 1x1 works with medieval.srt", "", "medieval");
        assertThat(calculator.calculate(subtitle)).isEqualTo(63);

        subtitle = createSubtitle("Arrested.Development.S01E01.BDrip.XviD-Acme.srt", "", "acme");
        assertThat(calculator.calculate(subtitle)).isEqualTo(13);

        subtitle = createSubtitle("Arrested.Development.S01E01.DVDRip.DivX-Acme.srt", "", "acme");
        assertThat(calculator.calculate(subtitle)).isEqualTo(25);

        subtitle = createSubtitle("Arrested.Development.S01E01.BluRay.x264-OSCORP.srt", "", "oscorp");
        assertThat(calculator.calculate(subtitle)).isZero();

        /* test bugfix: #27 */
        weights = createWeights("720p hdtv x264", "DIMENSION");
        calculator = new ScoreCalculator(weights);

        subtitle = createSubtitle("Criminal Minds - 10x12 - Anonymous 720pDimension Vertaling: Het Criminal Minds Team", "720p",
                "720pDimension Vertaling: Het Criminal Minds Team");
        assertThat(calculator.calculate(subtitle)).isEqualTo(70);
    }

    private Subtitle createSubtitle(String filename, String quality, String team) {
        Subtitle subtitle = mock(Subtitle.class);
        when(subtitle.fileName).thenReturn(filename);
        when(subtitle.quality).thenReturn(quality);
        when(subtitle.releaseGroup).thenReturn(team);
        return subtitle;
    }

    private SortWeight createWeights(String quality, String group) {
        // Arrested.Development.S01E01.DVDRip.XviD-MEDiEVAL
        Release release = mock(TvRelease.class);
        when(release.quality).thenReturn(quality);
        when(release.releaseGroup).thenReturn(group);

        HashMap<String, Integer> definedWeights = new HashMap<>();
        definedWeights.put("hdtv", 2);
        definedWeights.put("dvdrip", 2);
        definedWeights.put("720p", 2);
        definedWeights.put("xvid", 1);
        definedWeights.put("x264", 1);
        definedWeights.put("%GROUP%", 5);

        return new SortWeight(release, definedWeights);
    }
}
