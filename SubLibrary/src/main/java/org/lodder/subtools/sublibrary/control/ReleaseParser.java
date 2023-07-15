package org.lodder.subtools.sublibrary.control;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.FileUtils;
import org.lodder.subtools.sublibrary.util.NamedMatcher;
import org.lodder.subtools.sublibrary.util.NamedPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReleaseParser {

    private NamedMatcher namedMatcher;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseParser.class);

    public final Release parse(Path file) throws ReleaseParseException {
        String folderName = file.getParent() != null ? file.getParent().getFileName().toString() : "";

        for (String fileParseName : List.of(file.getFileName().toString(), folderName)) {
            for (NamedPattern np : VideoPatterns.COMPILED_PATTERNS) {
                namedMatcher = np.matcher(fileParseName);
                if (namedMatcher.find()) {
                    LOGGER.trace("Parsing match found using file name: {}", fileParseName);
                    return parsePatternResult(file, fileParseName);
                }
            }
        }
        throw new ReleaseParseException("Unknown format, can't be parsed: " + file.toAbsolutePath());
    }

    private Release parsePatternResult(Path file, String fileParseName) throws ReleaseParseException {
        List<String> namedGroups = namedMatcher.namedPattern().groupNames();
        String seriesName = "";
        List<Integer> episodeNumbers = new ArrayList<>();
        int seasonNumber = 0;
        Integer year = namedGroups.contains("year") ? Integer.parseInt(namedMatcher.group("year")): null;
        String description =namedGroups.contains("description") ?  namedMatcher.group("description").substring(1): "";

        if (namedGroups.contains("moviename")) {
            String movieName;
            if (namedGroups.contains("part")) {
                String number = "";
                if (namedGroups.contains("romanepisode")) {
                    number = namedMatcher.group("romanepisode");
                } else if (namedGroups.contains("partnumber")) {
                    number = namedMatcher.group("partnumber");
                }
                movieName = cleanUnwantedChars(namedMatcher.group("moviename") + " " + namedMatcher.group("part") + " " + number);
            } else {
                movieName = cleanUnwantedChars(namedMatcher.group("moviename"));
            }
            return MovieRelease.builder()
                    .name(movieName)
                    .file(file)
                    .year(year)
                    .description(description)
                    .releaseGroup(extractReleasegroup(file.getFileName().toString(), true))
                    .quality(getQualityKeyword(fileParseName))
                    .build();
        }

        if (namedGroups.contains("episodenumber1")) {
            LOGGER.trace("parsePatternResult: episodenumber1: {}", namedMatcher.group("episodenumber1"));
            // Multiple episodes, have episodenumber1, 2 ....
            for (String group : namedGroups) {
                Pattern pattern = Pattern.compile("episodenumber(\\d+)");
                Matcher match = pattern.matcher(group);
                if (match.matches()) {
                    episodeNumbers.add(Integer.parseInt(namedMatcher.group(group)));
                }
            }
            Collections.sort(episodeNumbers);
        } else if (namedGroups.contains("episodenumberstart")) {
            LOGGER.trace("parsePatternResult: episodenumberstart: {}", namedMatcher.group("episodenumberstart"));
            // Multiple episodes, regex specifies start and end number
            int start = Integer.parseInt(namedMatcher.group("episodenumberstart"));
            int end = Integer.parseInt(namedMatcher.group("episodenumberend"));
            if (start > end) {
                int temp = start;
                start = end;
                end = temp;
            }
            IntStream.rangeClosed(start, end).forEach(episodeNumbers::add);
        } else if (namedGroups.contains("episodenumber")) {
            LOGGER.trace("parsePatternResult: episodenumber: {}", namedMatcher.group("episodenumber"));
            episodeNumbers.add(Integer.parseInt(namedMatcher.group("episodenumber")));
        } else if (namedGroups.contains("year") || namedGroups.contains("month") || namedGroups.contains("day")) {
            // TODO: need to implement
        } else if (namedGroups.contains("romanepisode") && !namedGroups.contains("year")) {
            episodeNumbers.add(Roman.decode(namedMatcher.group("romanepisode")));
        }

        if (namedGroups.contains("seriesname")) {
            LOGGER.trace("parsePatternResult: seriesname: {}", namedMatcher.group("seriesname"));
            seriesName = cleanUnwantedChars(namedMatcher.group("seriesname"));
            if (namedGroups.contains("year")) {
                seriesName = seriesName + " " + namedMatcher.group("year");
            }
        }

        if (namedGroups.contains("seasonnumber")) {
            LOGGER.trace("parsePatternResult: seasonnumber: {}", namedMatcher.group("seasonnumber"));
            seasonNumber = Integer.parseInt(namedMatcher.group("seasonnumber"));
        } else if (namedGroups.contains("part") && !namedGroups.contains("year")) {
            seasonNumber = 1;
        } else if (namedGroups.contains("year") && namedGroups.contains("month") && namedGroups.contains("day")) {
            // need to implement
        } else if (namedGroups.contains("season_episode")) {
            LOGGER.trace("parsePatternResult: season_episode: {}", namedMatcher.group("season_episode"));
            if (namedMatcher.group("season_episode").length() == 3) {
                episodeNumbers.add(Integer.parseInt(namedMatcher.group("season_episode").substring(1, 3)));
                seasonNumber = Integer.parseInt(namedMatcher.group("season_episode").substring(0, 1));
            } else if (namedMatcher.group("season_episode").length() == 4) {
                episodeNumbers.add(Integer.parseInt(namedMatcher.group("season_episode").substring(2, 4)));
                seasonNumber = Integer.parseInt(namedMatcher.group("season_episode").substring(0, 2));
            }
        } else {
            // No season number specified, usually for Anime
            // TODO: need to implement
            throw new ReleaseParseException("Unable to parse the namedmatcher");
        }
        return TvRelease.builder()
                .name(seriesName)
                .season(seasonNumber)
                .episodes(episodeNumbers)
                .file(file)
                .description(FileUtils.withoutExtension(description))
                .releaseGroup(extractReleasegroup(file.getFileName().toString(), true))
                .special(isSpecialEpisode(seasonNumber, episodeNumbers))
                .quality(getQualityKeyword(fileParseName))
                .build();
    }

    private String cleanUnwantedChars(String text) {
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

        if (text.endsWith("-")) { // implemented if for "hawaii five-0"
            text = text.replace("-", ""); // remove space dash "altiplano-cd1"
        }

        // remove multiple spaces between text Back to the Future[][]Part II
        text = text.replaceAll(" +", " ");

        return text.trim();
    }

    public static String getQualityKeyword(String name) {
        LOGGER.trace("getQualityKeyword: name: {}", name);
        Matcher m = VideoPatterns.QUALITY_KEYWORDS_REGEX_PATTERN.matcher(name.trim().toLowerCase());
        StringBuilder builder = new StringBuilder();
        while (m.find()) {
            builder.append(m.group(0).replace(".", " ")).append(" ");
        }
        LOGGER.trace("getQualityKeyWords: keyswords: {}", builder.toString().trim());
        return builder.toString().trim();
    }

    public static List<String> getQualityKeyWords(String name) {
        LOGGER.trace("getQualityKeyWords: name: {}", name);
        Matcher m = VideoPatterns.QUALITY_KEYWORDS_REGEX_PATTERN.matcher(name.trim().toLowerCase());
        List<String> keywords = new ArrayList<>();
        while (m.find()) {
            keywords.add(m.group(0));
        }
        LOGGER.trace("getQualityKeyWords: keyswords: {}", keywords);
        return keywords;
    }

    public static String extractReleasegroup(final String fileName, boolean hasExtension) {
        LOGGER.trace("extractReleasegroup: name: {} , hasExtension: {}", fileName, hasExtension);
        Pattern releaseGroupPattern;
        if (hasExtension) {
            releaseGroupPattern = Pattern.compile("-([\\w]+).[\\w]+$");
        } else {
            releaseGroupPattern = Pattern.compile("-([\\w]+)$");
        }
        Matcher matcher = releaseGroupPattern.matcher(fileName);
        String releaseGroup = "";
        if (matcher.find()) {
            releaseGroup = matcher.group(1);
        }

        LOGGER.trace("extractReleasegroup: release group: {}", releaseGroup);
        return releaseGroup;
    }

    public static boolean isSpecialEpisode(final int season, final List<Integer> episodeNumbers) {
        if (season == 0) {
            return true;
        }
        return episodeNumbers.size() == 1 && episodeNumbers.get(0) == 0;
    }
}
