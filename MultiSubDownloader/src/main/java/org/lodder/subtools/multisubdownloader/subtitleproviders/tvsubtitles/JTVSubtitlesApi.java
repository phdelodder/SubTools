package org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.exception.TvSubtiltesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model.TVsubtitlesSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;

public class JTVSubtitlesApi extends Html {

    private static final String DOMAIN = "https://www.tvsubtitles.net";

    public JTVSubtitlesApi(Manager manager) {
        super(manager);
    }

    public List<TVsubtitlesSubtitleDescriptor> searchSubtitles(String name, int season, int episode, Language language) throws TvSubtiltesException {
        List<TVsubtitlesSubtitleDescriptor> lSubtitles = new ArrayList<>();
        try {
            String showUrl = this.getShowUrl(name);

            if (showUrl != null) {

                String episodeUrl = getEpisodeUrl(showUrl, season, episode);

                if (episodeUrl != null) {
                    episodeUrl = DOMAIN + episodeUrl.substring(0, episodeUrl.indexOf(".")) + "-" + language.getLangCode() + ".html";
                    String searchEpisode = this.getHtml(episodeUrl);
                    Document searchEpisodeDoc = Jsoup.parse(searchEpisode);
                    Elements searchEpisodes = searchEpisodeDoc.getElementsByClass("left_articles").get(0).getElementsByTag("a");

                    for (Element ep : searchEpisodes) {
                        String url = ep.attr("href");
                        if (url.contains("subtitle-")) {
                            String subtitlePage = this.getHtml(DOMAIN + url);
                            Document subtitlePageDoc = Jsoup.parse(subtitlePage);
                            String filename = null, rip = null, title = null, author = null;
                            Elements subtitlePageTableDoc = subtitlePageDoc.getElementsByClass("subtitle1");
                            if (subtitlePageTableDoc.size() == 1) {
                                for (Element item : subtitlePageTableDoc.get(0).getElementsByTag("tr")) {
                                    Elements row = item.getElementsByTag("td");
                                    if (row.size() == 3 && row.get(1).text().contains("episode title:")) {
                                        title = row.get(2).text();
                                    }
                                    if (row.size() == 3 && row.get(1).text().contains("filename:")) {
                                        filename = row.get(2).text();
                                    }
                                    if (row.size() == 3 && row.get(1).text().contains("rip:")) {
                                        rip = row.get(2).text();
                                    }
                                    if (row.size() == 3 && row.get(1).text().contains("author:")) {
                                        author = row.get(2).text();
                                    }

                                    if (filename != null && rip != null) {
                                        TVsubtitlesSubtitleDescriptor sub = new TVsubtitlesSubtitleDescriptor();
                                        sub.Filename = filename;
                                        sub.Url = DOMAIN + "/files/" + URLEncoder.encode(
                                                filename.replace(title + ".", "").replace(".srt", ".zip").replace(" - ", "_"),
                                                StandardCharsets.UTF_8);
                                        sub.Rip = rip;
                                        sub.Author = author;
                                        lSubtitles.add(sub);
                                        rip = null;
                                        filename = null;
                                        title = null;
                                        author = null;
                                    }
                                }
                            }
                        }

                    }
                }
            }
            return lSubtitles;
        } catch (ManagerException | IOException | HttpClientException | ManagerSetupException e) {
            throw new TvSubtiltesException(e);
        }
    }

    private String getEpisodeUrl(String showUrl, int season, int episode) throws TvSubtiltesException {
        try {
            String seasonUrl = DOMAIN + showUrl.substring(0, showUrl.indexOf(".")) + "-" + season + ".html";
            String searchSeason = this.getHtmlDisk(seasonUrl);
            Document searchSeasonDoc = Jsoup.parse(searchSeason);
            if (searchSeasonDoc == null) {
                return null;
            }

            Element searchSeasonTable = searchSeasonDoc.getElementById("table5");
            String episodeUrl = null;

            boolean foundEp = false;
            for (Element ep : searchSeasonTable.getElementsByTag("td")) {
                if (foundEp) {
                    Elements links = ep.getElementsByTag("a");
                    if (links.size() == 1) {
                        episodeUrl = "/" + links.get(0).attr("href");
                        break;
                    }
                }

                String formatedepisodenumber = "";
                if (episode < 10) {
                    formatedepisodenumber = "0" + episode;
                } else {
                    formatedepisodenumber = "" + episode;
                }
                if ((season + "x" + formatedepisodenumber).equals(ep.text())) {
                    foundEp = true;
                }
            }

            return episodeUrl;
        } catch (IOException | HttpClientException | ManagerSetupException | ManagerException e) {
            throw new TvSubtiltesException(e);
        }
    }

    private String getShowUrl(String showName) throws TvSubtiltesException {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("qs", showName);

            String searchShow = this.postHtml(DOMAIN + "/search.php", data);

            Document searchShowDoc = Jsoup.parse(searchShow);
            if (searchShowDoc == null) {
                return null;
            }

            return searchShowDoc.getElementsByTag("li").stream()
                    .map(show -> show.getElementsByTag("a"))
                    .filter(links -> links.size() == 1 && links.get(0).text().toLowerCase().contains(showName.toLowerCase()))
                    .map(links -> links.get(0).attr("href"))
                    .findFirst().orElse(null);
        } catch (ManagerException e) {
            throw new RuntimeException(e);
        }
    }
}
