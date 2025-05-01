package org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi;

import static manifold.science.measures.TimeUnit.*;
import static org.lodder.subtools.sublibrary.PageContentParams.*;

import java.io.Serial;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.exception.PodnapisiException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.model.PodnapisiSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.Retry;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;

@RequiredArgsConstructor
public class JPodnapisiApi implements SubtitleApi {

    private static final String DOMAIN = "https://www.podnapisi.net";
    private final Manager manager;
    private final String userAgent;
    @val @override SubtitleSource subtitleSource = SubtitleSource.PODNAPISI;

    public Optional<ProviderSerieId> getPodnapisiShowName(String showName) throws PodnapisiException {
        String url = DOMAIN + "/sl/ppodnapisi/search?sK=" + showName.trim().toLowerCase().urlEncode();
        return getXml(url).selectFirstByClass("subtitle-entry") != null ?
            Optional.of(new ProviderSerieId(showName, showName)) : Optional.empty();
    }

    public List<PodnapisiSubtitleDescriptor> getMovieSubtitles(String movieName, @Nullable Integer year, int season,
        int episode, Language language) throws PodnapisiException {
        return getSubtitles(new SerieMapping(movieName, movieName, movieName, season), year, season, episode, language);

    }

    public List<PodnapisiSubtitleDescriptor> getSerieSubtitles(SerieMapping providerSerieId, int season, int episode,
        Language language) throws PodnapisiException {
        return getSubtitles(providerSerieId, null, season, episode, language);

    }

    private List<PodnapisiSubtitleDescriptor> getSubtitles(SerieMapping providerSerieId, @Nullable Integer year,
        int season, int episode, Language language) throws PodnapisiException {
        return manager.getCache(CacheType.MEMORY,
                "%s-subtitles-%s-%s-%s-%s".formatted(subtitleSource.name(), providerSerieId.providerId, season, episode,
                    language))
            .getCollection(() -> {
                try {
                    StringBuilder url = new StringBuilder("$DOMAIN/sl/ppodnapisi/search?sK=").append(
                        URLEncoder.encode(providerSerieId.providerId.trim().toLowerCase(), StandardCharsets.UTF_8));
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

                    return getXml(url.toString()).selectAllByTag("subtitle")
                        .stream()
                        .map(this::parsePodnapisiSubtitle)
                        .toList();
                } catch (Exception e) {
                    throw new PodnapisiException(e);
                }
            }, new Retry(1, ex -> ex instanceof HttpClientException e && e.responseCode >= 500, 1 Second));
    }


    protected @Nullable Document getXml(String url) throws PodnapisiException {
        try {
            return manager.getAsJsoupDocument(params(url, CacheType.MEMORY, userAgent,
                new Retry(
                    1,
                    ex -> ex instanceof HttpClientException e && e.responseCode >= 500 &&
                        e.responseCode < 600,
                    5 Second)));
        } catch (Exception e) {
            throw new PodnapisiException(e);
        }
    }

    private PodnapisiSubtitleDescriptor parsePodnapisiSubtitle(Element elem) {
        Function<Element, String> getText = e -> e == null ? null : e.text();
        return PodnapisiSubtitleDescriptor.builder()
            .hearingImpaired(elem.select("new_flags flags")
                .stream()
                .anyMatch(flagElem -> "hearing_impaired".equals(flagElem.text())))
            .language(languageIdToLanguage(elem.selectFirst("languageId").text()))
            .releaseString(elem.selectFirst("release").text().length() > 10 ? elem.selectFirst("release").text() :
                elem.selectFirst("title").text().replace(":", "") + " " + elem.selectFirst("release").text())
            .uploaderName(elem.selectFirst("uploaderName").text())
            .url(elem.selectFirst("url").text() + "/download?")
            .subtitleId(elem.selectFirst("id").text())
            .year(getText.apply(elem.selectFirst("year")))
            .imdb(getText.apply(elem.selectFirst("imdb")))
            .omdb(getText.apply(elem.selectFirst("omdb")))
            .build();
    }

    private Language languageIdToLanguage(String languageId) {
        return PODNAPISI_LANGS.entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(languageId))
            .map(Entry::getKey)
            .findFirst()
            .orElse(null);
    }

    private static final Map<Language, String> PODNAPISI_LANGS =
        Collections.unmodifiableMap(new EnumMap<>(Language.class) {
            @Serial private static final long serialVersionUID = 2950169212654074275L;

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
