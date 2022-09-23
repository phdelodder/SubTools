package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.exception.Addic7edException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.model.Addic7edSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class JAddic7edApi extends Html {

    private static final Logger LOGGER = LoggerFactory.getLogger(JAddic7edApi.class);
    private final Pattern pattern = Pattern.compile("Version (.+), Duration: ([0-9]+).([0-9])+ ");
    private final static long RATEDURATION = 1; // seconds
    private static final String DOMAIN = "https://www.addic7ed.com";
    private final boolean speedy;
    private LocalDateTime lastRequest = LocalDateTime.now();

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
        Map<String, String> data = Map.of("username", username, "password", password, "remember", "false");
        try {
            this.postHtml(DOMAIN + "/dologin.php", data);
        } catch (ManagerException e) {
            throw new Addic7edException(e);
        }
    }

    public Optional<String> getAddictedSerieName(String name) throws ManagerSetupException {
        String formattedName = name.replace(":", "").replace("-", "").replace("_", " ").replace(" ", "").trim().toLowerCase();

        return getValue(formattedName)
                .cacheType(CacheType.DISK)
                .optionalValueSupplier(() -> resultStringForName(name)
                        .map(doc -> doc.select("#season td:not(.c) > a").stream()
                                .map(serieFound -> {
                                    String link = serieFound.attr("href");
                                    String seriename = link.replace("/serie/", "");
                                    return seriename.substring(0, seriename.indexOf("/"));
                                })
                                .filter(seriename -> URLDecoder.decode(seriename, StandardCharsets.UTF_8).replace(":", "").replace("-", "")
                                        .replace("_", " ").replace(" ", "").trim().toLowerCase().equals(formattedName))
                                .findAny().orElse(null)))
                .getOptional();
    }

    public Optional<String> getAddictedMovieName(String name) throws RuntimeException, ManagerSetupException {
        return getValue(name)
                .cacheType(CacheType.DISK)
                .optionalValueSupplier(
                        () -> resultStringForName(name).map(doc -> {
                            Elements aTagWithSerie = doc.select("a[debug]");
                            String link = aTagWithSerie.get(0).attr("href");
                            String moviename = link.replace("movie/", "");
                            return moviename.substring(0, moviename.indexOf("/"));
                        }))
                .getOptional();
    }

    private Optional<Document> resultStringForName(String name) {
        String url = DOMAIN + "/search.php?search=" + URLEncoder.encode(name, StandardCharsets.UTF_8) + "&Submit=Search";

        Optional<String> content = getContent(url);
        if (content.isEmpty()) {
            return Optional.empty();
        }
        if (content.get().contains("<b>0 results found</b>")) {
            if (name.contains(":")) {
                return resultStringForName(name.replace(":", ""));
            } else {
                return Optional.empty();
            }
        }
        return content.map(Jsoup::parse);
    }

    public List<Addic7edSubtitleDescriptor> searchSubtitles(String showname, int season, int episode, String title, Language language) {
        // http://www.addic7ed.com/serie/Smallville/9/11/Absolute_Justice
        // String url = "https://www.addic7ed.com/serie/" + showname.toLowerCase().replace(" ", "_") + "/" + season
        // + "/" + episode + "/" + title.toLowerCase().replace(" ", "_").replace("#", "");

        StringBuilder url = new StringBuilder(DOMAIN + "/serie/").append(showname.toLowerCase().replace(" ", "_")).append("/")
                .append(season).append("/").append(episode).append("/");
        List<LanguageId> languageIds = LanguageId.forLanguage(language);
        url.append(languageIds.size() == 1 ? languageIds.get(0).getId() : LanguageId.ALL.getId());

        Optional<Document> doc = getContent(url.toString()).map(Jsoup::parse);
        if (doc.isEmpty()) {
            return List.of();
        }

        String titel = null;
        Elements elTitel = doc.get().getElementsByClass("titulo");
        if (elTitel.size() == 1) {
            titel = elTitel.get(0).html().substring(0, elTitel.get(0).html().indexOf("<") - 1).trim();
        }

        String uploader, version, lang, download = null;
        boolean hearingImpaired = false;
        Elements blocks = doc.get().getElementsByClass("tabel95");
        blocks = blocks.select("table[width=100%]");

        List<Addic7edSubtitleDescriptor> lSubtitles = new ArrayList<>();
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
                            download = DOMAIN + downloadElements.get(0).attr("href");
                        }
                        if (downloadElements.size() == 2) {
                            download = DOMAIN + downloadElements.get(1).attr("href");
                        }
                    }
                    if (lang != null && download != null && titel != null) {
                        Addic7edSubtitleDescriptor sub =
                                new Addic7edSubtitleDescriptor()
                                        .setUploader(uploader)
                                        .setTitel(titel.trim())
                                        .setVersion(version.trim())
                                        .setUrl(download)
                                        .setLanguage(Language.fromValueOptional(lang.trim()).orElse(null))
                                        .setHearingImpaired(hearingImpaired);
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
                .anyMatch(s -> s.getLanguage() == sub.getLanguage()
                        && StringUtils.equals(s.getUrl(), sub.getUrl())
                        && StringUtils.equals(s.getVersion(), sub.getVersion()));
    }

    private Optional<String> getContent(String url) {
        try {
            if (!speedy && !this.isUrlCached(url)) {
                // if (ChronoUnit.SECONDS.between(lastRequest, LocalDateTime.now()) < RATEDURATION) {
                // LOGGER.info("RateLimiet is bereikt voor ADDIC7ed, gelieve {} sec te wachten", RATEDURATION);
                // }
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
            return Optional.of(this.getHtml(url).cacheType(CacheType.MEMORY).get());
        } catch (ManagerException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
