package org.lodder.subtools.sublibrary.control;


import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;

@UtilityClass
public class VideoPatterns {

    public interface RegexPattern {
        @val Pattern pattern;
        @val String value;

        default String getRegex() {
            return pattern.pattern();
        }
    }

    public enum Quality implements RegexPattern {
        Q8K("8k", "8k"),
        Q4K("4k", "4k"),
        Q1440P("1440p", "1440p"),
        Q1080P("1080p", "1080p"),
        Q1080I("1080i", "1080i"),
        Q720P("720p", "720p"),
        Q480P("480p", "480p");

        @val @override String value;
        @val @override Pattern pattern;

        Quality(String value, String quality) {
            this.value = value;
            this.pattern = Pattern.compile(quality, Pattern.CASE_INSENSITIVE);
        }

        public static Quality fromValue(String value) {
            return value == null ? null : Quality.values().stream()
                .filter(v -> v.pattern.matcher(value).find())
                .findAny()
                .orElse(null);
        }
    }

    @AllArgsConstructor
    public enum VideoEncoding implements RegexPattern {
        X264("x264", "[xh]264"),
        X265("x265", "[xh]265|hevc");

        @val @override Pattern pattern;
        @val @override String value;

        VideoEncoding(String value, String quality) {
            this.value = value;
            pattern = Pattern.compile(quality, Pattern.CASE_INSENSITIVE);
        }

        public static VideoEncoding fromValue(String value) {
            return value == null ? null : VideoEncoding.values().stream()
                .filter(v -> v.pattern.matcher(value).find())
                .findAny()
                .orElse(null);
        }
    }

    @AllArgsConstructor
    public enum AudioEncoding implements RegexPattern {
        DD5_1("dd5.1", "dd5[-.]1");

        @val @override Pattern pattern;
        @val @override String value;

        AudioEncoding(String value, String quality) {
            this.value = value;
            pattern = Pattern.compile(quality, Pattern.CASE_INSENSITIVE);
        }

        public static AudioEncoding fromValue(String value) {
            return value == null ? null : AudioEncoding.values().stream()
                .filter(v -> v.pattern.matcher(value).find())
                .findAny()
                .orElse(null);
        }
    }

    @AllArgsConstructor
    public enum Source implements RegexPattern {
        HDTV("hdtv", "hdtv", true, false),
        DVDRIP("dvdrip", "dvdrip", false, false),
        BLURAY("bluray", "bluray", false, false),
        BDRIP("bdrip", "bdrip", false, false),
        BRRIP("brrip", "brrip", false, false),
        XVID("xvid", "xvid", false, false),
        PDTV("pdtv", "pdtv", true, false),
        DIVX("divx", "divx", false, false),
        WEBRIP("webrip", "webrip", false, false),
        RERIP("rerip", "rerip", false, false),
        WEBDL("web-dl", "web[-.]?dl", false, false),
        WEB("web", "web", false, false),
        TS("ts", "ts", false, true),
        DVD_SCREENER("dvdscreener", "dvdscreener", false, true),
        R5("r5", "r5", false, true),
        CAM("cam", "cam", false, true);

        @val @override Pattern pattern;
        @val @override String value;
        @val boolean likelyTvRelease;
        @val boolean likelyMovieRelease;

        Source(String value, String quality, boolean likelyTvRelease, boolean likelyMovieRelease) {
            this.value = value;
            this.pattern = Pattern.compile(quality, Pattern.CASE_INSENSITIVE);
            this.likelyTvRelease = likelyTvRelease;
            this.likelyMovieRelease = likelyMovieRelease;
        }

        public static Source fromValue(String value) {
            return value == null ? null : Source.values().stream()
                .filter(v -> v.pattern.matcher(value).find())
                .findAny()
                .orElse(null);
        }

        public boolean isTypeForValue(String value) {
            return fromValue(value) != null;
        }

        @Override
        public String toString() {
            return regex;
        }
    }

    @AllArgsConstructor
    public enum VideoExtensions {
        MKV("mkv"),
        MP4("mp4"),
        AVI("avi"),
        WMV("wmv"),
        TS("ts"),
        M4V("m4v");

        @val String value;
    }

    public static final Set<String> EXTENSIONS =
        VideoExtensions.values().stream().map(VideoExtensions::getValue).collect(Collectors.toSet());
}
