package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ScoreCalculatorTest {

  protected ScoreCalculator calculator;

  @Before
  public void setUp() throws Exception {
    calculator = null;
  }

  @Test
  public void test_it_calculates_the_score_for_subtitle() throws Exception {
    SortWeight weights = createWeights();

    calculator = new ScoreCalculator(weights);
    int score;
    Subtitle subtitle;

    subtitle =
      createSubtitle("Arrested.Development.S01E01.DVDRip.XviD-MEDiEVAL.srt", "dvdrip xvid", "medieval");
    score = calculator.calculate(subtitle);
    assertEquals(convertToPercentage(8, weights), score);

    subtitle =
      createSubtitle("Arrested Development 1x1 works with medieval.srt", "", "medieval");
    score = calculator.calculate(subtitle);
    assertEquals(convertToPercentage(5, weights), score);

    subtitle =
      createSubtitle("Arrested.Development.S01E01.BDrip.XviD-Acme.srt", "", "acme");
    score = calculator.calculate(subtitle);
    assertEquals(convertToPercentage(1, weights), score);

    subtitle =
      createSubtitle("Arrested.Development.S01E01.DVDRip.DivX-Acme.srt", "", "acme");
    score = calculator.calculate(subtitle);
    assertEquals(convertToPercentage(2, weights), score);

    subtitle =
      createSubtitle("Arrested.Development.S01E01.BluRay.x264-OSCORP.srt", "", "oscorp");
    score = calculator.calculate(subtitle);
    assertEquals(convertToPercentage(0, weights), score);
  }

  private int convertToPercentage(int score, SortWeight weights){
    return (score / weights.getMaxScore()) * 100;
  }

  private Subtitle createSubtitle(String filename, String quality, String team) {
    Subtitle subtitle = mock(Subtitle.class);
    when(subtitle.getFilename()).thenReturn(filename);
    when(subtitle.getQuality()).thenReturn(quality);
    when(subtitle.getTeam()).thenReturn(team);
    return subtitle;
  }

  private SortWeight createWeights() {
    // Arrested.Development.S01E01.DVDRip.XviD-MEDiEVAL
    Release release = mock(Release.class);
    when(release.getQuality()).thenReturn("DVDRip XviD");
    when(release.getReleasegroup()).thenReturn("MEDiEVAL");

    HashMap<String, Integer> definedWeights = new HashMap<>();
    definedWeights.put("dvdrip", 2);
    definedWeights.put("xvid", 1);
    definedWeights.put("%GROUP%", 5);

    return new SortWeight(release, definedWeights);
  }
}
