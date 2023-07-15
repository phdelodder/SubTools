package org.lodder.subtools.sublibrary.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

public class SeasonEpisode {

    private static final Pattern SEASON_EPISODE_PATTERN_1 = Pattern.compile("S(\\d{1,2})E(\\d{1,2})");
    private static final Pattern SEASON_EPISODE_PATTERN_2 = Pattern.compile("[. ](\\d{1,2})x(\\d{1,2})");
    private static final Pattern SEASON_EPISODES_PATTERN_1 = Pattern.compile("S(\\d{1,2})E(\\d{1,2})E(\\d{1,2})");
    @Getter
    private final int season;
    @Getter
    private final List<Integer> episodes;

    public SeasonEpisode(int season, int episode) {
        this.season = season;
        this.episodes = List.of(episode);
    }

    public SeasonEpisode(int season, int... episodes) {
        this.season = season;
        this.episodes = Arrays.stream(episodes).boxed().toList();
    }

    public static Optional<SeasonEpisode> fromText(String text) {
        Matcher matcher = SEASON_EPISODES_PATTERN_1.matcher(text);
        if (matcher.find()) {
            return Optional.of(
                    new SeasonEpisode(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))));
        }
        Matcher matcher2 = SEASON_EPISODE_PATTERN_1.matcher(text);
        if (matcher2.find()) {
            return Optional.of(new SeasonEpisode(Integer.parseInt(matcher2.group(1)), Integer.parseInt(matcher2.group(2))));
        }
        Matcher matcher3 = SEASON_EPISODE_PATTERN_2.matcher(text);
        if (matcher3.find()) {
            return Optional.of(new SeasonEpisode(Integer.parseInt(matcher3.group(1)), Integer.parseInt(matcher3.group(2))));
        }
        return Optional.empty();
    }
}
