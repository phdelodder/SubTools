package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubsceneSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.PageContentBuilderCacheTypeIntf;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pivovarit.function.ThrowingSupplier;

public class SubsceneApi extends Html {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubsceneApi.class);
    private static final String IDENTIFIER = SubtitleSource.SUBSCENE.name();
    private static final long RATEDURATION_SHORT = 1; // seconds
    private static final long RATEDURATION_LONG = 5; // seconds
    private static final String DOMAIN = "https://subscene.com";

    private int selectedLanguage;
    private boolean selectedIncludeHearingImpaired;

    private LocalDateTime lastRequest = LocalDateTime.now();

    public SubsceneApi(Manager manager) {
        super(manager, "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)");
        addCookie("ForeignOnly", "False");
    }

    public List<SubsceneSubtitleDescriptor> getSubtilteDescriptors(String serieName, int season, Language language)
            throws SubsceneException {
        setLanguageWithCookie(language);
        return retry(() -> {
            try {
                Optional<String> urlForSerie = getUrlForSerie(serieName, season);
                if (urlForSerie.isEmpty()) {
                    return List.of();
                }
                return getHtml(urlForSerie.get()).cacheType(CacheType.MEMORY).getAsJsoupDocument()
                        .select("td.a1").stream().map(Element::parent)
                        .map(row -> new SubsceneSubtitleDescriptor()
                                .setLanguage(Language.fromValueOptional(row.select(".a1 span.l").text().trim()).orElse(null))
                                .setUrlSupplier(() -> getDownloadUrl(DOMAIN + row.select(".a1 > a").attr("href").trim()))
                                .setName(row.select(".a1 span:not(.l)").text().trim())
                                .setHearingImpaired(!row.select(".a41").isEmpty())
                                .setUploader(row.select(".a5 > a").text().trim())
                                .setComment(row.select(".a6 > div").text().trim()))
                        .toList();
            } catch (Exception e) {
                throw new SubsceneException(e);
            }
        });
    }

    private String getDownloadUrl(String seriePageUrl) throws SubsceneException {
        try {
            return DOMAIN + getHtml(seriePageUrl).cacheType(CacheType.NONE).getAsJsoupDocument().selectFirst("#downloadButton").attr("href");
        } catch (ManagerException e) {
            throw new SubsceneException(e);
        }
    }

    private Optional<String> getUrlForSerie(String serieName, int season) throws SubsceneException {
        return retry(() -> getValue(IDENTIFIER + serieName + "_SEASON:" + season)
                .cacheType(CacheType.MEMORY)
                .optionalValueSupplier(() -> {
                    try {
                        String url = DOMAIN + "/subtitles/searchbytitle?query=" + URLEncoder.encode(serieName, StandardCharsets.UTF_8);
                        Element searchResultElement =
                                getHtml(url).cacheType(CacheType.MEMORY).getAsJsoupDocument().selectFirst(".search-result");
                        Pattern elementNamePattern = Pattern.compile("(.*) - (.*?) Season.*?");
                        return searchResultElement.select("h2").stream()
                                .filter(element -> "TV-Series".equals(element.text())).findFirst().stream()
                                .map(Element::nextElementSibling)
                                .flatMap(element -> element.select(".title a").stream())
                                .filter(element -> {
                                    Matcher matcher = elementNamePattern.matcher(element.text());
                                    return matcher.matches() && StringUtils.equalsIgnoreCase(matcher.group(1), serieName)
                                            && StringUtils.equalsIgnoreCase(matcher.group(2), getOrdinalName(season));
                                })
                                .map(element -> DOMAIN + element.attr("href")).findFirst();
                    } catch (Exception e) {
                        if (e.getCause() != null && e.getCause() instanceof HttpClientException httpClientException) {
                            throw new SubsceneException(e.getCause());
                        }
                        throw new SubsceneException(e);
                    }
                })
                .getOptional());
    }

    private <T> T retry(ThrowingSupplier<T, SubsceneException> supplier) throws SubsceneException {
        try {
            return supplier.get();
        } catch (SubsceneException e) {
            Throwable cause = e.getCause();
            while (cause instanceof SubsceneException) {
                cause = cause.getCause();
            }
            if (cause == null) {
                throw e;
            }
            if ((cause instanceof HttpClientException httpClientException
                    && httpClientException != null && httpClientException.getResponseCode() == 409)
                    || (cause instanceof ManagerException && cause.getMessage().contains("409 Conflict"))) {
                LOGGER.info("RateLimiet is bereikt voor Subscene, gelieve {} sec te wachten", RATEDURATION_LONG);
                sleepSeconds(RATEDURATION_LONG);
                return supplier.get();
            }
            throw e;
        }
    }

    @Override
    public PageContentBuilderCacheTypeIntf getHtml(String url) {
        while (ChronoUnit.SECONDS.between(lastRequest, LocalDateTime.now()) < RATEDURATION_SHORT) {
            sleepSeconds(1);
        }
        PageContentBuilderCacheTypeIntf html = super.getHtml(url);
        lastRequest = LocalDateTime.now();
        return html;
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

    private String getOrdinalName(int ordinal) {
        return switch (ordinal) {
            case 1 -> "First";
            case 2 -> "Second";
            case 3 -> "Third";
            case 4 -> "Fourth";
            case 5 -> "Fifth";
            case 6 -> "Sixth";
            case 7 -> "Seventh";
            case 8 -> "Eighth";
            case 9 -> "Ninth";
            case 10 -> "Tenth";
            case 11 -> "Eleventh";
            case 12 -> "Twelfth";
            case 13 -> "Thirteenth";
            case 14 -> "Fourteenth";
            case 15 -> "Fifteenth";
            case 16 -> "Sixteenth";
            case 17 -> "Seventeenth";
            case 18 -> "Eighteenth";
            case 19 -> "Nineteenth";
            case 20 -> "Twentieth";
            case 21 -> "Twenty-First";
            case 22 -> "Twenty-Second";
            case 23 -> "Twenty-Third";
            case 24 -> "Twenty-Fourth";
            case 25 -> "Twenty-Fifth";
            case 26 -> "Twenty-Sixth";
            case 27 -> "Twenty-Seventh";
            case 28 -> "Twenty-Eighth";
            case 29 -> "Twenty-Ninth";
            case 30 -> "Thirtieth";
            case 31 -> "Thirty-First";
            case 32 -> "Thirty-Second";
            case 33 -> "Thirty-Third";
            case 34 -> "Thirty-Fourth";
            case 35 -> "Thirty-Fifth";
            case 36 -> "Thirty-Sixth";
            case 37 -> "Thirty-Seventh";
            case 38 -> "Thirty-Eighth";
            case 39 -> "Thirty-Ninth";
            case 40 -> "Fortieth";
            case 41 -> "Forty-First";
            case 42 -> "Forty-Second";
            case 43 -> "Forty-Third";
            case 44 -> "Forty-Fourth";
            case 45 -> "Forty-Fifth";
            case 46 -> "Forty-Sixth";
            case 47 -> "Forty-Seventh";
            case 48 -> "Forty-Eighth";
            case 49 -> "Forty-Ninth";
            case 50 -> "Fiftieth";
            case 51 -> "Fifty-First";
            case 52 -> "Fifty-Second";
            case 53 -> "Fifty-Third";
            case 54 -> "Fifty-Fourth";
            case 55 -> "Fifty-Fifth";
            case 56 -> "Fifty-Sixth";
            case 57 -> "Fifty-Seventh";
            case 58 -> "Fifty-Eighth";
            case 59 -> "Fifty-Ninth";
            case 60 -> "Sixtieth";
            case 61 -> "Sixty-First";
            case 62 -> "Sixty-Second";
            case 63 -> "Sixty-Third";
            case 64 -> "Sixty-Fourth";
            case 65 -> "Sixty-Fifth";
            case 66 -> "Sixty-Sixth";
            case 67 -> "Sixty-Seventh";
            case 68 -> "Sixty-Eighth";
            case 69 -> "Sixty-Ninth";
            case 70 -> "Seventieth";
            case 71 -> "Seventy-First";
            case 72 -> "Seventy-Second";
            case 73 -> "Seventy-Third";
            case 74 -> "Seventy-Fourth";
            case 75 -> "Seventy-Fifth";
            case 76 -> "Seventy-Sixth";
            case 77 -> "Seventy-Seventh";
            case 78 -> "Seventy-Eighth";
            case 79 -> "Seventy-Ninth";
            case 80 -> "Eightieth";
            case 81 -> "Eighty-First";
            case 82 -> "Eighty-Second";
            case 83 -> "Eighty-Third";
            case 84 -> "Eighty-Fourth";
            case 85 -> "Eighty-Fifth";
            case 86 -> "Eighty-Sixth";
            case 87 -> "Eighty-Seventh";
            case 88 -> "Eighty-Eighth";
            case 89 -> "Eighty-Ninth";
            case 90 -> "Ninetieth";
            case 91 -> "Ninety-First";
            case 92 -> "Ninety-Second";
            case 93 -> "Ninety-Third";
            case 94 -> "Ninety-Fourth";
            case 95 -> "Ninety-Fifth";
            case 96 -> "Ninety-Sixth";
            case 97 -> "Ninety-Seventh";
            case 98 -> "Ninety-Eighth";
            case 99 -> "Ninety-Ninth";
            case 100 -> "Hundredth";
            default -> "not defined";
        };
    }

    private static final Map<Language, Integer> SUBSCENE_LANGS = Collections
            .unmodifiableMap(new EnumMap<>(Language.class) {
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
