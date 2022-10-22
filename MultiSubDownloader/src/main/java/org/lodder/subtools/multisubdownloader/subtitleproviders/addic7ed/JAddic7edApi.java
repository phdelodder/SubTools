package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.exception.Addic7edException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.model.Addic7edSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.lazy.LazyThrowingSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class JAddic7edApi extends Html implements SubtitleApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(JAddic7edApi.class);
    private static final long RATEDURATION = 1; // seconds
    private static final String DOMAIN = "https://www.addic7ed.com";
    private final static Pattern TITLE_PATTERN = Pattern.compile(".*? - [0-9]+x[0-9]+ - (.*)");
    private final static Pattern VERSION_PATTERN = Pattern.compile("Version (.+), Duration: ([0-9]+).([0-9])+");
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

    public List<ProviderSerieId> getProviderId(String serieName) throws Addic7edException {
        if (StringUtils.isBlank(serieName)) {
            return List.of();
        }
        String serieNameFormatted = serieName.replaceAll("[^A-Za-z]", "");
        try {
            List<ProviderSerieId> providerSerieId = getAllMappings().stream()
                    .filter(providerId -> {
                        String formattedSerieName = providerId.getName().replaceAll("[^A-Za-z]", "");
                        return StringUtils.containsIgnoreCase(serieNameFormatted, formattedSerieName) ||
                                StringUtils.containsIgnoreCase(formattedSerieName, serieNameFormatted);
                    })
                    .toList();
            return providerSerieId.isEmpty() ? getAllMappings() : providerSerieId;
        } catch (Exception e) {
            throw new Addic7edException(e);
        }
    }

    private List<ProviderSerieId> getAllMappings() throws Addic7edException {
        return ALL_MAPPINGS.get();
    }

    private final LazyThrowingSupplier<List<ProviderSerieId>, Addic7edException> ALL_MAPPINGS =
            new LazyThrowingSupplier<>(() -> getContent(DOMAIN)
                    .map(doc -> doc.select("#qsShow option").stream()
                            .map(e -> new ProviderSerieId(e.text(), e.attr("value")))
                            .toList())
                    .orElseGet(List::of));

    public List<Addic7edSubtitleDescriptor> getSubtitles(SerieMapping addic7edSerieMapping, int season, int episode, Language language)
            throws Addic7edException {
        return getManager().valueBuilder()
                .memoryCache()
                .key("%s-subtitles-%s-%s-%s-%s".formatted(getSubtitleSource().name(), addic7edSerieMapping.getProviderId(), season, episode,
                        language))
                .collectionSupplier(Addic7edSubtitleDescriptor.class, () -> {

                    // http://www.addic7ed.com/serie/Smallville/9/11/Absolute_Justice
                    // String url = "https://www.addic7ed.com/serie/" + showname.toLowerCase().replace(" ", "_") + "/" + season
                    // + "/" + episode + "/" + title.toLowerCase().replace(" ", "_").replace("#", "");

                    // https://www.addic7ed.com/show/9026

                    List<LanguageId> languageIds = LanguageId.forLanguage(language);
                    String url = "%s/serie/%s/%s/%s/%s".formatted(
                            DOMAIN,
                            URLEncoder.encode(addic7edSerieMapping.getProviderId().toLowerCase().replace(" ", "_"), StandardCharsets.UTF_8),
                            season,
                            episode,
                            languageIds.size() == 1 ? languageIds.get(0).getId() : LanguageId.ALL.getId());

                    Optional<Document> doc = getContent(url);
                    if (doc.isEmpty()) {
                        return List.of();
                    }

                    String title = null;

                    Elements elTitel = doc.get().getElementsByClass("titulo");
                    if (elTitel.size() == 1) {
                        Matcher matcher = TITLE_PATTERN.matcher(elTitel.get(0).html());
                        if (matcher.matches()) {
                            title = matcher.group(1);
                        }
                    }

                    String uploader, version, lang, download = null;
                    boolean hearingImpaired = false;
                    Elements blocks = doc.get().select(".tabel95[width='100%']");

                    List<Addic7edSubtitleDescriptor> lSubtitles = new ArrayList<>();
                    for (Element block : blocks) {
                        uploader = "";
                        version = null;
                        lang = null;
                        download = null;
                        hearingImpaired = false;

                        Elements classesNewsTitle = block.getElementsByClass("NewsTitle");
                        Elements classesNewsDate = block.getElementsByClass("newsDate").select("td[colspan=3]");
                        Elements imgHearingImpaired = block.select("img[title~=Hearing]");
                        if (classesNewsTitle.size() == 1 && classesNewsDate.size() == 1) {
                            Matcher m = VERSION_PATTERN.matcher(classesNewsTitle.get(0).text().trim());
                            if (!m.matches()) {
                                break;
                            } else {
                                version = m.group(1).trim();
                                uploader = block.selectFirst("a[href*=user/]").text();
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
                                if ((lang != null && td.toString().toLowerCase().contains("completed"))
                                        && td.html().toLowerCase().contains("% completed")) {
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
                                if (lang != null && download != null && title != null) {
                                    Addic7edSubtitleDescriptor sub =
                                            new Addic7edSubtitleDescriptor()
                                                    .setUploader(uploader)
                                                    .setTitel(title.trim())
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
                }).getCollection();
    }

    public boolean isDuplicate(List<Addic7edSubtitleDescriptor> lSubtitles, Addic7edSubtitleDescriptor sub) {
        return lSubtitles.stream()
                .anyMatch(s -> s.getLanguage() == sub.getLanguage()
                        && StringUtils.equals(s.getUrl(), sub.getUrl())
                        && StringUtils.equals(s.getVersion(), sub.getVersion()));
    }

    private Optional<Document> getContent(String url) throws Addic7edException {
        return getContent(url, null);
    }

    private Optional<Document> getContent(String url, Predicate<String> emptyResultPredicate) throws Addic7edException {
        try {
            if (!speedy && !getManager().valueBuilder().cacheType(CacheType.MEMORY).key(url).isPresent()) {
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
            return this.getHtml(url).cacheType(CacheType.NONE).getAsJsoupDocument(emptyResultPredicate);
        } catch (Exception e) {
            throw new Addic7edException(e);
        }
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.ADDIC7ED;
    }
}
