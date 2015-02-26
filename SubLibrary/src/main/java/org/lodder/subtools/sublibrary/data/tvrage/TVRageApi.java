package org.lodder.subtools.sublibrary.data.tvrage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisodeList;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageShowInfo;

public class TVRageApi{

	private static final String API_EPISODE_INFO = "episodeinfo.php";
    private static final String API_EPISODE_LIST = "episode_list.php";
    private static final String API_SEARCH = "search.php";
    private static final String API_SHOWINFO = "showinfo.php";
    private static final String API_SITE = "http://services.tvrage.com/feeds/";
    public static final String UNKNOWN = "UNKNOWN";
    private final TVRageParser tvrParser;
	
    public TVRageApi(Manager manager){
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
            return new ArrayList<TVRageShowInfo>();
        }

        String tvrageURL = buildURL(API_SEARCH, showName).toString();
        return tvrParser.getSearchShow(tvrageURL);
    }
    
    /**
     * Get the episode information for all episodes for a show
     *
     * @param showID
     * @return
     */
    public TVRageEpisodeList getEpisodeList(String showID) {
        if (!isValidString(showID)) {
            return new TVRageEpisodeList();
        }

        String tvrageURL = buildURL(API_EPISODE_LIST, showID).toString();
        return tvrParser.getEpisodeList(tvrageURL);
    }
    
    /**
     * Get the information for a specific episode
     *
     * @param showID
     * @param seasonId
     * @param episodeId
     * @return
     */
    public TVRageEpisode getEpisodeInfo(String showID, String seasonId, String episodeId) {
        if (!isValidString(showID) || !isValidString(seasonId) || !isValidString(episodeId)) {
            return new TVRageEpisode();
        }

        StringBuilder tvrageURL = buildURL(API_EPISODE_INFO, showID);
        // Append the Season & Episode to the URL
        tvrageURL.append("&ep=").append(seasonId);
        tvrageURL.append("x").append(episodeId);

        return tvrParser.getEpisodeInfo(tvrageURL.toString());
    }
	
	private StringBuilder buildURL(String urlParameter, String urlData) {
        // apiSite + search.php          + ?show=buffy
        // apiSite + showinfo.php        + ?sid=2930
        // apiSite + episode_list.php     + ?sid=2930

        StringBuilder tvrageURL = new StringBuilder();
        tvrageURL.append(API_SITE);
        tvrageURL.append(urlParameter);
        tvrageURL.append("?");
        
        String encUrlData = urlData;
        try {
          encUrlData = URLEncoder.encode(urlData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        if (urlParameter.equalsIgnoreCase(API_SEARCH)) {
            tvrageURL.append("show=").append(encUrlData);
        } else if (urlParameter.equalsIgnoreCase(API_SHOWINFO)) {
            tvrageURL.append("sid=").append(encUrlData);
        } else if (urlParameter.equalsIgnoreCase(API_EPISODE_LIST)) {
            tvrageURL.append("sid=").append(encUrlData);
        } else if (urlParameter.equalsIgnoreCase(API_EPISODE_INFO)) {
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
        return StringUtils.isNotBlank(testString) && (!testString.equalsIgnoreCase(TVRageApi.UNKNOWN));
    }
}
