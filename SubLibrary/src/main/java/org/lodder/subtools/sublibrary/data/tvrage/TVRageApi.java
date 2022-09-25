package org.lodder.subtools.sublibrary.data.tvrage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisodeList;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageShowInfo;

public class TVRageApi {

    private static final String API_EPISODE_INFO = "episodeinfo.php";
    private static final String API_EPISODE_LIST = "episode_list.php";
    private static final String API_SEARCH = "search.php";
    private static final String API_SHOWINFO = "showinfo.php";
    private static final String API_SITE = "http://services.tvrage.com/feeds/";
    public static final String UNKNOWN = "UNKNOWN";
    private final TVRageParser tvrParser;

    public TVRageApi(Manager manager) {
        this.tvrParser = new TVRageParser(manager);
    }

    /**
     * Search for the show using the show name
     *
     * @param showName
     * @return list of matching shows
     * @throws TvrageException
     */
    public List<TVRageShowInfo> searchShow(String showName) throws TvrageException {
        if (!isValidString(showName)) {
            return List.of();
        }
        return tvrParser.getSearchShow(buildURL(API_SEARCH, showName).toString());
    }

    /**
     * Get the episode information for all episodes for a show
     *
     * @param showId
     * @return
     * @throws TvrageException
     */
    public Optional<TVRageEpisodeList> getEpisodeList(String showId) throws TvrageException {
        if (!isValidString(showId)) {
            return Optional.empty();
        }
        return Optional.of(tvrParser.getEpisodeList(buildURL(API_EPISODE_LIST, showId).toString()));
    }

    /**
     * Get the information for a specific episode
     *
     * @param showId
     * @param seasonId
     * @param episodeId
     * @return
     * @throws TvrageException
     */
    public Optional<TVRageEpisode> getEpisodeInfo(String showId, int seasonId, int episodeId) throws TvrageException {
        if (!isValidString(showId)) {
            return Optional.empty();
        }

        StringBuilder tvrageURL = buildURL(API_EPISODE_INFO, showId);
        // Append the Season & Episode to the URL
        tvrageURL.append("&ep=").append(seasonId);
        tvrageURL.append("x").append(episodeId);

        return tvrParser.getEpisodeInfo(tvrageURL.toString());
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
