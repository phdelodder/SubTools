package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting;

import org.lodder.subtools.sublibrary.model.Subtitle;

public class ScoreCalculator {

  private SortWeight weights;

  public ScoreCalculator(SortWeight weights) {
    this.weights = weights;
  }

  public int calculate(Subtitle subtitle) {
    int score = 0;

    if (weights.getMaxScore() <= 0)
      return score;

    String subtitleInfo = subtitle.getFilename();
    subtitleInfo += " " + subtitle.getQuality();
    subtitleInfo += " " + subtitle.getTeam();

    subtitleInfo = subtitleInfo.trim().toLowerCase();

    for (String keyname : weights.getWeights().keySet()) {
      if (subtitleInfo.contains(keyname))
        score += weights.getWeights().get(keyname);
    }

    score = (int) Math.ceil(((float) score / weights.getMaxScore()) * 100);
    return score;
  }
}
