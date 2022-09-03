package org.lodder.subtools.sublibrary.data.thetvdb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.data.XmlHTTP;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBEpisode;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TheTVDBApi {

    private final XmlHTTP xmlHTTPAPI;
    private final TheTVDBMirrors theTVDBMirrors;
    private String apiKey;

    private static final String XML_EXTENSION = ".xml";
    private static final String SERIES_URL = "/series/";
    private static final String ALL_URL = "/all/";

    private static final Logger LOGGER = LoggerFactory.getLogger(TheTVDBApi.class);

    public TheTVDBApi(String apikey, Manager manager) throws TheTVDBException {
        xmlHTTPAPI = new XmlHTTP(manager);
        try {
            theTVDBMirrors = new TheTVDBMirrors(apikey, manager);
        } catch (Exception e) {
            throw new TheTVDBException("Trouble getting a mirror");
        }
        setApiKey(apikey);
    }

    public int searchSerie(String seriename, String language) throws TheTVDBException {
        String url = getXmlMirror() + "/api/GetSeries.php?seriesname=";

        try {
            url = url + URLEncoder.encode(seriename, "UTF-8");

            Document doc = xmlHTTPAPI.getXMLDisk(url);
            NodeList nList = null;

            if (doc != null) {
                nList = doc.getElementsByTagName("Series");
            }

            if (nList == null || nList.getLength() == 0) {
                // retry if failed, but delete entry!
                xmlHTTPAPI.removeCacheEntry(url);
                doc = xmlHTTPAPI.getXMLDisk(url);
                nList = doc.getElementsByTagName("Series");
            }

            for (int i = 0; i < nList.getLength(); i++) {

                Element eElement = (Element) nList.item(i);
                if (eElement != null) {
                    String seriesName =
                            org.lodder.subtools.sublibrary.xml.XMLHelper
                                    .getStringTagValue("SeriesName", eElement);
                    if (seriesName.replaceAll("[^A-Za-z]", "").equalsIgnoreCase(
                            seriename.replaceAll("[^A-Za-z]", ""))) {
                        return org.lodder.subtools.sublibrary.xml.XMLHelper
                                .getIntTagValue("seriesid", eElement);
                    }
                    String aliasNames =
                            org.lodder.subtools.sublibrary.xml.XMLHelper
                                    .getStringTagValue("AliasNames", eElement);
                    if (aliasNames.replaceAll("[^A-Za-z]", "").equalsIgnoreCase(
                            seriename.replaceAll("[^A-Za-z]", ""))) {
                        return org.lodder.subtools.sublibrary.xml.XMLHelper
                                .getIntTagValue("seriesid", eElement);
                    }
                }
            }

        } catch (ManagerSetupException | ManagerException | ParserConfigurationException | SAXException | IOException e) {
            throw new TheTVDBException(e);
        }

        return 0;
    }

    public TheTVDBSerie getSerie(int tvdbid, String language) throws TheTVDBException {
        try {
            if (tvdbid != 0) {
                String url = createApiUrl("series", new String[] { Integer.toString(tvdbid) });
                // Use all as final resort if nothing get's found, or api returns bad results
                String urlAll = createApiUrl("series", new String[] { Integer.toString(tvdbid), ALL_URL });
                NodeList nList = tryGettingData(new String[] { url, url, urlAll });

                if (nList != null && nList.getLength() > 0
                        && nList.item(0).getNodeType() == Node.ELEMENT_NODE) {
                    return parseSerieNode((Element) nList.item(0));
                }
            } else {
                LOGGER.warn("TVDB ID is 0! please fix");
            }
        } catch (Exception e) {
            throw new TheTVDBException("Trouble getting a mirror");
        }

        return null;
    }

    private NodeList tryGettingData(String[] urls) throws TheTVDBException {
        NodeList nList = null;
        try {
            for (String url : urls) {
                Document doc = xmlHTTPAPI.getXMLDisk(url);

                if (doc != null) {
                    nList = doc.getElementsByTagName("Series");
                } else {
                    xmlHTTPAPI.removeCacheEntry(url);
                }

                if (nList == null || nList.getLength() == 0) {
                    xmlHTTPAPI.removeCacheEntry(url);
                } else {
                    return nList;
                }
            }
        } catch (ManagerSetupException | ManagerException | ParserConfigurationException | SAXException | IOException e) {
            throw new TheTVDBException(e);
        }

        return null;
    }

    public List<TheTVDBEpisode> getAllEpisodes(int tvdbid, String language) throws TheTVDBException {
        List<TheTVDBEpisode> epList = new ArrayList<>();
        String url = createApiUrl("series", new String[] { Integer.toString(tvdbid), ALL_URL });
        Document doc;

        try {
            doc = xmlHTTPAPI.getXMLDisk(url);
            NodeList nList = doc.getElementsByTagName("Episode");

            for (int i = 0; i < nList.getLength(); i++) {
                if (nList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nList.item(i);
                    TheTVDBEpisode ep = parseEpisodeNode(eElement);
                    epList.add(ep);
                }
            }
        } catch (ManagerSetupException | ManagerException | ParserConfigurationException | SAXException | IOException e) {
            throw new TheTVDBException(e);
        }

        return epList;
    }

    public TheTVDBEpisode getEpisode(int tvdbid, int season, int episode, String language)
            throws TheTVDBException {
        StringBuilder urlString = new StringBuilder();
        try {
            urlString.append(getXmlMirror());
            urlString.append("/api/");
            urlString.append(apiKey);
            urlString.append(SERIES_URL);
            urlString.append(tvdbid);
            urlString.append("/default/");
            urlString.append(season);
            urlString.append("/");
            urlString.append(episode);
            urlString.append("/");
            if (language != null) {
                urlString.append(language).append(XML_EXTENSION);
            }
            Document doc = xmlHTTPAPI.getXMLDisk(urlString.toString());

            NodeList nList = doc.getElementsByTagName("Episode");

            if (nList.getLength() > 0 && nList.item(0).getNodeType() == Node.ELEMENT_NODE) {
                return parseEpisodeNode((Element) nList.item(0));
            }

        } catch (ManagerSetupException | ManagerException | ParserConfigurationException | SAXException | IOException e) {
            throw new TheTVDBException(e);
        }

        return null;
    }

    private TheTVDBEpisode parseEpisodeNode(Element eElement) {
        TheTVDBEpisode episode = new TheTVDBEpisode();

        episode.setId(org.lodder.subtools.sublibrary.xml.XMLHelper.getStringTagValue("id", eElement));
        episode.setDvdChapter(XMLHelper.getStringTagValue("DVD_chapter", eElement));
        episode.setDvdDiscId(XMLHelper.getStringTagValue("DVD_discid", eElement));
        episode.setDvdEpisodeNumber(XMLHelper.getStringTagValue("DVD_episodenumber", eElement));
        episode.setDvdSeason(XMLHelper.getStringTagValue("DVD_season", eElement));
        episode.setDirectors(parseList(XMLHelper.getStringTagValue("Director", eElement), "|,"));
        episode.setEpImgFlag(XMLHelper.getStringTagValue("EpImgFlag", eElement));
        episode.setEpisodeName(XMLHelper.getStringTagValue("EpisodeName", eElement));
        episode.setEpisodeNumber(XMLHelper.getIntTagValue("EpisodeNumber", eElement));
        episode.setFirstAired(XMLHelper.getStringTagValue("FirstAired", eElement));
        episode.setGuestStars(parseList(XMLHelper.getStringTagValue("GuestStars", eElement), "|,"));
        episode.setImdbId(XMLHelper.getStringTagValue("IMDB_ID", eElement));
        episode.setLanguage(XMLHelper.getStringTagValue("Language", eElement));
        episode.setOverview(XMLHelper.getStringTagValue("Overview", eElement));
        episode.setProductionCode(XMLHelper.getStringTagValue("ProductionCode", eElement));
        episode.setRating(XMLHelper.getStringTagValue("Rating", eElement));
        episode.setSeasonNumber(XMLHelper.getIntTagValue("SeasonNumber", eElement));
        episode.setWriters(parseList(XMLHelper.getStringTagValue("Writer", eElement), "|,"));
        episode.setAbsoluteNumber(XMLHelper.getStringTagValue("absolute_number", eElement));
        // String s = XMLHelper.getStringTagValue(eElement, "filename");
        // if (!s.isEmpty()) {
        // episode.setFilename(TheTVDB.getBannerMirror() + s);
        // }
        episode.setLastUpdated(XMLHelper.getStringTagValue("lastupdated", eElement));
        episode.setSeasonId(XMLHelper.getStringTagValue("seasonid", eElement));
        episode.setSeriesId(XMLHelper.getStringTagValue("seriesid", eElement));

        try {
            episode.setAirsAfterSeason(XMLHelper.getIntTagValue("airsafter_season", eElement));
        } catch (Exception ignore) {
            episode.setAirsAfterSeason(0);
        }

        try {
            episode.setAirsBeforeEpisode(XMLHelper.getIntTagValue("airsbefore_episode", eElement));
        } catch (Exception ignore) {
            episode.setAirsBeforeEpisode(0);
        }

        try {
            episode.setAirsBeforeSeason(XMLHelper.getIntTagValue("airsbefore_season", eElement));
        } catch (Exception ignore) {
            episode.setAirsBeforeSeason(0);
        }

        return episode;
    }

    private TheTVDBSerie parseSerieNode(Element eElement) {
        TheTVDBSerie TheTVDBSerie = new TheTVDBSerie();

        try {
            LOGGER.trace("parseSerieNode: eElement [{}]", XMLHelper.getXMLAsString(eElement));
        } catch (Exception e) {
            LOGGER.error("Trying to display eElement", e);
        }

        TheTVDBSerie.setId(XMLHelper.getStringTagValue("id", eElement));
        TheTVDBSerie.setActors(parseList(XMLHelper.getStringTagValue("Actors", eElement), "|,"));
        TheTVDBSerie.setAirsDayOfWeek(XMLHelper.getStringTagValue("Airs_DayOfWeek", eElement));
        TheTVDBSerie.setAirsTime(XMLHelper.getStringTagValue("Airs_Time", eElement));
        TheTVDBSerie.setContentRating(XMLHelper.getStringTagValue("ContentRating", eElement));
        TheTVDBSerie.setFirstAired(XMLHelper.getStringTagValue("FirstAired", eElement));
        TheTVDBSerie.setGenres(parseList(XMLHelper.getStringTagValue("Genre", eElement), "|,"));
        TheTVDBSerie.setImdbId(XMLHelper.getStringTagValue("IMDB_ID", eElement));
        TheTVDBSerie.setLanguage(XMLHelper.getStringTagValue("Language", eElement));
        TheTVDBSerie.setNetwork(XMLHelper.getStringTagValue("Network", eElement));
        TheTVDBSerie.setOverview(XMLHelper.getStringTagValue("Overview", eElement));
        TheTVDBSerie.setRating(XMLHelper.getStringTagValue("Rating", eElement));
        TheTVDBSerie.setRuntime(XMLHelper.getStringTagValue("Runtime", eElement));
        TheTVDBSerie.setSerieId(XMLHelper.getStringTagValue("SeriesID", eElement));
        TheTVDBSerie.setSerieName(XMLHelper.getStringTagValue("SeriesName", eElement));
        TheTVDBSerie.setStatus(XMLHelper.getStringTagValue("Status", eElement));

        // String artwork = XMLHelper.getValueFromElement(eTheTVDBSerie, TYPE_BANNER);
        // if (!artwork.isEmpty()) {
        // TheTVDBSerie.setBanner(bannerMirror + artwork);
        // }
        //
        // artwork = XMLHelper.getValueFromElement(eTheTVDBSerie, TYPE_FANART);
        // if (!artwork.isEmpty()) {
        // TheTVDBSerie.setFanart(bannerMirror + artwork);
        // }
        //
        // artwork = XMLHelper.getValueFromElement(eTheTVDBSerie, TYPE_POSTER);
        // if (!artwork.isEmpty()) {
        // TheTVDBSerie.setPoster(bannerMirror + artwork);
        // }
        //
        TheTVDBSerie.setLastUpdated(XMLHelper.getStringTagValue("lastupdated", eElement));
        TheTVDBSerie.setZap2ItId(XMLHelper.getStringTagValue("zap2it_id", eElement));

        return TheTVDBSerie;
    }

    private static List<String> parseList(String input, String delim) {
        List<String> result = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(input, delim);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (token.length() > 0) {
                result.add(token);
            }
        }

        return result;
    }

    private String createApiUrl(String command, String[] params) {
        LOGGER.trace("createApiUrl, command [{}], params [{}]", command, params);
        command = command.replace("/", "");
        String urlParam = "";
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
                urlParam = params[i].replace("/", "");
            } else {
                urlParam = urlParam + "/" + params[i].replace("/", "");
            }
        }
        LOGGER.trace("createApiUrl, createpart /  command [{}] / urlParam [{}]", command, urlParam);
        return getXmlMirror() + "/api/" + this.getApiKey() + "/" + command + "/" + urlParam;
    }

    private String getXmlMirror() {
        return theTVDBMirrors.getMirror(TheTVDBMirrors.TYPE_XML);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
