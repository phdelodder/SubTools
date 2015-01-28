package org.lodder.subtools.sublibrary.control;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lodder.subtools.sublibrary.exception.VideoFileParseException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.NamedMatcher;
import org.lodder.subtools.sublibrary.util.NamedPattern;

public class ReleaseParser {

  private NamedMatcher namedMatcher;

  public final NamedMatcher getNamedMatcher() {
    return namedMatcher;
  }

  @SuppressWarnings("unchecked")
  public final Release parse(final File file, final File basedir) throws VideoFileParseException {
    String[] parsenames =
        new String[] {file.getName(), file.getAbsolutePath().replace(basedir.getAbsolutePath(), "")};
    for (String fileparsename : parsenames) {
      for (NamedPattern np : VideoPatterns.getCompiledPatterns()) {
        namedMatcher = np.matcher(fileparsename);
        if (namedMatcher.find()) {
          Logger.instance.trace(this.getClass().toString(), "parse", "using file name: "
              + fileparsename);
          Object[] parseResults = parsePatternResult();
          if (parseResults != null) {
            Release vFile = null;
            if (parseResults.length == 4) {
              vFile =
                  new TvRelease((String) parseResults[0], (Integer) parseResults[1],
                      (List<Integer>) parseResults[2], file,
                      extractFileNameExtension(file.getName()),
                      removeExtension((String) parseResults[3]),
                      extractReleasegroup(file.getName()), isSpecialEpisode(
                          (Integer) parseResults[1], (List<Integer>) parseResults[2]));
            } else if (parseResults.length == 3) {
              vFile =
                  new MovieRelease((String) parseResults[0], (Integer) parseResults[1], file,
                      extractFileNameExtension(file.getName()),
                      removeExtension((String) parseResults[2]),
                      extractReleasegroup(file.getName()));
            }
            vFile.setQuality(getQualityKeyword(fileparsename));
            return vFile;
          }
        }
      }
    }
    throw new VideoFileParseException("Unknow format, can't be parsed: " + file.getAbsolutePath());
  }

  protected final Object[] parsePatternResult() throws VideoFileParseException {
    List<String> namedgroups = namedMatcher.namedPattern().groupNames();
    String seriesname = "";
    List<Integer> episodenumbers = new ArrayList<Integer>();
    int seasonnumber = 0;
    int year = 0;
    String description = "";

    if (namedgroups.contains("description")) {
      description = namedMatcher.group("description").substring(1);
    }


    if (namedgroups.contains("year")) {
      year = Integer.parseInt(namedMatcher.group("year"));
    }

    if (namedgroups.contains("moviename")) {
      if (namedgroups.contains("part")) {
        return new Object[] {
            cleanUnwantedChars(namedMatcher.group("moviename") + " " + namedMatcher.group("part")
                + " " + namedMatcher.group("romanepisode")), year, description};
      } else {
        return new Object[] {cleanUnwantedChars(namedMatcher.group("moviename")), year, description};
      }
    }

    if (namedgroups.contains("episodenumber1")) {
      Logger.instance.trace(this.getClass().toString(), "parsePatternResult", "episodenumber1 '"
          + namedMatcher.group("episodenumber1") + "'");
      // Multiple episodes, have episodenumber1, 2 ....
      for (String group : namedgroups) {
        Pattern pattern = Pattern.compile("episodenumber(\\d+)");
        Matcher match = pattern.matcher(group);
        if (match.matches()) {
          episodenumbers.add(Integer.parseInt(namedMatcher.group(group)));
        }
      }
      Collections.sort(episodenumbers);
    } else if (namedgroups.contains("episodenumberstart")) {
      Logger.instance.trace(this.getClass().toString(), "parsePatternResult",
          "episodenumberstart '" + namedMatcher.group("episodenumberstart") + "'");
      // Multiple episodes, regex specifies start and end number
      int start = Integer.parseInt(namedMatcher.group("episodenumberstart"));
      int end = Integer.parseInt(namedMatcher.group("episodenumberend"));
      if (start > end) {
        int temp = start;
        start = end;
        end = temp;
      }
      for (int i = start; i <= end; i++) {
        episodenumbers.add(i);
      }
    } else if (namedgroups.contains("episodenumber")) {
      Logger.instance.trace(this.getClass().toString(), "parsePatternResult", "episodenumber '"
          + namedMatcher.group("episodenumber") + "'");
      episodenumbers.add(Integer.parseInt(namedMatcher.group("episodenumber")));
    } else if (namedgroups.contains("year") || namedgroups.contains("month")
        || namedgroups.contains("day")) {
      // need to implement
    } else if (namedgroups.contains("romanepisode") && !namedgroups.contains("year")) {
      episodenumbers.add(romanToInteger(namedMatcher.group("romanepisode")));
    }

    if (namedgroups.contains("seriesname")) {
      Logger.instance.trace(this.getClass().toString(), "parsePatternResult", "seriesname '"
          + namedMatcher.group("seriesname") + "'");
      seriesname = cleanUnwantedChars(namedMatcher.group("seriesname"));
      if (namedgroups.contains("year")) {
        seriesname = seriesname + " " + namedMatcher.group("year");
      }
    }

    if (namedgroups.contains("seasonnumber")) {
      Logger.instance.trace(this.getClass().toString(), "parsePatternResult", "seasonnumber '"
          + namedMatcher.group("seasonnumber") + "'");
      seasonnumber = Integer.parseInt(namedMatcher.group("seasonnumber"));
      return new Object[] {seriesname, seasonnumber, episodenumbers, description};
    } else if (namedgroups.contains("part") && !namedgroups.contains("year")) {
      seasonnumber = 1;
      return new Object[] {seriesname, seasonnumber, episodenumbers, description};
    } else if (namedgroups.contains("year") && namedgroups.contains("month")
        && namedgroups.contains("day")) {
      // need to implement
    } else if (namedgroups.contains("season_episode")) {
      Logger.instance.trace(this.getClass().toString(), "parsePatternResult", "season_episode '"
          + namedMatcher.group("season_episode") + "'");
      if (namedMatcher.group("season_episode").length() == 3) {
        episodenumbers.add(Integer.parseInt(namedMatcher.group("season_episode").substring(1, 3)));
        seasonnumber = Integer.parseInt(namedMatcher.group("season_episode").substring(0, 1));
      } else if (namedMatcher.group("season_episode").length() == 4) {
        episodenumbers.add(Integer.parseInt(namedMatcher.group("season_episode").substring(2, 4)));
        seasonnumber = Integer.parseInt(namedMatcher.group("season_episode").substring(0, 2));
      }
      return new Object[] {seriesname, seasonnumber, episodenumbers, description};
    } else {
      // No season number specified, usually for Anime
      // need to implement
    }
    throw new VideoFileParseException("Unable to parse the namedmatcher");
  }

  protected final String cleanUnwantedChars(String text) {
    if (text.contains("cd1")) {
      text = text.replace("cd1", " ");
    }
    if (text.contains("cd2")) {
      text = text.replace("cd2", " ");
    }

    text = text.replace(".", " "); // remove point bones.01x01
    text = text.replace("_", " "); // remove underscore bones_01x01
    text = text.replace(" -", " "); // remove space dash "ncis - 01x01"
    text = text.replace(":", ""); // remove double point "CSI: NY"
    text = text.replace("(", ""); // remove ( for castle (2009)
    text = text.replace(")", ""); // remove ) for castle (2009)
    text = text.replace("'", "");

    if (text.endsWith("-")) // implemented if for "hawaii five-0"
    {
      text = text.replace("-", ""); // remove space dash "altiplano-cd1"
    }

    // remove multiple spaces between text Back to the Future[][]Part II
    text = text.replaceAll(" +", " ");

    return text.trim();
  }

  public final int romanToInteger(final String roman) {
    int decimal = 0;
    String romanNumeral = roman.toUpperCase();
    int x = 0;
    do {
      char convertToDecimal = roman.charAt(x);
      switch (convertToDecimal) {
        case 'M':
          decimal += 1000;
          break;

        case 'D':
          decimal += 500;
          break;

        case 'C':
          decimal += 100;
          break;

        case 'L':
          decimal += 50;
          break;

        case 'X':
          decimal += 10;
          break;

        case 'V':
          decimal += 5;
          break;

        case 'I':
          decimal += 1;
          break;
        default:
          break;
      }
      x++;
    } while (x < romanNumeral.length());
    return decimal;
  }

  public static final String getQualityKeyword(final String name) {
    Logger.instance.trace("VideoFileParser", "getQualityKeyword", name);
    Pattern p = Pattern.compile(VideoPatterns.buildQualityRegex(), Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(name);
    StringBuilder builder = new StringBuilder();
    while (m.find()) {
      builder.append(m.group(0).replace(".", " ")).append(" ");
    }
    return builder.toString().trim();
  }

  public static List<String> getQualityKeyWords(String name) {
    Logger.instance.trace("VideoFileParser", "getQualityKeywords", name);
    name = name.trim().toLowerCase();
    List<String> keywords = new ArrayList<>();
    for (String keyword : VideoPatterns.QUALITYKEYWORDS) {
      if (name.contains(keyword)) keywords.add(keyword);
    }
    return keywords;
  }

  public static String extractFileNameExtension(final String fileName) {
    int mid = fileName.lastIndexOf(".");
    return fileName.substring(mid + 1, fileName.length());
  }

  public static String extractReleasegroup(final String fileName) {
    Pattern releaseGroupPattern = Pattern.compile("-([\\w]+).[\\w]+$");
    Matcher matcher = releaseGroupPattern.matcher(fileName);
    if (!matcher.find()) return "";

    return matcher.group(1);
  }

  public static String removeExtension(final String fileName) {
    final int index = fileName.lastIndexOf('.');

    if (-1 == index) {
      return fileName;
    } else {
      return fileName.substring(0, index);
    }
  }

  public static boolean isSpecialEpisode(final int season, final List<Integer> episodeNumbers) {
    if (season == 0) return true;
    return episodeNumbers.size() == 1 & episodeNumbers.get(0) == 0;
  }
}
