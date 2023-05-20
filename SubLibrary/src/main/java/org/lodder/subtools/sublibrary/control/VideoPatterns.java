package org.lodder.subtools.sublibrary.control;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lodder.subtools.sublibrary.util.NamedPattern;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;

@UtilityClass
@ExtensionMethod({ Arrays.class })
public class VideoPatterns {

    public interface VideoPatternEnumIntf {
    }

    @Getter
    @RequiredArgsConstructor
    public enum Quality implements VideoPatternEnumIntf {
        Q1080P("1080p"),
        Q1080I("1080i"),
        Q720P("720p"),
        Q480P("480p");

        final String value;

        public static Stream<String> getValuesStream() {
            return Quality.values().stream().map(Quality::getValue);
        }
    }

    @Getter
    public enum VideoEncoding implements VideoPatternEnumIntf {
        X264("x264", "h264"),
        X265("x265", "h265");

        final String[] values;

        VideoEncoding(String... values) {
            this.values = values;
        }

        public static Stream<String> getValuesStream() {
            return VideoEncoding.values().stream().map(VideoEncoding::getValues).flatMap(Arrays::stream);
        }
    }

    @Getter
    public enum AudioEncoding implements VideoPatternEnumIntf {
        DD5_1("dd5.1", "dd5-1");

        final String[] values;

        AudioEncoding(String... values) {
            this.values = values;
        }

        public static Stream<String> getValuesStream() {
            return AudioEncoding.values().stream().map(AudioEncoding::getValues).flatMap(Arrays::stream);
        }
    }

    @Getter
    public enum Source implements VideoPatternEnumIntf {
        HDTV(false, "hdtv"),
        DVDRIP(false, "dvdrip"),
        BLURAY(false, "bluray"),
        BDRIP(false, "bdrip"),
        BRRIP(false, "brrip"),
        XVID(false, "xvid"),
        PDTV(false, "pdtv"),
        DIVX(false, "divx"),
        WEBRIP(false, "webrip"),
        RERIP(false, "rerip"),
        WEBDL(false, "webdl", "web-dl", "web.dl"),
        TS(true, "ts"),
        DVD_SCREENER(true, "dvdscreener"),
        R5(true, "r5"),
        CAM(true, "cam");

        final boolean manyDifferentSources;
        final String[] values;

        Source(boolean manyDifferentSources, String... values) {
            this.manyDifferentSources = manyDifferentSources;
            this.values = values;
        }

        public static Stream<String> getValuesStream() {
            return Source.values().stream().map(Source::getValues).flatMap(Arrays::stream);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum VideoExtensions {
        MKV("mkv"),
        MP4("mp4"),
        AVI("avi"),
        WMV("wmv"),
        TS("ts"),
        M4V("m4v");

        final String value;
    }

    private static final Set<String> QUALITY_KEYWORDS_SET = Set.of("hdtv", "dvdrip", "bluray",
            "1080p", "ts", "dvdscreener", "r5", "bdrip", "brrip", "720p", "xvid", "cam", "480p", "x264", "x265",
            "1080i", "pdtv", "divx", "webrip", "h264", "h265", "rerip", "webdl");

    private static final Set<String> QUALITY_KEYWORDS_REGEX_SET = Set.of("web[ .-]dl", "dd5[ .]1");

    public static final Set<String> EXTENSIONS = Set.of("mkv", "mp4", "avi", "wmv", "ts", "m4v");

    // order is important!!!!!!
    private static final String[] PATTERNS = {
            // example:
            // Back.to.the.Future.Part.II.1989.720p.BluRay.X264-AMIABLE.mkv
            "(?<moviename>[\'\\w\\s:&()!.,_-]+)(?<part>Pt|Part|pt|part|Ep)(?<romanepisode>[I|V|X]+)[. ](?<year>19\\d{2}|20\\d{2})(?<description>[\'\\w\\s:&()!.,_-]+)",
            "(?<moviename>[\'\\w\\s:&()!.,_-]+)(?<part>Pt|Part|pt|part|Ep)[.](?<romanepisode>[I|V|X]+)[. ](?<year>19\\d{2}|20\\d{2})(?<description>[\'\\w\\s:&()!.,_-]+)",
            // The.Hunger.Games.Mockingjay.Part.1..2014.720p.BluRay.x264-SPARKS.mkv
            "(?<moviename>[\'\\w\\s:&()!.,_-]+)(?<part>Pt|Part|pt|part|Ep)(?<partnumber>[\\d]{1})[. ](?<year>19\\d{2}|20\\d{2})(?<description>[\'\\w\\s:&()!.,_-]+)",
            "(?<moviename>[\'\\w\\s:&()!.,_-]+)(?<part>Pt|Part|pt|part|Ep)[.](?<partnumber>[\\d]{1})[. ](?<year>19\\d{2}|20\\d{2})(?<description>[\'\\w\\s:&()!.,_-]+)",
            // serie
            "(?<seriesname>[\'\\w\\s:&()!.,_-]+)[Ss. _](?<seasonnumber>[\\d]{1,2})[XxEe]{1,2}(?<episodenumberstart>[\\d]{1,2})(?<episodebetween>[XxEe][\\d]{1,2})*[XxEe](?<episodenumberend>[\\d]{1,2})(?<description>[\'\\w\\s:&()!.,_-]+)",
            "(?<seriesname>[\'\\w\\s:&()!.,_-]+)[Ss. _](?<seasonnumber>[\\d]{1,2})[XxEe]{1,2}(?<episodenumber>[\\d]{1,3})(?<description>[\'\\w\\s:&()!.,_-]+)",
            // sXeX - Serienaam - Titel ex: S04E02 - White Collar - Most Wanted.mkv
            "[Ss. _](?<seasonnumber>[\\d]{1,2})[XxEe]{1,2}(?<episodenumberstart>[\\d]{1,2})(?<episodebetween>[XxEe][\\d]{1,2})*[XxEe](?<episodenumberend>[\\d]{1,2})\\s?+-?\\s?+(?<seriesname>[\'\\w\\s:&()!.,_]+)\\s?+-?\\s?+(?<description>[\'\\w\\s:&()!.,_]+)",
            "[Ss. _](?<seasonnumber>[\\d]{1,2})[XxEe]{1,2}(?<episodenumber>[\\d]{1,2})\\s?+-?\\s?+(?<seriesname>[\'\\w\\s:&()!.,_]+)\\s?+-?\\s?+(?<description>[\'\\w\\s:&()!.,_]+)",
            // example: hawaii.five-0.2010.410.hdtv-lol.mp4
            // example:
            // Spartacus.Gods.of.The.Arena.Pt.I.720p.HDTV.X264-DIMENSION.mkv
            "(?<seriesname>[\'\\w\\s:&()!.,_-]+)(?<part>Pt|Part|pt|part|Ep)(?<romanepisode>[I|V|X]+)(?<description>[\'\\w\\s:&()!.,_-]+)",
            "(?<seriesname>[\'\\w\\s:&()!.,_-]+)(?<part>Pt|Part|pt|part|Ep)[.](?<romanepisode>[I|V|X]+)(?<description>[\'\\w\\s:&()!.,_-]+)",
            "(?<seriesname>[\'\\w\\s:&()!.,_-]+)(?<part>Pt|Part|pt|part|Ep)(?<episodenumber>[\\d]{1,2})(?<description>[\'\\w\\s:&()!.,_-]+)",
            "(?<seriesname>[\'\\w\\s:&()!.,_-]+)(?<part>Pt|Part|pt|part|Ep)[.](?<episodenumber>[\\d]{1,2})(?<description>[\'\\w\\s:&()!.,_-]+)",
            // example hawaii.five-0.2010.410.hdtv-lol.mp4
            "(?<seriesname>[\'\\w\\s:&()!.,_-]+)[. ](?<year>19\\d{2}|20\\d{2})[. ](?<season_episode>[\\d]{3,4})[. ](?<description>[\'\\w\\s:&()!.,_-]+)",
            // format movietitle.year
            "(?<moviename>[\'\\w\\s:&()!.,_-]+)[\\.|\\[|\\(| ]{1}(?<year>19\\d{2}|20\\d{2})(?<description>[\'\\w\\s:&()!.,_-]+)",
            // format episode.0101.title
            // format episode.101.title
            // exclude format movietitle.720p
            "(?<seriesname>[\'\\w\\s:&()!.,_-]+)[. ](?<season_episode>[\\d]{3,4})[. ](?<description>[\'\\w\\s:&()!.,_-]+)",
            // format (2-11) Joey and the High School Friend
            "[(](?<seasonnumber>[\\d]{1,2})[-](?<episodenumber>[\\d]{1,2})[) ](?<seriesname>[\'\\w\\s:&()!.,_-]+)[ ]and(?<description>[\'\\w\\s:&()!.,_-]+)",
            "[(](?<seasonnumber>[\\d]{1,2})[-](?<episodenumber>[\\d]{1,2})[) ](?<seriesname>[\'\\w\\s:&()!.,_-]+)[ ]And(?<description>[\'\\w\\s:&()!.,_-]+)",
            // take the rest and treat as movie
            "(?<moviename>[\'\\w\\s:&()!.,_-]+)[\\.|\\[|\\(| ]{1}[720P|1080P](?<description>[\'\\w\\s:&()!.,_-]+)"

    };

    public static final List<NamedPattern> COMPILED_PATTERNS =
            Arrays.stream(PATTERNS).map(p -> NamedPattern.compile(p, Pattern.CASE_INSENSITIVE)).toList();

    public static final List<String> QUALITY_KEYWORDS = List.of();
    // Stream.concat(QUALITY_KEYWORDS_SET.stream(),
    // new Generex(QUALITY_KEYWORDS_REGEX_SET.stream().collect(Collectors.joining("|"))).getAllMatchedStrings().stream()).toList();

    private static final String QUALITY_KEYWORDS_REGEX =
            Stream.concat(QUALITY_KEYWORDS_SET.stream(), QUALITY_KEYWORDS_REGEX_SET.stream()).collect(Collectors.joining("|", "(", ")"));

    public static final Pattern QUALITY_KEYWORDS_REGEX_PATTERN = Pattern.compile(QUALITY_KEYWORDS_REGEX, Pattern.CASE_INSENSITIVE);

}
