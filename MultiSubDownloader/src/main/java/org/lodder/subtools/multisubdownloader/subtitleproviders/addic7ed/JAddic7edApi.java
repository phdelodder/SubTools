package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.model.Addic7edSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAddic7edApi extends Html {

    private final Pattern pattern;
    private final static long RATEDURATION = MILLISECONDS.convert(15, TimeUnit.SECONDS);
    private boolean speedy;
    private Date lastRequest = new Date();
    private static final Logger LOGGER = LoggerFactory.getLogger(JAddic7edApi.class);

    public JAddic7edApi(boolean speedy, Manager manager) {
        super(manager, "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)");
        pattern = Pattern.compile("Version (.+), ([0-9]+).([0-9])+ MBs");
        this.speedy = speedy;
    }

    public JAddic7edApi(String username, String password, boolean speedy, Manager manager)
            throws ManagerException {
        super(manager, "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)");
        pattern = Pattern.compile("Version (.+), ([0-9]+).([0-9])+ MBs");
        login(username, password);
        this.speedy = speedy;
    }

    public void login(String username, String password) throws ManagerException {
        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        data.put("remember", "false");
        this.postHtml("http://www.addic7ed.com/dologin.php", data);
    }

    public String searchSerieName(String name) throws Exception {
        String content = searchName(name);

        Document doc = Jsoup.parse(content);
        Elements aTagWithSerie = doc.select("a[debug]");

        for (Element serieFound : aTagWithSerie) {
            String text =
                    serieFound.text().split(" - ")[0].replaceAll("[^A-Za-z]", "").trim().toLowerCase();
            name = name.replaceAll("[^A-Za-z]", "").trim().toLowerCase();
            if (text.contains(name)) {
                String link = serieFound.attr("href");
                String seriename = link.replace("serie/", "");
                return seriename.substring(0, seriename.indexOf("/"));
            }
        }
        return "";
    }

    public String searchMovieName(String name) throws Exception {
        String content = searchName(name);

        Document doc = Jsoup.parse(content);
        Elements aTagWithSerie = doc.select("a[debug]");

        String link = aTagWithSerie.get(0).attr("href");
        String moviename = link.replace("movie/", "");
        return moviename.substring(0, moviename.indexOf("/"));
    }

    private String searchName(String name) throws Exception {
        String url =
                "http://www.addic7ed.com/search.php?search=" + URLEncoder.encode(name, "UTF-8")
                        + "&Submit=Search";

        String content = this.getContent(true, url);

        if (content.contains("<b>0 results found</b>")) {
            if (name.contains(":")) {
                name = name.replace(":", "");
                return searchName(name);
            } else {
                throw new Exception("Can't find it on addic7ed, please contact developer!");
            }
        }

        return content;
    }

    public List<Addic7edSubtitleDescriptor> searchSubtitles(String showname, int season, int episode,
            String title) throws Exception {
        // http://www.addic7ed.com/serie/Smallville/9/11/Absolute_Justice
        String url =
                "http://www.addic7ed.com/serie/" + showname.toLowerCase().replace(" ", "_") + "/" + season
                        + "/" + episode + "/" + title.toLowerCase().replace(" ", "_").replace("#", "");
        String content = this.getContent(false, url);
        List<Addic7edSubtitleDescriptor> lSubtitles = new ArrayList<>();
        Document doc = Jsoup.parse(content);

        String titel = null;
        Elements elTitel = doc.getElementsByClass("titulo");
        if (elTitel.size() == 1) {
            titel = elTitel.get(0).html().substring(0, elTitel.get(0).html().indexOf("<") - 1).trim();
        }

        String uploader, version, lang, download = null;
        boolean hearingImpaired = false;
        Elements blocks = doc.getElementsByClass("tabel95");
        blocks = blocks.select("table[width=100%]");

        for (Element block : blocks) {
            uploader = "";
            version = null;
            lang = null;
            download = null;
            hearingImpaired = false;

            Elements classesNewsTitle = block.getElementsByClass("NewsTitle");
            Elements classesNewsDate = block.getElementsByClass("newsDate").select("td[colspan=3]");
            Elements imgHearingImpaired = block.select("img").select("img[title~=Hearing]");
            if (classesNewsTitle.size() == 1 && classesNewsDate.size() == 1) {
                TextNode tn = (TextNode) classesNewsTitle.get(0).childNode(1);
                Matcher m = pattern.matcher(tn.text());
                if (!m.find()) {
                    break;
                } else {
                    version =
                            m.group().substring(0, m.group().lastIndexOf(",")).replace("Version", "") + " "
                                    + classesNewsDate.get(0).text().trim();
                    uploader = block.getElementsByTag("a").select("a[href*=user/]").get(0).text();
                    hearingImpaired = imgHearingImpaired.size() > 0;
                }
            }

            if (version != null) {
                Elements tds = block.select("tr:contains(Completed)");
                Elements reqTds = tds.select("td").not("td[rowspan=2]");
                for (Element td : reqTds) {
                    if (td.hasClass("language")) {
                        lang = td.html().substring(0, td.html().indexOf("<"));
                    }

                    // incompleted not wanted
                    if ((lang != null && td.toString().toLowerCase().contains("completed")) && td.html().toLowerCase().contains("% completed")) {
                        lang = null;
                    }

                    if (lang != null && td.getElementsByClass("buttonDownload").size() > 0) {
                        Elements a = td.getElementsByClass("buttonDownload");
                        if (a.size() == 1) {
                            download = "http://www.addic7ed.com" + a.get(0).attr("href");
                        }
                        if (a.size() == 2) {
                            download = "http://www.addic7ed.com" + a.get(1).attr("href");
                        }
                    }
                    if (lang != null && download != null && titel != null) {
                        Addic7edSubtitleDescriptor sub = new Addic7edSubtitleDescriptor();
                        sub.setUploader(uploader);
                        sub.setTitel(titel.trim());
                        sub.setVersion(version.trim());
                        sub.setUrl(download);
                        sub.setLanguage(lang.trim());
                        sub.setHearingImpaired(hearingImpaired);
                        if (!isDuplicate(lSubtitles, sub)) {
                            lSubtitles.add(sub);
                        }
                        lang = null;
                        download = null;
                    }
                }
            }
        }

        return lSubtitles;

    }

    public boolean isDuplicate(List<Addic7edSubtitleDescriptor> lSubtitles,
            Addic7edSubtitleDescriptor sub) {
        for (Addic7edSubtitleDescriptor s : lSubtitles) {
            if (s.getLanguage().equals(sub.getLanguage()) && s.getUrl().equals(sub.getUrl())
                    && s.getVersion().equals(sub.getVersion())) {
                return true;
            }
        }
        return false;
    }

    private String getContent(boolean disk, String url) {
        String content = "";

        try {
            if (disk) {
                content = this.getHtmlDisk(url);
            } else {
                if (!speedy && !this.isCached(url)) {
                    long dur = new Date().getTime() - lastRequest.getTime();
                    if (dur < RATEDURATION) {
                        LOGGER.info("RateLimiet is bereikt voor ADDIC7ed, gelieve 15 sec te wachten");
                    }
                    while (new Date().getTime() - lastRequest.getTime() < RATEDURATION) {
                        try {
                            // Pause for 1 seconds
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // restore
                                                                // interrupted
                                                                // status
                        }
                    }
                    lastRequest = new Date();
                }
                content = this.getHtml(url);
            }
        } catch (HttpClientException | IOException | ManagerSetupException | ManagerException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return content;
    }
}
