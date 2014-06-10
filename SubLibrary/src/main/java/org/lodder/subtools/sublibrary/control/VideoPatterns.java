package org.lodder.subtools.sublibrary.control;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.util.NamedPattern;

public class VideoPatterns {

  private static List<NamedPattern> plist = null;

  public static final String[] QUALITYKEYWORDS = new String[] {"hdtv", "web-dl", "web dl",
      "dvdrip", "bluray", "1080p", "ts", "dvdscreener", "r5", "bdrip", "brrip", "720p", "xvid",
      "cam", "480p", "webdl", "web", "dl", "x264", "1080i", "pdtv", "divx"};

  public static final String[] GROUPEDQUALITYKEYWORDS = new String[] {"hdtv 720p", "hdtv xvid",
      "hdtv x264"};

  public static final String[] CODECKEYWORDS = new String[] {"X264", "XVID", "divx"};

  public static final String[] EXTENSIONS = new String[] {"avi", "mkv", "wmv", "ts", "mp4"};

  // order is important!!!!!!
  public final static String[] PATTERNS =
      new String[] {
          // example:
          // Back.to.the.Future.Part.II.1989.720p.BluRay.X264-AMIABLE.mkv
          "(?<moviename>[\'\\w\\s:&()!.,_-]+)(?<part>Pt|Part|pt|part|Ep)(?<romanepisode>[I|V|X]+)[. ](?<year>19\\d{2}|20\\d{2})(?<description>[\'\\w\\s:&()!.,_-]+)",
          "(?<moviename>[\'\\w\\s:&()!.,_-]+)(?<part>Pt|Part|pt|part|Ep)[.](?<romanepisode>[I|V|X]+)[. ](?<year>19\\d{2}|20\\d{2})(?<description>[\'\\w\\s:&()!.,_-]+)",
          // serie
          "(?<seriesname>[\'\\w\\s:&()!.,_-]+)[Ss. _](?<seasonnumber>[\\d]{1,2})[XxEe]{1,2}(?<episodenumberstart>[\\d]{1,2})(?<episodebetween>[XxEe][\\d]{1,2})*[XxEe](?<episodenumberend>[\\d]{1,2})(?<description>[\'\\w\\s:&()!.,_-]+)",
          "(?<seriesname>[\'\\w\\s:&()!.,_-]+)[Ss. _](?<seasonnumber>[\\d]{1,2})[XxEe]{1,2}(?<episodenumber>[\\d]{1,2})(?<description>[\'\\w\\s:&()!.,_-]+)",
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

  public static List<NamedPattern> getCompiledPatterns() {
    if (plist == null) {
      plist = new ArrayList<NamedPattern>();
      for (String p : PATTERNS) {
        plist.add(NamedPattern.compile(p, Pattern.CASE_INSENSITIVE));
      }
    }
    return plist;
  }

  public static String buildQualityRegex() {
    Logger.instance.trace("VideoPatterns", "buildQualityRegex", "");
    StringBuilder result = new StringBuilder();
    String separator = "|";
    if (VideoPatterns.QUALITYKEYWORDS.length > 0) {
      result.append(VideoPatterns.QUALITYKEYWORDS[0]);
      for (int i = 1; i < VideoPatterns.QUALITYKEYWORDS.length; i++) {
        result.append(separator);
        result.append(VideoPatterns.QUALITYKEYWORDS[i]);
      }
    }
    return "(" + result.toString().replace(" ", "[. ]") + ")";
  }

}
