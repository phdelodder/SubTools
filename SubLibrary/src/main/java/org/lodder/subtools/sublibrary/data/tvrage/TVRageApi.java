package org.lodder.subtools.sublibrary.data.tvrage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisodeList;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageShowInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TVRageApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(TVRageApi.class);
    private static final String API_EPISODE_INFO = "episodeinfo.php";
    private static final String API_EPISODE_LIST = "episode_list.php";
    private static final String API_SEARCH = "search.php";
    private static final String API_SHOWINFO = "showinfo.php";
    private static final String API_SITE = "http://services.tvrage.com/feeds/";
    public static final String UNKNOWN = "UNKNOWN";
    private final TVRageParser tvrParser;

    public TVRageApi(Manager manager) {
        tvrParser = new TVRageParser(manager);
    }

    /**
     * Search for the show using the show name
     *
     * @param showName
     * @return list of matching shows
     */
    public List<TVRageShowInfo> searchShow(String showName) {
        if (!isValidString(showName)) {
            return new ArrayList<>();
        }

        String tvrageURL = buildURL(API_SEARCH, showName).toString();
        try {
            return tvrParser.getSearchShow(tvrageURL);
        } catch (Exception e) {
            LOGGER.error("API TVRage: " + e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Get the episode information for all episodes for a show
     *
     * @param showId
     * @return
     */
    public Optional<TVRageEpisodeList> getEpisodeList(String showId) {
        if (!isValidString(showId)) {
            return Optional.empty();
        }

        String tvrageURL = buildURL(API_EPISODE_LIST, showId).toString();
        try {
            return Optional.of(tvrParser.getEpisodeList(tvrageURL));
        } catch (Exception e) {
            LOGGER.error("API TVRage: " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get the information for a specific episode
     *
     * @param showID
     * @param seasonId
     * @param episodeId
     * @return
     */
    public Optional<TVRageEpisode> getEpisodeInfo(String showID, String seasonId, String episodeId) {
        if (!isValidString(showID) || !isValidString(seasonId) || !isValidString(episodeId)) {
            return Optional.empty();
        }

        StringBuilder tvrageURL = buildURL(API_EPISODE_INFO, showID);
        // Append the Season & Episode to the URL
        tvrageURL.append("&ep=").append(seasonId);
        tvrageURL.append("x").append(episodeId);

        try {
            return tvrParser.getEpisodeInfo(tvrageURL.toString());
        } catch (Exception e) {
            LOGGER.error("API TVRage: " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    private StringBuilder buildURL(String urlParameter, String urlData) {
        // apiSite + search.php + ?show=buffy
        // apiSite + showinfo.php + ?sid=2930
        // apiSite + episode_list.php + ?sid=2930

        StringBuilder tvrageURL = new StringBuilder();
        tvrageURL.append(API_SITE);
        tvrageURL.append(urlParameter);
        tvrageURL.append("?");

        String encUrlData = URLEncoder.encode(urlData, StandardCharsets.UTF_8);

        if (API_SEARCH.equalsIgnoreCase(urlParameter)) {
            tvrageURL.append("show=").append(encUrlData);
        } else if (API_SHOWINFO.equalsIgnoreCase(urlParameter)) {
            tvrageURL.append("sid=").append(encUrlData);
        } else if (API_EPISODE_LIST.equalsIgnoreCase(urlParameter)) {
            tvrageURL.append("sid=").append(encUrlData);
        } else if (API_EPISODE_INFO.equalsIgnoreCase(urlParameter)) {
            tvrageURL.append("sid=").append(encUrlData);
            // Note this needs the season & episode appending to the url
        } else {
            return new StringBuilder(UNKNOWN);
        }

        return tvrageURL;
    }

    /**
     * Check the string passed to see if it contains a value.
     *
     * @param testString The string to test
     * @return False if the string is empty, null or UNKNOWN, True otherwise
     */
    public static boolean isValidString(String testString) {
        return StringUtils.isNotBlank(testString) && !TVRageApi.UNKNOWN.equalsIgnoreCase(testString);
    }
}
