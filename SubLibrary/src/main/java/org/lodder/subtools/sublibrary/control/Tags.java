package org.lodder.subtools.sublibrary.control;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.sublibrary.control.VideoPatterns.AudioEncoding;
import org.lodder.subtools.sublibrary.control.VideoPatterns.Quality;
import org.lodder.subtools.sublibrary.control.VideoPatterns.Source;
import org.lodder.subtools.sublibrary.control.VideoPatterns.VideoEncoding;

@UtilityClass
public class Tags {

    @AllArgsConstructor
    public static class Tag<T> {
        @val String value;
        @val Function<String, T> mapper;
    }

    public static Tag<String> NAME = new Tag<>("name", Function.identity());
    public static Tag<String> TITLE = new Tag<>("title", Function.identity());
    public static Tag<Integer> SEASON = new Tag<>("season", Integer::parseInt);
    public static Tag<List<Integer>> EPISODE = new Tag<>("episode", v -> List.of(Integer.parseInt(v)));
    public static Tag<List<Integer>> EPISODES_TEXT =
        new Tag<>("episodestext", v -> {
            List<Integer> episodes = new ArrayList<>();
            Matcher matcher = Pattern.compile("\\d+").matcher(v);
            while (matcher.find()) {
                episodes.add(Integer.parseInt(matcher.group()));
            }
            return episodes;
        });
    public static Tag<Integer> ARABIC_NUMBER = new Tag<>("arabicnbr", Integer::parseInt);
    public static Tag<Integer> ROMAN_NUMBER = new Tag<>("romannbr", Roman::decode);
    public static Tag<Integer> YEAR = new Tag<>("year", Integer::parseInt);
    public static Tag<AudioEncoding> AUDIO_ENCODING = new Tag<>("audioenc", AudioEncoding::fromValue);
    public static Tag<VideoEncoding> VIDEO_ENCODING = new Tag<>("videoenc", VideoEncoding::fromValue);
    public static Tag<Quality> QUALITY = new Tag<>("quality", Quality::fromValue);
    public static Tag<Source> SOURCE = new Tag<>("source", Source::fromValue);
}
