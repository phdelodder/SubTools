package org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.util.OptionalExtension;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class JTVSubtitlesApi extends Html implements SubtitleApi {

    private static final String DOMAIN = "https://www.tvsubtitles.net";

    public JTVSubtitlesApi(Manager manager) {
        super(manager);
    }

    public Set<TVsubtitlesSubtitleDescriptor> searchSubtitles(String name, int season, int episode, Language language)
            throws TvSubtiltesException {
        return getValue("%s-subtitles-%s-%s-%s-%s".formatted(getSubtitleSource().name(), name.toLowerCase(), season, episode, language))
                .cacheType(CacheType.MEMORY)
                .collectionSupplier(TVsubtitlesSubtitleDescriptor.class, () -> {
                    Set<TVsubtitlesSubtitleDescriptor> lSubtitles = new HashSet<>();
                    try {
                        Optional<String> episodeUrl = getShowUrl(name).mapToOptionalObj(showUrl -> getEpisodeUrl(showUrl, season, episode));
                        if (episodeUrl.isEmpty()) {
                            return Set.of();
                        }
                        String episodeUrl2 =
                                DOMAIN + episodeUrl.get().substring(0, episodeUrl.get().indexOf(".")) + "-" + language.getLangCode() + ".html";
                        Document searchEpisodeDoc = this.getHtml(episodeUrl2).cacheType(CacheType.NONE).getAsJsoupDocument();
                        Elements searchEpisodes = searchEpisodeDoc.getElementsByClass("left_articles").get(0).getElementsByTag("a");
                        for (Element ep : searchEpisodes) {
                            String url = ep.attr("href");
                            if (url.contains("subtitle-")) {
                                Document subtitlePageDoc = this.getHtml(DOMAIN + url).cacheType(CacheType.NONE).getAsJsoupDocument();
                                String filename = null, rip = null, title = null, author = null;
                                Elements subtitlePageTableDoc = subtitlePageDoc.getElementsByClass("subtitle1");
                                if (subtitlePageTableDoc.size() == 1) {
                                    for (Element item : subtitlePageTableDoc.get(0).getElementsByTag("tr")) {
                                        Elements row = item.getElementsByTag("td");
                                        if (row.size() == 3 && row.get(1).text().contains("episode title:")) {
                                            title = row.get(2).text();
                                        }
                                        if (row.size() == 3 && row.get(1).text().contains("filename:")) {
                                            filename = row.get(2).text();
                                        }
                                        if (row.size() == 3 && row.get(1).text().contains("rip:")) {
                                            rip = row.get(2).text();
                                        }
                                        if (row.size() == 3 && row.get(1).text().contains("author:")) {
                                            author = row.get(2).text();
                                        }

                                        if (filename != null && rip != null) {
                                            TVsubtitlesSubtitleDescriptor sub = new TVsubtitlesSubtitleDescriptor();
                                            sub.setFilename(filename);
                                            sub.setUrl(DOMAIN + "/files/" + URLEncoder.encode(
                                                    filename.replace(title + ".", "").replace(".srt", ".zip").replace(" - ", "_"),
                                                    StandardCharsets.UTF_8));
                                            sub.setRip(rip);
                                            sub.setAuthor(author);
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
                }).getCollection();
    }

    private Optional<String> getEpisodeUrl(String showUrl, int season, int episode) throws TvSubtiltesException {
        try {
            String seasonUrl = DOMAIN + showUrl.substring(0, showUrl.indexOf(".")) + "-" + season + ".html";

            Element searchSeasonTable = this.getHtml(seasonUrl).cacheType(CacheType.MEMORY).getAsJsoupDocument().getElementById("table5");
            String episodeUrl = null;

            boolean foundEp = false;
            for (Element ep : searchSeasonTable.getElementsByTag("td")) {
                if (foundEp) {
                    Elements links = ep.getElementsByTag("a");
                    if (links.size() == 1) {
                        episodeUrl = "/" + links.get(0).attr("href");
                        break;
                    }
                }
                String formatedepisodenumber = "";
                if (episode < 10) {
                    formatedepisodenumber = "0" + episode;
                } else {
                    formatedepisodenumber = "" + episode;
                }
                if ((season + "x" + formatedepisodenumber).equals(ep.text())) {
                    foundEp = true;
                }
            }
            return Optional.ofNullable(episodeUrl);
        } catch (Exception e) {
            throw new TvSubtiltesException(e);
        }
    }

    private Optional<String> getShowUrl(String showName) throws TvSubtiltesException {
        return getValue("%s-ShowName-".formatted(getSubtitleSource().name(), showName.toLowerCase()))
                .cacheType(CacheType.DISK)
                .optionalValueSupplier(() -> {
                    try {
                        Map<String, String> data = new HashMap<>();
                        data.put("qs", showName);

                        Document searchShowDoc = Jsoup.parse(postHtml(DOMAIN + "/search.php", data));
                        if (searchShowDoc == null) {
                            return Optional.empty();
                        }

                        return searchShowDoc.getElementsByTag("li").stream()
                                .map(show -> show.getElementsByTag("a"))
                                .filter(links -> links.size() == 1 && links.get(0).text().toLowerCase().contains(showName.toLowerCase()))
                                .map(links -> links.get(0).attr("href"))
                                .findFirst();
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
