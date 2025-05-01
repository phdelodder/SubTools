package org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.exception.TvSubtitlesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model.TVSubtitlesSubtitleDescriptor;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model.TVSubtitlesSubtitleDescriptor.TVSubtitlesSubtitleDescriptorBuilder;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.PageContentParams;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.control.VideoPatterns.Source;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.http.CookieManager;

public class JTVSubtitlesApi implements SubtitleApi {

    private static final String DOMAIN = "https://www.tvsubtitles.net";
    private static final String SERIE_URL_PREFIX = DOMAIN + "/";
    private final Manager manager;
    @val @override SubtitleSource subtitleSource = SubtitleSource.TVSUBTITLES;

    public JTVSubtitlesApi(Manager manager) {
        this.manager = manager;
    }

    public List<ProviderSerieId> getUrisForSerieName(String serieName) throws TvSubtitlesException {
        try {
            return manager.postBuilder("$DOMAIN/search.php")
                .addData("qs", serieName)
                .postAsJsoupDocument()
                .select(".left_articles > ul > li a")
                .stream()
                .map(element -> new ProviderSerieId(element.text(),
                    StringUtils.substringAfterLast(element.attr("href"), "/")))
                .toList();
        } catch (Exception e) {
            throw new TvSubtitlesException(e);
        }
    }

    public Set<TVSubtitlesSubtitleDescriptor> getSubtitles(SerieMapping providerSerieId, int season, int episode,
        Language language) throws TvSubtitlesException {
        // https://www.tvsubtitles.net/setlang.php?page=/tvshow-3219-2.html&setlang1=es
        Set<TVSubtitlesSubtitleDescriptor> results = new HashSet<>();
        Optional<EpisodeRow> episodeRow = getSeasonSubtitleInfo(providerSerieId.providerId, season, language).filter(
            row -> row.isSameEpisode(season, episode)).findAny();
        if (episodeRow.isPresent()) {
            for (String url : episodeRow.get().urls) {
                results.addAll(getSubtitles(url));
            }
            return results;
        }
        return results;
    }

    private List<EpisodeRow> getSeasonSubtitleInfo(String providerSerieId, int season, Language language)
        throws TvSubtitlesException {
        return manager.getCache(CacheType.MEMORY,
                subtitleSource.name() + "subtitleInfo-$providerSerieId-$season-$language")
            .getCollection(() -> {
                try {
                    String languageCode = getLanguageCode(language);
                    CookieManager cookieManager = null;
                    if (languageCode != null) {
                        cookieManager = new CookieManager().storeCookie("tvsubtitles.net", "setlang", languageCode);
                    }
//                    DOMAIN + "/setlang.php?page=/$providerSerieId-$season.html&setlang1=$languageCode",
                    return manager.getAsJsoupDocument(PageContentParams.params(
                            DOMAIN + "/" + providerSerieId.replace(".html", "-$season.html"),
                            cookieManager:cookieManager))
                        .select("#table4 table tr[bgcolor]")
                        .stream()
                        .filter(episodeRow -> StringUtils.isNotBlank(episodeRow.selectFirstByTag("td").text()))
                        .map(episodeRow -> {
                            Elements tds = episodeRow.selectAllByTag("td");
                            String[] seasonEpisode = tds.get(0).text().split("x");
                            List<String> urls =
                                tds.get(3).select("a").stream().map(elem -> DOMAIN + "/" + elem.attr("href")).toList();
                            return new EpisodeRow(Integer.parseInt(seasonEpisode[0]),
                                Integer.parseInt(seasonEpisode[1]), urls);
                        }).filter(episodeRow -> !episodeRow.urls.isEmpty()).toList();
                } catch (Exception e) {
                    throw new TvSubtitlesException(e);
                }
            });
    }

    private List<TVSubtitlesSubtitleDescriptor> getSubtitles(String episodeUrl)
        throws TvSubtitlesException {
        return manager.getCache(CacheType.MEMORY, subtitleSource.name() + "subtitles-$episodeUrl")
            .getCollection(() -> {
                try {
                    return manager.getAsJsoupDocument(PageContentParams.url(episodeUrl))
                        .select(".left_articles > div[class^='subtitle']")
                        .stream().map(subtitleElement -> {
                            TVSubtitlesSubtitleDescriptorBuilder subtitleBuilder =
                                TVSubtitlesSubtitleDescriptor.builder();
                            for (Element titleElement : subtitleElement.select(".subtitle_grid > div > img[title]")) {
                                String value =
                                    ((Element) titleElement.parent()).nextElementSibling().nextElementSibling().text();
                                switch (titleElement.attr("title")) {
                                    case "episode title" -> subtitleBuilder.title(value);
                                    case "rip" -> subtitleBuilder.source(Source.fromValue(value));
                                    case "release" -> subtitleBuilder.releaseGroup(value);
                                    case "filename" -> subtitleBuilder.filename(value);
                                    default -> {
                                    }
                                }
                            }
                            subtitleBuilder.url(DOMAIN + "/" + subtitleElement.select("a[href^='download-']")
                                .attr("href"));
                            return subtitleBuilder.build();
                        }).toList();
                } catch (ManagerException e) {
                    throw new TvSubtitlesException(e);
                }
            });
    }

    private record EpisodeRow(int season, int episode, List<String> urls) implements Serializable {
        public boolean isSameEpisode(int season, int episode) {
            return this.season == season && this.episode == episode;
        }
    }

    private String getLanguageCode(Language language) {
        return switch (language) {
            case ENGLISH -> "en";
            case SPANISH -> "es";
            case FRENCH -> "fr";
            case GERMAN -> "de";
            case RUSSIAN -> "ru";
            case UKRAINIAN -> "ua";
            case ITALIAN -> "it";
            case GREEK -> "gr";
            case ARABIC -> "ar";
            case HUNGARIAN -> "hu";
            case POLISH -> "pl";
            case TURKISH -> "tr";
            case DUTCH -> "nl";
            case PORTUGUESE -> "pt";
            case SWEDISH -> "sv";
            case DANISH -> "da";
            case FINNISH -> "fi";
            case KOREAN -> "ko";
            case CHINESE_SIMPLIFIED, CHINESE_TRADITIONAL -> "cn";
            case JAPANESE -> "jp";
            case BULGARIAN -> "bg";
            case CZECH -> "cz";
            case ROMANIAN -> "ro";
            default -> null;
        };
    }
}
