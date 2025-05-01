package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene;

import static manifold.science.measures.TimeUnit.*;
import static org.lodder.subtools.sublibrary.util.Sleep.*;

import java.io.Serial;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.pivovarit.function.ThrowingSupplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import manifold.science.measures.Time;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception.SubsceneException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubSceneSerieId;
import org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model.SubsceneSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.Retry;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.PageContentParams;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import util.Utils;

public class SubsceneApi implements SubtitleApi {

    private static final Time RATE_DURATION_SHORT = 1 Second;
    private static final Time RATE_DURATION_LONG = 5 Second;
    private static final String DOMAIN = "https://subscene.com";
    private static final Pattern SERIE_NAME_PATTERN = Pattern.compile(".*? - ([A-Z][a-z]*) Season.*");

    private static final Predicate<Exception> RETRY_PREDICATE = exception -> switch (exception) {
        case HttpClientException httpClientException ->
            httpClientException.responseCode == 409 || httpClientException.responseCode == 429;
        case ManagerException managerException -> managerException.getMessage().contains("409 Conflict");
        default -> false;
    };

    private final Manager manager;
    private int selectedLanguage;
    private boolean selectedIncludeHearingImpaired;

    private Time lastRequest = Time.now();
    @val @override SubtitleSource subtitleSource = SubtitleSource.SUBSCENE;

    public SubsceneApi(Manager manager) {
//        super(manager, "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)");
        this.manager = manager;
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
            String url = "$DOMAIN/subtitles/searchbytitle?query=" + serieName.urlEncode();
            return getJsoupDocument(url).selectFirstByClass("search-result").selectAllByTag("h2")
                .stream()
                .collect(Utils.mapCollector((map, titleElement) -> map.put(titleElement.text(),
                    titleElement.nextElementSibling().selectAllByTag("a").stream().map(elem -> {
                        Matcher matcher = SERIE_NAME_PATTERN.matcher(elem.text());
                        int season = 0;
                        if (matcher.matches()) {
                            season = OrdinalNumber.optionalFromValue(matcher.group(1))
                                .mapToInt(OrdinalNumber::getNumber).orElse(-1);
                        }
                        return new SubSceneSerieId(elem.text(), elem.attr("href"), season);
                    }).toList())));
        } catch (Exception e) {
            throw new SubsceneException(e);
        }
    }

    public List<SubsceneSubtitleDescriptor> getSubtitles(SerieMapping providerSerieId, int season, int episode,
        Language language) throws SubsceneException {
        return manager.getCache(CacheType.MEMORY, "%s-subtitles-%s-%s-%s-%s".formatted(subtitleSource.name,
                providerSerieId.providerId, season, episode, language))
            .getCollection(() -> {
                setLanguageWithCookie(language);
                try {
                    return getJsoupDocument(DOMAIN + providerSerieId.providerId)
                        .selectAllByCss("td.a1")
                        .stream()
                        .map(el -> (Element) el.parent())
                        .map(row -> {
                            Language lang = Language.fromValueOptional(row.selectAllByCss(".a1 span.l").text().trim())
                                .orElse(null);
                            String name = row.selectAllByCss(".a1 span:not(.l)").text().trim();
                            boolean hearingImpaired = row.selectFirstByCss(".a41") != null;
                            String uploader = row.selectFirstByCss(".a5 > a").text().trim();
                            String comment = row.selectFirstByCss(".a6 > div").text().trim();
                            ThrowingSupplier<String, SubsceneException> urlSupplier = () -> getDownloadUrl(
                                DOMAIN + row.selectAllByCss(".a1 > a").attr("href").trim());
                            return new SubsceneSubtitleDescriptor(lang, name, hearingImpaired, uploader, comment,
                                urlSupplier);
                        })
                        .filter(subDescriptor -> subDescriptor.seasonEpisode != null &&
                            subDescriptor.seasonEpisode.containsEpisode(episode))
                        .toList();
                } catch (Exception e) {
                    throw new SubsceneException(e);
                }
            });
    }

    private String getDownloadUrl(String seriePageUrl) throws SubsceneException {
        try {
            String href = getJsoupDocument(seriePageUrl).selectFirstById("downloadButton").attr("href");
            if (StringUtils.isBlank(href)) {
                throw new SubsceneException("href for $seriePageUrl is blank");
            }
            return DOMAIN + href;
        } catch (ManagerException e) {
            throw new SubsceneException(e);
        }
    }

    private Document getJsoupDocument(String url) throws ManagerException {
        Time timeToSleep = RATE_DURATION_SHORT - Time.now() + lastRequest;
        if (timeToSleep.isPositive) {
            sleep(timeToSleep);
        }

        Document document =
            manager.getAsJsoupDocument(PageContentParams.params(
                url:url,
                userAgent:"",
                retry:new Retry(1, RETRY_PREDICATE, RATE_DURATION_LONG)));
        lastRequest = Time.now();
        return document;
    }

    private void setLanguageWithCookie(Language language) {
        int languageId = SUBSCENE_LANGS.get(language);
        if (selectedLanguage != languageId) {
            addCookie("LanguageFilter", String.valueOf(languageId));
            selectedLanguage = languageId;
        }
    }

    @SuppressWarnings("unused")
    private void setIncludeHearingImpairedWithCookie(boolean includeHearingImpaired) {
        if (selectedIncludeHearingImpaired != includeHearingImpaired) {
            addCookie("HearingImpaired", includeHearingImpaired ? "2" : "0");
            selectedIncludeHearingImpaired = includeHearingImpaired;
        }
    }

    private void addCookie(String cookieName, String cookieValue) {
        manager.storeCookies("subscene.com", Map.of(cookieName, cookieValue));
    }

    private static final Map<Language, Integer> SUBSCENE_LANGS =
        Collections.unmodifiableMap(new EnumMap<>(Language.class) {
            @Serial private static final long serialVersionUID = 2950169212654074275L;

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
        THIRTY_FIRST(31, "Thirty-First"),
        THIRTY_SECOND(32, "Thirty-Second"),
        THIRTY_THIRD(33, "Thirty-Third"),
        THIRTY_FOURTH(34, "Thirty-Fourth"),
        THIRTY_FIFTH(35, "Thirty-Fifth"),
        THIRTY_SIXTH(36, "Thirty-Sixth"),
        THIRTY_SEVENTH(37, "Thirty-Seventh"),
        THIRTY_EIGHTH(38, "Thirty-Eighth"),
        THIRTY_NINTH(39, "Thirty-Ninth"),
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
            return Stream.of(OrdinalNumber.values())
                .filter(ordinalNumber -> StringUtils.equalsIgnoreCase(value, ordinalNumber.getValue()))
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
