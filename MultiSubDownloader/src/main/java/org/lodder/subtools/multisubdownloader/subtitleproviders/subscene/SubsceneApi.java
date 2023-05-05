package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene;

import java.io.Serial;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubSceneSerieId;
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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class SubsceneApi extends Html implements SubtitleApi {

    private static final int RATEDURATION_SHORT = 1; // seconds
    private static final int RATEDURATION_LONG = 5; // seconds
    private static final String DOMAIN = "https://subscene.com";
    // private static final String SERIE_URL_PREFIX = DOMAIN + "/subtitles/";
    private static final Pattern SERIE_NAME_PATTERN = Pattern.compile(".*? - ([A-Z][a-z]*) Season.*");

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
     * @param serieName the serie name
     * @return a {@link Map} containing a list of {@link ProviderSerieId provider serie ids} per type
     * @throws SubsceneException SubsceneException
     */
    public Map<String, List<SubSceneSerieId>> getSubSceneSerieNames(String serieName) throws SubsceneException {
        try {
            if (StringUtils.isBlank(serieName)) {
                return Map.of();
            }
            String url = DOMAIN + "/subtitles/searchbytitle?query=" + URLEncoder.encode(serieName, StandardCharsets.UTF_8);
            Element searchResultElement = getJsoupDocument(url).selectFirst(".search-result");

            return searchResultElement.select("h2").stream()
                    .map(titleElement -> Pair.of(titleElement.text(), titleElement.nextElementSibling().select("a").stream()
                            .map(aElem -> {
                                Matcher matcher = SERIE_NAME_PATTERN.matcher(aElem.text());
                                int season = 0;
                                if (matcher.matches()) {
                                    season = OrdinalNumber.optionalFromValue(matcher.group(1)).mapToInt(OrdinalNumber::getNumber).orElse(-1);
                                }
                                return new SubSceneSerieId(aElem.text(), aElem.attr("href"), season);
                            }).toList()))
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

    private void setIncludeHearingImpairedWithCookie(boolean includeHearingImpaired) {
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
        @Serial
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

    @Getter
    @RequiredArgsConstructor
    private enum OrdinalNumber {
        ZEROTH(0, "Zeroth"),
        FIRST(1, "First"),
        SECOND(2, "Second"),
        THIRD(3, "Third"),
        FOURTH(4, "Fourth"),
        FIFTH(5, "Fifth"),
        SIXTH(6, "Sixth"),
        SEVENTH(7, "Seventh"),
        EIGHTH(8, "Eighth"),
        NINTH(9, "Ninth"),
        TENTH(10, "Tenth"),
        ELEVENTH(11, "Eleventh"),
        TWELFTH(12, "Twelfth"),
        THIRTEENTH(13, "Thirteenth"),
        FOURTEENTH(14, "Fourteenth"),
        FIFTEENTH(15, "Fifteenth"),
        SIXTEENTH(16, "Sixteenth"),
        SEVENTEENTH(17, "Seventeenth"),
        EIGHTEENTH(18, "Eighteenth"),
        NINETEENTH(19, "Nineteenth"),
        TWENTIETH(20, "Twentieth"),
        TWENTY_FIRST(21, "Twenty-First"),
        TWENTY_SECOND(22, "Twenty-Second"),
        TWENTY_THIRD(23, "Twenty-Third"),
        TWENTY_FOURTH(24, "Twenty-Fourth"),
        TWENTY_FIFTH(25, "Twenty-Fifth"),
        TWENTY_SIXTH(26, "Twenty-Sixth"),
        TWENTY_SEVENTH(27, "Twenty-Seventh"),
        TWENTY_EIGHTH(28, "Twenty-Eighth"),
        TWENTY_NINTH(29, "Twenty-Ninth"),
        THIRTIETH(30, "Thirtieth"),
        THIRTHY_FIRST(31, "Thirty-First"),
        THIRTHY_SECOND(32, "Thirty-Second"),
        THIRTHY_THIRD(33, "Thirty-Third"),
        THIRTHY_FOURTH(34, "Thirty-Fourth"),
        THIRTHY_FIFTH(35, "Thirty-Fifth"),
        THIRTHY_SIXTH(36, "Thirty-Sixth"),
        THIRTHY_SEVENTH(37, "Thirty-Seventh"),
        THIRTHY_EIGHTH(38, "Thirty-Eighth"),
        THIRTHY_NINTH(39, "Thirty-Ninth"),
        FORTIETH(40, "Fortieth"),
        FORTY_FIRST(41, "Forty-First"),
        FORTY_SECOND(42, "Forty-Second"),
        FORTY_THIRD(43, "Forty-Third"),
        FORTY_FOURTH(44, "Forty-Fourth"),
        FORTY_FIFTH(45, "Forty-Fifth"),
        FORTY_SIXTH(46, "Forty-Sixth"),
        FORTY_SEVENTH(47, "Forty-Seventh"),
        FORTY_EIGHTH(48, "Forty-Eighth"),
        FORTY_NINTH(49, "Forty-Ninth"),
        FIFTIETH(50, "Fiftieth"),
        FIFTY_FIRST(51, "Fifty-First"),
        FIFTY_SECOND(52, "Fifty-Second"),
        FIFTY_THIRD(53, "Fifty-Third"),
        FIFTY_FOURTH(54, "Fifty-Fourth"),
        FIFTY_FIFTH(55, "Fifty-Fifth"),
        FIFTY_SIXTH(56, "Fifty-Sixth"),
        FIFTY_SEVENTH(57, "Fifty-Seventh"),
        FIFTY_EIGHTH(58, "Fifty-Eighth"),
        FIFTY_NINTH(59, "Fifty-Ninth"),
        SIXTIETH(60, "Sixtieth"),
        SIXTY_FIRST(61, "Sixty-First"),
        SIXTY_SECOND(62, "Sixty-Second"),
        SIXTY_THIRD(63, "Sixty-Third"),
        SIXTY_FOURTH(64, "Sixty-Fourth"),
        SIXTY_FIFTH(65, "Sixty-Fifth"),
        SIXTY_SIXTH(66, "Sixty-Sixth"),
        SIXTY_SEVENTH(67, "Sixty-Seventh"),
        SIXTY_EIGHTH(68, "Sixty-Eighth"),
        SIXTY_NINTH(69, "Sixty-Ninth"),
        SEVENTIETH(70, "Seventieth"),
        SEVENTY_FIRST(71, "Seventy-First"),
        SEVENTY_SECOND(72, "Seventy-Second"),
        SEVENTY_THIRD(73, "Seventy-Third"),
        SEVENTY_FOURTH(74, "Seventy-Fourth"),
        SEVENTY_FIFTH(75, "Seventy-Fifth"),
        SEVENTY_SIXTH(76, "Seventy-Sixth"),
        SEVENTY_SEVENTH(77, "Seventy-Seventh"),
        SEVENTY_EIGHTH(78, "Seventy-Eighth"),
        SEVENTY_NINTH(79, "Seventy-Ninth"),
        EIGHTIETH(80, "Eightieth"),
        EIGHTY_FIRST(81, "Eighty-First"),
        EIGHTY_SECOND(82, "Eighty-Second"),
        EIGHTY_THIRD(83, "Eighty-Third"),
        EIGHTY_FOURTH(84, "Eighty-Fourth"),
        EIGHTY_FIFTH(85, "Eighty-Fifth"),
        EIGHTY_SIXTH(86, "Eighty-Sixth"),
        EIGHTY_SEVENTH(87, "Eighty-Seventh"),
        EIGHTY_EIGHTH(88, "Eighty-Eighth"),
        EIGHTY_NINTH(89, "Eighty-Ninth"),
        NINETIETH(90, "Ninetieth"),
        NINETY_FIRST(91, "Ninety-First"),
        NINETY_SECOND(92, "Ninety-Second"),
        NINETY_THIRD(93, "Ninety-Third"),
        NINETY_FOURTH(94, "Ninety-Fourth"),
        NINETY_FIFTH(95, "Ninety-Fifth"),
        NINETY_SIXTH(96, "Ninety-Sixth"),
        NINETY_SEVENTH(97, "Ninety-Seventh"),
        NINETY_EIGHTH(98, "Ninety-Eighth"),
        NINETY_NINTH(99, "Ninety-Ninth"),
        HUNDREDTH(100, "Hundredth");

        private final int number;
        private final String value;

        public static Optional<OrdinalNumber> optionalFromValue(String value) {
            return Stream.of(OrdinalNumber.values()).filter(ordinalNumber -> StringUtils.equalsIgnoreCase(value, ordinalNumber.getValue()))
                    .findAny();
        }
    }

    public static String getOrdinalName(int ordinal) {
        if (ordinal < 0 || ordinal > 100) {
            return "not defined";
        }
        return OrdinalNumber.values()[ordinal].getValue();
    }
}
