package org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.exception.PodnapisiException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.model.PodnapisiSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.lodder.subtools.sublibrary.xml.XmlExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

@Getter(value = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@ExtensionMethod({ XmlExtension.class, OptionalExtension.class })
public class JPodnapisiApi implements SubtitleApi {

    public static final int maxAge = 90;
    private static final Logger LOGGER = LoggerFactory.getLogger(JPodnapisiApi.class);
    private static final String DOMAIN = "https://www.podnapisi.net";
    private final Manager manager;
    private final String userAgent;
    private LocalDateTime nextCheck;

    public Optional<ProviderSerieId> getPodnapisiShowName(String showName) throws PodnapisiException {
        String url = DOMAIN + "/sl/ppodnapisi/search?sK=" + URLEncoder.encode(showName.trim().toLowerCase(), StandardCharsets.UTF_8);
        return getXml(url).selectFirst(".subtitle-entry") != null
                ? Optional.of(new ProviderSerieId(showName, showName))
                : Optional.empty();
    }

    public List<PodnapisiSubtitleDescriptor> getMovieSubtitles(String movieName, int year, int season, int episode, Language language)
            throws PodnapisiException {
        return getSubtitles(new SerieMapping(movieName, movieName, movieName), year, season, episode, language);

    }

    public List<PodnapisiSubtitleDescriptor> getSerieSubtitles(SerieMapping providerSerieId, int season, int episode, Language language)
            throws PodnapisiException {
        return getSubtitles(providerSerieId, null, season, episode, language);

    }

    private List<PodnapisiSubtitleDescriptor> getSubtitles(SerieMapping providerSerieId, Integer year, int season, int episode, Language language)
            throws PodnapisiException {
        return getManager().valueBuilder()
                .memoryCache()
                .key("%s-subtitles-%s-%s-%s-%s".formatted(getSubtitleSource().name(), providerSerieId.getProviderId(), season, episode, language))
                .collectionSupplier(PodnapisiSubtitleDescriptor.class, () -> {
                    try {
                        StringBuilder url = new StringBuilder(DOMAIN + "/sl/ppodnapisi/search?sK=")
                                .append(URLEncoder.encode(providerSerieId.getProviderId().trim().toLowerCase(), StandardCharsets.UTF_8));
                        if (PODNAPISI_LANGS.containsKey(language)) {
                            url.append("&sJ=").append(PODNAPISI_LANGS.get(language));
                        }
                        if (year != null) {
                            url.append("&sY=").append(year);
                        }
                        if (season > 0) {
                            url.append("&sTS=").append(season).append("&sT=1"); // series
                        } else {
                            url.append("&sT=0"); // movies
                        }
                        if (episode > 0) {
                            url.append("&sTE=").append(episode);
                        }
                        url.append("&sXML=1");

                        return getXml(url.toString()).select("subtitle").stream().map(this::parsePodnapisiSubtitle).toList();
                    } catch (Exception e) {
                        throw new PodnapisiException(e);
                    }
                })
                .getCollection();
    }


    protected Document getXml(String url) throws PodnapisiException {
        try {
            return manager.getPageContentBuilder().url(url).userAgent(getUserAgent()).cacheType(CacheType.MEMORY).retries(1)
                    .retryPredicate(e -> e instanceof HttpClientException httpClientException && httpClientException.getResponseCode() >= 500
                            && httpClientException.getResponseCode() < 600)
                    .retryWait(5).getAsJsoupDocument();
        } catch (Exception e) {
            throw new PodnapisiException(e);
        }
    }

    private PodnapisiSubtitleDescriptor parsePodnapisiSubtitle(Element elem) {
        Function<Element, String> getText = e -> e == null ? null : e.text();
        return PodnapisiSubtitleDescriptor.builder()
                .hearingImpaired(elem.select("new_flags flags").stream().anyMatch(flagElem -> "hearing_impaired".equals(flagElem.text())))
                .language(languageIdToLanguage(elem.selectFirst("languageId").text()))
                .releaseString(elem.selectFirst("release").text().length() > 10 ? elem.selectFirst("release").text()
                        : elem.selectFirst("title").text().replace(":", "") + " " + elem.selectFirst("release").text())
                .uploaderName(elem.selectFirst("uploaderName").text())
                .url(elem.selectFirst("url").text() + "/download?")
                .subtitleId(elem.selectFirst("id").text())
                .year(getText.apply(elem.selectFirst("year")))
                .imdb(getText.apply(elem.selectFirst("imdb")))
                .omdb(getText.apply(elem.selectFirst("omdb")))
                .build();
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.PODNAPISI;
    }

    private Language languageIdToLanguage(String languageId) {
        return PODNAPISI_LANGS.entrySet().stream().filter(entry -> entry.getValue().equals(languageId)).map(Entry::getKey).findFirst().orElse(null);
    }

    private static final Map<Language, String> PODNAPISI_LANGS = Collections
            .unmodifiableMap(new EnumMap<>(Language.class) {
                private static final long serialVersionUID = 2950169212654074275L;

                {
                    put(Language.SLOVENIAN, "1");
                    put(Language.ENGLISH, "2");
                    put(Language.NORWEGIAN, "3");
                    put(Language.KOREAN, "4");
                    put(Language.GERMAN, "5");
                    put(Language.ICELANDIC, "6");
                    put(Language.CZECH, "7");
                    put(Language.FRENCH, "8");
                    put(Language.ITALIAN, "9");
                    put(Language.BOSNIAN, "10");
                    put(Language.JAPANESE, "11");
                    put(Language.ARABIC, "12");
                    put(Language.ROMANIAN, "13");
                    put(Language.SPANISH, "14"); // es-ar Spanish (Argentina)
                    put(Language.HUNGARIAN, "15");
                    put(Language.GREEK, "16");
                    put(Language.CHINESE_SIMPLIFIED, "17");
                    put(Language.LITHUANIAN, "19");
                    put(Language.ESTONIAN, "20");
                    put(Language.LATVIAN, "21");
                    put(Language.HEBREW, "22");
                    put(Language.DUTCH, "23");
                    put(Language.DANISH, "24");
                    put(Language.SWEDISH, "25");
                    put(Language.POLISH, "26");
                    put(Language.RUSSIAN, "27");
                    put(Language.SPANISH, "28");
                    put(Language.ALBANIAN, "29");
                    put(Language.TURKISH, "30");
                    put(Language.FINNISH, "31");
                    put(Language.PORTUGUESE, "32");
                    put(Language.BULGARIAN, "33");
                    put(Language.MACEDONIAN, "35");
                    put(Language.SLOVAK, "37");
                    put(Language.CROATIAN, "38");
                    put(Language.CHINESE_SIMPLIFIED, "40");
                    put(Language.HINDI, "42");
                    put(Language.THAI, "44");
                    put(Language.UKRAINIAN, "46");
                    put(Language.SERBIAN, "47");
                    put(Language.PORTUGUESE, "48"); // Portuguese (Brazil)
                    put(Language.IRISH, "49");
                    put(Language.BELARUSIAN, "50");
                    put(Language.VIETNAMESE, "51");
                    put(Language.PERSIAN, "52");
                    put(Language.CATALAN, "53");
                    put(Language.INDONESIAN, "54");

                }
            });

}
