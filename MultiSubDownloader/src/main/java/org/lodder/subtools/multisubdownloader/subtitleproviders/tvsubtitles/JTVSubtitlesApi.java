package org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.exception.TvSubtiltesException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model.TVsubtitlesSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.StreamExtension;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class, StreamExtension.class })
public class JTVSubtitlesApi extends Html implements SubtitleApi {

    private static final String DOMAIN = "https://www.tvsubtitles.net";
    private static final String SERIE_URL_PREFIX = DOMAIN + "/";

    public JTVSubtitlesApi(Manager manager) {
        super(manager);
    }

    public List<ProviderSerieId> getUrisForSerieName(String serieName) throws TvSubtiltesException {
        try {
            Document searchShowDoc = Jsoup.parse(postHtml(DOMAIN + "/search.php", Map.of("qs", serieName)));
            return searchShowDoc.select(".left_articles > ul > li a").stream()
                    .map(element -> new ProviderSerieId(element.text(), StringUtils.substringAfterLast(element.attr("href"), "/"))).toList();
        } catch (Exception e) {
            throw new TvSubtiltesException(e);
        }
    }

    public Set<TVsubtitlesSubtitleDescriptor> getSubtitles(SerieMapping providerSerieId, int season, int episode, Language language)
            throws TvSubtiltesException {
        return getEpisodeUrl(SERIE_URL_PREFIX + providerSerieId.getProviderId(), season, episode)
                .mapToObj(episodeUrl -> getSubtitles(episodeUrl, language))
                .orElseGet(Set::of);
    }

    private Set<TVsubtitlesSubtitleDescriptor> getSubtitles(String episodeUrl, Language language) throws TvSubtiltesException {
        return getManager().valueBuilder()
                .memoryCache()
                .key("%s-subtitles-%s-%s".formatted(getSubtitleSource().name(), episodeUrl, language))
                .collectionSupplier(TVsubtitlesSubtitleDescriptor.class, () -> {
                    Set<TVsubtitlesSubtitleDescriptor> lSubtitles = new HashSet<>();
                    try {
                        Document searchEpisodeDoc =
                                this.getHtml(episodeUrl.replace(".html", "-" + language.getLangCode() + ".html")).cacheType(CacheType.NONE)
                                        .getAsJsoupDocument();
                        Elements searchEpisodes = searchEpisodeDoc.select(".left_articles > a");

                        BiPredicate<Elements, String> isRowWithText = (row, text) -> row.get(1).text().contains(text);
                        Function<Elements, String> getRowValue = row -> row.get(2).text();
                        for (Element ep : searchEpisodes) {
                            String url = ep.attr("href");
                            if (url.contains("subtitle-")) {
                                Document subtitlePageDoc = this.getHtml(DOMAIN + url).cacheType(CacheType.NONE).getAsJsoupDocument();
                                String filename = null, rip = null, title = null, author = null;
                                Elements subtitlePageTableDoc = subtitlePageDoc.getElementsByClass("subtitle1");
                                if (subtitlePageTableDoc.size() == 1) {
                                    for (Element item : subtitlePageTableDoc.get(0).getElementsByTag("tr")) {
                                        Elements row = item.getElementsByTag("td");
                                        if (row.size() != 3) {
                                            continue;
                                        }
                                        if (isRowWithText.test(row, "episode title:")) {
                                            title = getRowValue.apply(row);
                                        } else if (isRowWithText.test(row, "filename:")) {
                                            filename = getRowValue.apply(row);
                                        } else if (isRowWithText.test(row, "rip:")) {
                                            rip = getRowValue.apply(row);
                                        } else if (isRowWithText.test(row, "author:")) {
                                            author = getRowValue.apply(row);
                                        }
                                        if (filename != null && rip != null) {
                                            TVsubtitlesSubtitleDescriptor sub = TVsubtitlesSubtitleDescriptor.builder()
                                                    .filename(filename)
                                                    .url(DOMAIN + "/files/" + URLEncoder.encode(
                                                            filename.replace(title + ".", "").replace(".srt", ".zip").replace(" - ", "_"),
                                                            StandardCharsets.UTF_8))
                                                    .rip(rip)
                                                    .author(author)
                                                    .build();
                                            lSubtitles.add(sub);
                                            rip = null;
                                            filename = null;
                                            title = null;
                                            author = null;
                                        }
                                    }
                                }
                            }
                        }
                        return lSubtitles;
                    } catch (Exception e) {
                        throw new TvSubtiltesException(e);
                    }
                })
                .getCollection();
    }

    private Optional<String> getEpisodeUrl(String showUrl, int season, int episode) throws TvSubtiltesException {
        return getManager().valueBuilder()
                .memoryCache()
                .key("%s-episodeUrl-%s-%s-%s".formatted(getSubtitleSource().name(), showUrl, season, episode))
                .optionalSupplier(() -> {
                    try {
                        String formatedSeasonEpisode = season + "x" + (episode < 10 ? "0" + episode : "" + episode);
                        return getHtml(showUrl.replace(".html", "-" + season + ".html"))
                                .getAsJsoupDocument()
                                .getElementById("table5").getElementsByTag("tr").stream().skip(1)
                                .filter(row -> Optional.ofNullable(row.selectFirst("td"))
                                        .map(element -> formatedSeasonEpisode.equals(element.text()))
                                        .orElse(false))
                                .map(element -> DOMAIN + "/" + element.select("td").get(1).selectFirst("a").attr("href")).findAny();
                    } catch (Exception e) {
                        throw new TvSubtiltesException(e);
                    }
                }).getOptional();
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.TVSUBTITLES;
    }
}
