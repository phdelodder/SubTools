package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubsceneSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.PageContentBuilderCacheTypeIntf;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class SubsceneApi extends Html implements SubtitleApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsceneApi.class);
    private static final int RATEDURATION_SHORT = 1; // seconds
    private static final int RATEDURATION_LONG = 5; // seconds
    private static final String DOMAIN = "https://subscene.com";
    // private static final String SERIE_URL_PREFIX = DOMAIN + "/subtitles/";

    private static final Predicate<Exception> RETRY_PREDICATE =
            exception -> (exception instanceof HttpClientException httpClientException
                    && (httpClientException.getResponseCode() == 409 || httpClientException.getResponseCode() == 429))
                    || (exception instanceof ManagerException managerException && managerException.getMessage().contains("409 Conflict"));

    private int selectedLanguage;
    private boolean selectedIncludeHearingImpaired;

    private LocalDateTime lastRequest = LocalDateTime.now();

    public SubsceneApi(Manager manager) {
        super(manager, "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)");
        addCookie("ForeignOnly", "False");
    }

    /**
     *
     * @param serieName the serie name
     * @return a {@link Map} containing a list of {@link ProviderSerieId provider serie ids} per type
     * @throws SubsceneException
     */
    public Map<String, List<ProviderSerieId>> getSubSceneSerieNames(String serieName) throws SubsceneException {
        try {
            if (StringUtils.isBlank(serieName)) {
                return Map.of();
            }
            String url = DOMAIN + "/subtitles/searchbytitle?query=" + URLEncoder.encode(serieName, StandardCharsets.UTF_8);
            Element searchResultElement = getJsoupDocument(url).selectFirst(".search-result");

            return searchResultElement.select("h2").stream()
                    .map(titleElement -> Pair.of(titleElement.text(), titleElement.nextElementSibling().select("a").stream()
                            .map(aElem -> new ProviderSerieId(aElem.text(), aElem.attr("href"))).toList()))
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        } catch (Exception e) {
            throw new SubsceneException(e);
        }
    }

    public List<SubsceneSubtitleDescriptor> getSubtitles(SerieMapping providerSerieId, int season, int episode, Language language)
            throws SubsceneException {
        return getManager().valueBuilder().memoryCache()
                .key("%s-subtitles-%s-%s-%s-%s".formatted(getSubtitleSource().getName(), providerSerieId.getProviderId(), season, episode, language))
                .collectionSupplier(SubsceneSubtitleDescriptor.class, () -> {
                    setLanguageWithCookie(language);
                    try {
                        return getJsoupDocument(DOMAIN + providerSerieId.getProviderId())
                                .select("td.a1").stream().map(Element::parent)
                                .map(row -> new SubsceneSubtitleDescriptor()
                                        .setLanguage(Language.fromValueOptional(row.select(".a1 span.l").text().trim()).orElse(null))
                                        .setUrlSupplier(() -> getDownloadUrl(DOMAIN + row.select(".a1 > a").attr("href").trim()))
                                        .setName(row.select(".a1 span:not(.l)").text().trim())
                                        .setHearingImpaired(!row.select(".a41").isEmpty())
                                        .setUploader(row.select(".a5 > a").text().trim())
                                        .setComment(row.select(".a6 > div").text().trim()))
                                .filter(subDescriptor -> subDescriptor.getSeasonEpisode() != null
                                        && subDescriptor.getSeasonEpisode().getEpisodes().stream().anyMatch(ep -> ep == episode))
                                .toList();
                    } catch (Exception e) {
                        throw new SubsceneException(e);
                    }
                }).getCollection();
    }

    private String getDownloadUrl(String seriePageUrl) throws SubsceneException {
        try {
            return DOMAIN + getJsoupDocument(seriePageUrl).selectFirst("#downloadButton").attr("href");
        } catch (ManagerException e) {
            throw new SubsceneException(e);
        }
    }

    private Document getJsoupDocument(String url) throws ManagerException {
        while (ChronoUnit.SECONDS.between(lastRequest, LocalDateTime.now()) < RATEDURATION_SHORT) {
            sleepSeconds(1);
        }
        Document document = super.getHtml(url).retries(1).retryPredicate(RETRY_PREDICATE).retryWait(RATEDURATION_LONG).getAsJsoupDocument();
        lastRequest = LocalDateTime.now();
        return document;
    }

    @Override
    public PageContentBuilderCacheTypeIntf getHtml(String url) {
        throw new IllegalStateException("Should not be used, use getJsoupDocument");
    }

    private void setLanguageWithCookie(Language language) {
        int languageId = SUBSCENE_LANGS.get(language);
        if (selectedLanguage != languageId) {
            addCookie("LanguageFilter", String.valueOf(languageId));
            selectedLanguage = languageId;
        }
    }

    private void setIncludeHearingImpairedWithCookier(boolean includeHearingImpaired) {
        if (selectedIncludeHearingImpaired != includeHearingImpaired) {
            addCookie("HearingImpaired", includeHearingImpaired ? "2" : "0");
            selectedIncludeHearingImpaired = includeHearingImpaired;
        }
    }

    private void addCookie(String cookieName, String cookieValue) {
        getManager().storeCookies("subscene.com", Map.of(cookieName, cookieValue));
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.SUBSCENE;
    }

    private static final Map<Language, Integer> SUBSCENE_LANGS = Collections.unmodifiableMap(new EnumMap<>(Language.class) {
        private static final long serialVersionUID = 2950169212654074275L;

        {
            put(Language.ARABIC, 2);
            put(Language.BENGALI, 54);
            put(Language.PORTUGUESE, 4); // BRAZILLIAN PORTUGUESE
            put(Language.CHINESE_SIMPLIFIED, 7);
            put(Language.CZECH, 9);
            put(Language.DANISH, 10);
            put(Language.DUTCH, 11);
            put(Language.ENGLISH, 13);
            // put(Language.FARSI / PERSIAN, 46);
            put(Language.FINNISH, 17);
            put(Language.FRENCH, 18);
            put(Language.GERMAN, 19);
            put(Language.GREEK, 21);
            put(Language.HEBREW, 22);
            put(Language.INDONESIAN, 44);
            put(Language.ITALIAN, 26);
            put(Language.KOREAN, 28);
            put(Language.MALAY, 50);
            put(Language.NORWEGIAN, 30);
            put(Language.POLISH, 31);
            put(Language.PORTUGUESE, 32);
            put(Language.ROMANIAN, 33);
            put(Language.SPANISH, 38);
            put(Language.SWEDISH, 39);
            put(Language.THAI, 40);
            put(Language.TURKISH, 41);
            put(Language.VIETNAMESE, 45);
            put(Language.ALBANIAN, 1);
            put(Language.ARMENIAN, 73);
            put(Language.AZERBAIJANI, 55);
            // put(Language.BASQUE, 74);
            put(Language.BELARUSIAN, 68);
            put(Language.CHINESE_SIMPLIFIED, 3); // BIG 5 CODE
            put(Language.BOSNIAN, 60);
            put(Language.BULGARIAN, 5);
            // put(Language.BULGARIAN / ENGLISH, 6);
            // put(Language.BURMESE, 61);
            // put(Language.CAMBODIAN / KHMER, 79);
            put(Language.CATALAN, 49);
            put(Language.CROATIAN, 8);
            // put(Language.DUTCH / ENGLISH, 12);
            // put(Language.ENGLISH / GERMAN, 15);
            // put(Language.ESPERANTO, 47);
            put(Language.ESTONIAN, 16);
            // put(Language.GEORGIAN, 62);
            // put(Language.GREENLANDIC, 57);
            put(Language.HINDI, 51);
            put(Language.HUNGARIAN, 23);
            // put(Language.HUNGARIAN / ENGLISH, 24);
            put(Language.ICELANDIC, 25);
            put(Language.JAPANESE, 27);
            put(Language.KANNADA, 78);
            // put(Language.KINYARWANDA, 81);
            // put(Language.KURDISH, 52);
            put(Language.LATVIAN, 29);
            put(Language.LITHUANIAN, 43);
            put(Language.MACEDONIAN, 48);
            put(Language.MALAYALAM, 64);
            // put(Language.MANIPURI, 65);
            // put(Language.MONGOLIAN, 72);
            // put(Language.NEPALI, 80);
            // put(Language.PASHTO, 67);
            // put(Language.PUNJABI, 66);
            put(Language.RUSSIAN, 34);
            put(Language.SERBIAN, 35);
            put(Language.SINHALA, 58);
            put(Language.SLOVAK, 36);
            put(Language.SLOVENIAN, 37);
            // put(Language.SOMALI, 70);
            // put(Language.SUNDANESE, 76);
            // put(Language.SWAHILI, 75);
            put(Language.TAGALOG, 53);
            put(Language.TAMIL, 59);
            put(Language.TELUGU, 63);
            put(Language.UKRAINIAN, 56);
            // put(Language.URDU, 42);
            // put(Language.YORUBA, 71);

        }
    });
}
