package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.exception.Addic7edException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.model.Addic7edSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAddic7edApi extends Html {

    private static final Logger LOGGER = LoggerFactory.getLogger(JAddic7edApi.class);
    private final Pattern pattern = Pattern.compile("Version (.+), Duration: ([0-9]+).([0-9])+ ");
    private final static long RATEDURATION = 15; // seconds
    private final boolean speedy;
    private LocalDateTime lastRequest = LocalDateTime.now();
    private final Map<String, String> serieNameCache = new HashMap<>();

    public JAddic7edApi(boolean speedy, Manager manager) {
        super(manager, "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)");
        this.speedy = speedy;
    }

    public JAddic7edApi(String username, String password, boolean speedy, Manager manager) throws Addic7edException {
        super(manager, "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)");
        this.speedy = speedy;
        login(username, password);
    }

    public void login(String username, String password) throws Addic7edException {
        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        data.put("remember", "false");
        try {
            this.postHtml("http://www.addic7ed.com/dologin.php", data);
        } catch (ManagerException e) {
            throw new Addic7edException(e);
        }
    }

    public Optional<String> getAddictedSerieName(String name) {
        String formattedName = name.replace(":", "").replace("-", "").trim().toLowerCase();
        if (serieNameCache.containsKey(formattedName)) {
            return Optional.of(serieNameCache.get(formattedName));
        }
        return resultStringForName(name).map(content -> {
            Document doc = Jsoup.parse(content);
            Elements aTagWithSerie = doc.select("#season td > a");

            Optional<String> serieName = aTagWithSerie.stream()
                    .map(serieFound -> {
                        String link = serieFound.attr("href");
                        String seriename = link.replace("/serie/", "");
                        return seriename.substring(0, seriename.indexOf("/"));
                    })
                    .filter(seriename -> seriename.replace(":", "").replace("-", "").replace("_", " ").trim().toLowerCase()
                            .equalsIgnoreCase(formattedName))
                    .findAny();
            serieName.ifPresent(sn -> serieNameCache.put(name, sn));
            return serieName.orElse(null);
        });

    }

    public Optional<String> searchMovieName(String name) {
        return resultStringForName(name).map(content -> {
            Document doc = Jsoup.parse(content);
            Elements aTagWithSerie = doc.select("a[debug]");

            String link = aTagWithSerie.get(0).attr("href");
            String moviename = link.replace("movie/", "");
            return moviename.substring(0, moviename.indexOf("/"));
        });
    }

    private Optional<String> resultStringForName(String name) {
        String url = "https://www.addic7ed.com/search.php?search=" + URLEncoder.encode(name, StandardCharsets.UTF_8) + "&Submit=Search";

        String content = this.getContent(true, url);

        if (content.contains("<b>0 results found</b>")) {
            if (name.contains(":")) {
                return resultStringForName(name.replace(":", ""));
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(content);
    }

    public List<Addic7edSubtitleDescriptor> searchSubtitles(String showname, int season, int episode, String title) {
        // http://www.addic7ed.com/serie/Smallville/9/11/Absolute_Justice
        // String url = "https://www.addic7ed.com/serie/" + showname.toLowerCase().replace(" ", "_") + "/" + season
        // + "/" + episode + "/" + title.toLowerCase().replace(" ", "_").replace("#", "");

        // title isn't necessary to be added. In fact, if an invalid title is used (ie. invalid chars, like '/'),
        // a 302 error can be returned. It's better to add a valid dummy title.
        String url = "https://www.addic7ed.com/serie/" + showname.toLowerCase().replace(" ", "_") + "/" + season
                + "/" + episode + "/a";
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
                    version = m.group().substring(0, m.group().lastIndexOf(",")).replace("Version", "") + " " + classesNewsDate.get(0).text().trim();
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

                    // incomplete not wanted
                    if ((lang != null && td.toString().toLowerCase().contains("completed")) && td.html().toLowerCase().contains("% completed")) {
                        lang = null;
                    }

                    Elements downloadElements = td.getElementsByClass("buttonDownload");
                    if (lang != null && downloadElements.size() > 0) {
                        if (downloadElements.size() == 1) {
                            download = "http://www.addic7ed.com" + downloadElements.get(0).attr("href");
                        }
                        if (downloadElements.size() == 2) {
                            download = "http://www.addic7ed.com" + downloadElements.get(1).attr("href");
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

    public boolean isDuplicate(List<Addic7edSubtitleDescriptor> lSubtitles, Addic7edSubtitleDescriptor sub) {
        return lSubtitles.stream()
                .anyMatch(s -> s.getLanguage().equals(sub.getLanguage())
                        && s.getUrl().equals(sub.getUrl())
                        && s.getVersion().equals(sub.getVersion()));
    }

    private String getContent(boolean disk, String url) {
        try {
            if (disk) {
                return this.getHtmlDisk(url);
            } else {
                if (!speedy && !this.isCached(url)) {
                    if (ChronoUnit.SECONDS.between(lastRequest, LocalDateTime.now()) < RATEDURATION) {
                        LOGGER.info("RateLimiet is bereikt voor ADDIC7ed, gelieve 15 sec te wachten");
                    }
                    while (ChronoUnit.SECONDS.between(lastRequest, LocalDateTime.now()) < RATEDURATION) {
                        try {
                            // Pause for 1 seconds
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            // restore interrupted status
                            Thread.currentThread().interrupt();
                        }
                    }
                    lastRequest = LocalDateTime.now();
                }
                return this.getHtml(url);
            }
        } catch (HttpClientException | IOException | ManagerSetupException | ManagerException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "";
    }
}
