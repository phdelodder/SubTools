package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting;

import org.lodder.subtools.sublibrary.model.Subtitle;

public class ScoreCalculator {

    private final SortWeight weights;

    public ScoreCalculator(SortWeight weights) {
        this.weights = weights;
    }

    public int calculate(Subtitle subtitle) {
        if (weights.getMaxScore() <= 0) {
            return 0;
        }

        String subtitleInfo = subtitle.getFilename();
        subtitleInfo += " " + subtitle.getQuality();
        subtitleInfo += " " + subtitle.getReleasegroup();

        subtitleInfo = subtitleInfo.trim().toLowerCase();

        int score = weights.getWeights().keySet().stream().filter(subtitleInfo::contains).mapToInt(weights.getWeights()::get).sum();
        return (int) Math.ceil((float) score / weights.getMaxScore() * 100);
    }
}
