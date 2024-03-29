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

        String subtitleInfo = "%s %s %s".formatted(subtitle.getFileName(), subtitle.getQuality(), subtitle.getReleaseGroup()).trim().toLowerCase();

        int score = weights.getWeights().keySet().stream().filter(subtitleInfo::contains).mapToInt(weights.getWeights()::get).sum();
        return (int) Math.ceil((float) score / weights.getMaxScore() * 100);
    }
}
