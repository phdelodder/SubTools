package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubsceneSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.pivovarit.function.ThrowingSupplier;

public class SubsceneApi extends Html {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsceneApi.class);
    private static final String IDENTIFIER = SubtitleSource.SUBSCENE.name();
    private static final RuleBasedNumberFormat NUMBER_FORMAT = new RuleBasedNumberFormat(Locale.UK, RuleBasedNumberFormat.SPELLOUT);
    private static final long RATEDURATION_SHORT = 1; // seconds
    private static final long RATEDURATION_LONG = 5; // seconds
    private static final String DOMAIN = "https://subscene.com";

    private static final String COOKIE_LANG_NL = "11";
    private static final String COOKIE_LANG_EN = "13";

    private LocalDateTime lastRequest = LocalDateTime.now();

    public SubsceneApi(Manager manager) {
        super(manager, "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)");
        Map<String, String> cookies =
                Map.of("LanguageFilter", String.join(",", COOKIE_LANG_NL, COOKIE_LANG_EN), "HearingImpaired", "2", "ForeignOnly", "False");
        manager.storeCookies("subscene.com", cookies);
    }

    public List<SubsceneSubtitleDescriptor> getSubtilteDescriptors(String serieName, int season) throws SubsceneException {
        return retry(() -> {
            try {
                Optional<String> urlForSerie = getUrlForSerie(serieName, season);
                if (urlForSerie.isEmpty()) {
                    return List.of();
                }
                return Jsoup.parse(getHtml(urlForSerie.get()))
                        .select("td.a1").stream().map(Element::parent)
                        .map(row -> new SubsceneSubtitleDescriptor()
                                .setLanguage(row.select(".a1 span.l").text().trim())
                                .setUrlSupplier(() -> getDownloadUrl(DOMAIN + row.select(".a1 > a").attr("href").trim()))
                                .setName(row.select(".a1 span:not(.l)").text().trim())
                                .setHearingImpaired(!row.select(".a41").isEmpty())
                                .setUploader(row.select(".a5 > a").text().trim())
                                .setComment(row.select(".a6 > div").text().trim()))
                        .toList();
            } catch (IOException | HttpClientException | ManagerSetupException | ManagerException e) {
                throw new SubsceneException(e);
            }
        });
    }

    private String getDownloadUrl(String seriePageUrl) throws ManagerException {
        try {
            return DOMAIN + Jsoup.parse(getHtml(seriePageUrl)).selectFirst("#downloadButton").attr("href");
        } catch (IOException | HttpClientException | ManagerSetupException e) {
            throw new ManagerException(e);
        }
    }

    private Optional<String> getUrlForSerie(String serieName, int season) throws SubsceneException {
        return retry(() -> {
            ThrowingSupplier<Optional<String>, SubsceneException> valueSupplier = () -> {
                try {
                    String url = DOMAIN + "/subtitles/searchbytitle?query=" + URLEncoder.encode(serieName, StandardCharsets.UTF_8);
                    Element searchResultElement = Jsoup.parse(getHtml(url)).selectFirst(".search-result");
                    if (searchResultElement == null) {
                        return null;
                    }
                    return searchResultElement.select("h2").stream()
                            .filter(element -> "TV-Series".equals(element.text())).findFirst()
                            .map(Element::nextElementSibling)
                            .map(element -> element.selectFirst(".title a"))
                            .filter(element -> {
                                String[] split = element.text().trim().split(" - ");
                                return split.length > 1 && (NUMBER_FORMAT.format(season, "%spellout-ordinal") + " Season")
                                        .equalsIgnoreCase(split[split.length - 1].trim());
                            })
                            .map(element -> DOMAIN + element.attr("href"));
                } catch (ManagerException e) {
                    if (e.getCause() != null && e.getCause() instanceof HttpClientException httpClientException) {
                        throw new SubsceneException(e.getCause());
                    }
                    throw new SubsceneException(e);
                } catch (ManagerSetupException | HttpClientException | IOException e) {
                    throw new SubsceneException(e);
                }
            };
            try {
                return getOptionalValueDisk(IDENTIFIER + serieName + "_SEASON:" + season, valueSupplier);
            } catch (ManagerSetupException e) {
                throw new SubsceneException(e);
            }
        });
    }

    private <T> T retry(ThrowingSupplier<T, SubsceneException> supplier) throws SubsceneException {
        try {
            return supplier.get();
        } catch (SubsceneException e) {
            if (e.getCause() instanceof HttpClientException httpClientException
                    && httpClientException != null && httpClientException.getResponseCode() == 409) {
                LOGGER.info("RateLimiet is bereikt voor Subscene, gelieve {} sec te wachten", RATEDURATION_LONG);
                sleepSeconds(RATEDURATION_LONG);
                return supplier.get();
            }
            throw e;
        }
    }

    @Override
    public String getHtml(String url) throws IOException, HttpClientException, ManagerSetupException, ManagerException {
        while (ChronoUnit.SECONDS.between(lastRequest, LocalDateTime.now()) < RATEDURATION_SHORT) {
            sleepSeconds(1);
        }
        lastRequest = LocalDateTime.now();
        return super.getHtml(url);
    }
}
