package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed;

import static java.nio.charset.StandardCharsets.*;
import static manifold.science.util.UnitConstants.*;
import static org.lodder.subtools.sublibrary.util.Sleep.*;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import manifold.science.measures.Time;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.exception.Addic7edException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.model.Addic7edSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Credentials;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.PageContentParams;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;

public class JAddic7edApi implements SubtitleApi {

    private static final Time RATE_DURATION = 1 s; // seconds

    private static final String DOMAIN = "https://www.addic7ed.com";
    private static final Pattern TITLE_PATTERN = Pattern.compile(".*? - \\d+x\\d+ - (.*)");
    private static final Pattern VERSION_PATTERN = Pattern.compile("Version (.+), Duration: (\\d+).(\\d)+");
    private final Manager manager;
    private final boolean speedy;
    private Time lastRequest = Time.now();
    @val @override SubtitleSource subtitleSource = SubtitleSource.ADDIC7ED;

    public JAddic7edApi(Manager manager, boolean speedy, Credentials credentials=null) throws Addic7edException {
//        super(manager, "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)");
        this.manager = manager;
        this.speedy = speedy;
        if (credentials != null) {
            login(credentials);
        }
    }

    public void login(Credentials credentials) throws Addic7edException {
        try {
            manager.postBuilder("$DOMAIN/dologin.php")
                .addData("username", credentials.username)
                .addData("password", credentials.password)
                .addData("remember", "false")
                .post();
        } catch (ManagerException e) {
            throw new Addic7edException(e);
        }
    }

    public List<ProviderSerieId> getProviderId(String serieName) throws Addic7edException {
        if (StringUtils.isBlank(serieName)) {
            return List.of();
        }
        try {
            List<ProviderSerieId> providerSerieIds =
                getContent("$DOMAIN/allshows/" + serieName.split(" ")[0]).selectAllByCss("table.tabel90 td a")
                    .stream()
                    .map(elem -> new ProviderSerieId(elem.text(), elem.attr("href").split("/")[2]))
                    .toList();

            String serieNameFormatted = serieName.replaceAll("[^A-Za-z]", "");
            List<ProviderSerieId> providerSerieIdsFormatted = providerSerieIds.stream().filter(providerId -> {
                String formattedSerieName = providerId.name.replaceAll("[^A-Za-z]", "");
                return StringUtils.containsIgnoreCase(serieNameFormatted, formattedSerieName) ||
                    StringUtils.containsIgnoreCase(formattedSerieName, serieNameFormatted);
            }).toList();
            return !providerSerieIdsFormatted.isEmpty() ? providerSerieIdsFormatted : providerSerieIds;
        } catch (Exception e) {
            throw new Addic7edException(e);
        }
    }

    public List<Addic7edSubtitleDescriptor> getSubtitles(SerieMapping addic7edSerieMapping, int season, int episode,
        Language language) throws Addic7edException {

        return manager.getCache(CacheType.MEMORY,
                "%s-subtitles-%s-%s-%s-%s".formatted(subtitleSource.name(), addic7edSerieMapping.providerId,
                season, episode, language))
            .getCollection(() -> {
                List<LanguageId> languageIds = LanguageId.forLanguage(language);
                String url = "%s/serie/%s/%s/%s/%s".formatted(DOMAIN,
                    URLEncoder.encode(addic7edSerieMapping.providerName.replace(" ", "_"), UTF_8), season,
                    episode, languageIds.size() == 1 ? languageIds.first.id : LanguageId.ALL.id);

                Document doc = getContent(url);
                String title = null;

                Elements elTitle = doc.getElementsByClass("titulo");
                if (elTitle.size() == 1) {
                    Matcher matcher = TITLE_PATTERN.matcher(elTitle.first.html());
                    if (matcher.matches()) {
                        title = matcher.group(1);
                    }
                }

                Elements blocks = doc.select(".tabel95[width='100%']");

                List<Addic7edSubtitleDescriptor> lSubtitles = new ArrayList<>();
                for (Element block : blocks) {
                    String uploader = "";
                    String version = null;
                    String lang = null;
                    String download = null;
                    boolean hearingImpaired = false;

                    Elements classesNewsTitle = block.getElementsByClass("NewsTitle");
                    Elements classesNewsDate = block.getElementsByClass("newsDate").select("td[colspan=3]");
                    if (classesNewsTitle.size() == 1 && classesNewsDate.size() == 1) {
                        Matcher m = VERSION_PATTERN.matcher(classesNewsTitle.first.text().trim());
                        if (!m.matches()) {
                            break;
                        } else {
                            version = m.group(1).trim();
                            uploader = block.selectFirst("a[href*=user/]").text();
                            hearingImpaired = !block.select("img[title~=Hearing]").isEmpty();
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
                            if ((lang != null && td.toString().toLowerCase().contains("completed")) &&
                                td.html().toLowerCase().contains("% completed")) {
                                lang = null;
                            }

                            Elements downloadElements = td.getElementsByClass("buttonDownload");
                            if (lang != null && !downloadElements.isEmpty()) {
                                if (downloadElements.size() == 1) {
                                    download = DOMAIN + downloadElements.first.attr("href");
                                } else if (downloadElements.size() == 2) {
                                    download = DOMAIN + downloadElements.get(1).attr("href");
                                }
                            }
                            if (lang != null && download != null && title != null) {
                                Addic7edSubtitleDescriptor sub = new Addic7edSubtitleDescriptor(version.trim(),
                                    Language.fromValueOptional(lang.trim()).orElse(null), download, title.trim(),
                                    uploader, hearingImpaired);
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
            });
    }

    public boolean isDuplicate(List<Addic7edSubtitleDescriptor> lSubtitles, Addic7edSubtitleDescriptor sub) {
        return lSubtitles.stream().anyMatch(s -> s.language == sub.language && StringUtils.equals(s.url, sub.url) &&
            StringUtils.equals(s.version, sub.version));
    }

    private Document getContent(String url) throws Addic7edException {
        try {
            if (!speedy && !manager.getCache(CacheType.MEMORY, url).isPresent()) {
                // if (ChronoUnit.SECONDS.between(lastRequest, LocalDateTime.now()) < RATEDURATION) {
                // LOGGER.info("RateLimit is reached for ADDIC7ed, please wait {} seconds", RATEDURATION);
                // }
                Time timeToSleep = RATE_DURATION - Time.now() + lastRequest;
                if (timeToSleep.isPositive) {
                    sleep(timeToSleep);
                }
                lastRequest = Time.now();
            }
            return manager.getAsJsoupDocument(PageContentParams.params(url:url, userAgent:""));
        } catch (Exception e) {
            throw new Addic7edException(e);
        }
    }
}
