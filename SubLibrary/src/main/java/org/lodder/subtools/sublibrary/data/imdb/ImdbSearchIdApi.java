package org.lodder.subtools.sublibrary.data.imdb;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbSearchIdException;
import org.lodder.subtools.sublibrary.util.OptionalExtension;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

record ImdbSearchIdApi(Manager manager) {

    private static final Pattern IMDB_URL_ID_PATTERN = Pattern.compile("/title/tt([0-9]*)");

    public Set<ProviderSerieId> getImdbIdOnImdb(String title, Integer year) throws ImdbSearchIdException {
        return manager.valueBuilder()
                .memoryCache()
                .key("%s-imdbid-imdb-%s-%s".formatted("IMDB", title, year))
                .collectionSupplier(ProviderSerieId.class, () -> {

                    StringBuilder sb = new StringBuilder("https://www.imdb.com/find?q=");
                    sb.append(URLEncoder.encode(title, StandardCharsets.UTF_8));
                    if (year != null) {
                        sb.append("+%28").append(year).append("%29");
                    }
                    String url = sb.toString();
                    try {
                        Elements searchResults = manager.getPageContentBuilder()
                                .url(url)
                                .getAsJsoupDocument()
                                .select("#main .findList .findResult .result_text");
                        return getImdbIdCommon(searchResults, title, year, e -> e.selectFirst("a").text() + " " + e.text(),
                                e -> e.selectFirst("a").attr("href"));
                    } catch (Exception e) {
                        throw new ImdbSearchIdException("Error getImdbIdOnImdb", url, e);
                    }
                }).getCollection();
    }

    public Set<ProviderSerieId> getImdbIdOnYahoo(String title, Integer year) throws ImdbSearchIdException {
        return manager.valueBuilder()
                .memoryCache()
                .key("%s-imdbid-yahoo-%s-%s".formatted("IMDB", title, year))
                .collectionSupplier(ProviderSerieId.class, () -> {
                    StringBuilder sb = new StringBuilder("http://search.yahoo.com/search;_ylt=A1f4cfvx9C1I1qQAACVjAQx.?p=");
                    sb.append(URLEncoder.encode(title, StandardCharsets.UTF_8));
                    if (year != null) {
                        sb.append("+%28").append(year).append("%29");
                    }

                    sb.append("+site%3Aimdb.com&fr=yfp-t-501&ei=UTF-8&rd=r1");
                    String url = sb.toString();

                    try {
                        Elements searchResults = manager.getPageContentBuilder()
                                .url(url)
                                .getAsJsoupDocument()
                                .select("a[href~='https%3a%2f%2fwww.imdb.com%2ftitle%2ftt']");
                        Function<Element, String> toStringMapper =
                                e -> Optional.ofNullable(e.selectFirst("h3")).map(e2 -> e2.text().replace(" - IMDb", "")).orElse(null);
                        Function<Element, String> toHrefMapper = e -> URLDecoder.decode(e.attr("href"), StandardCharsets.UTF_8);
                        return getImdbIdCommon(searchResults, title, year, toStringMapper, toHrefMapper);
                    } catch (Exception e) {
                        throw new ImdbSearchIdException("Error getImdbIdOnYahoo", url, e);
                    }
                }).getCollection();

    }

    public Set<ProviderSerieId> getImdbIdOnGoogle(String title, Integer year) throws ImdbSearchIdException {
        return manager.valueBuilder()
                .memoryCache()
                .key("%s-imdbid-google-%s-%s".formatted("IMDB", title, year))
                .collectionSupplier(ProviderSerieId.class, () -> {
                    StringBuilder sb = new StringBuilder("http://www.google.com/search?q=");
                    sb.append(URLEncoder.encode(title, StandardCharsets.UTF_8));
                    if (year != null) {
                        sb.append("+%28").append(year).append("%29");
                    }
                    sb.append("+site%3Awww.imdb.com&meta=");
                    String url = sb.toString();
                    try {
                        Elements searchResults = manager.getPageContentBuilder()
                                .url(url)
                                .getAsJsoupDocument()
                                .select("a[href*='https://www.imdb.com/title/tt']");
                        Function<Element, String> toStringMapper =
                                e -> Optional.ofNullable(e.selectFirst("span")).map(e2 -> e2.text().replace(" - IMDb", "")).orElse(null);
                        Function<Element, String> toHrefMapper = e -> e.attr("href");
                        return getImdbIdCommon(searchResults, title, year, toStringMapper, toHrefMapper);
                    } catch (Exception e) {
                        throw new ImdbSearchIdException("Error getImdbIdOnGoogle", url, e);
                    }
                }).getCollection();
    }

    private Set<ProviderSerieId> getImdbIdCommon(Elements searchResults, String title, int year, Function<Element, String> toStringMapper,
            Function<Element, String> toHrefMapper) {
        return searchResults.stream().map(element -> {
            String name = toStringMapper.apply(element);
            if (name == null) {
                return null;
            }
            String href = toHrefMapper.apply(element);
            Matcher matcher = IMDB_URL_ID_PATTERN.matcher(href);
            return matcher.find() ? new ProviderSerieId(name, matcher.group().replace("/title/tt", "")) : null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
