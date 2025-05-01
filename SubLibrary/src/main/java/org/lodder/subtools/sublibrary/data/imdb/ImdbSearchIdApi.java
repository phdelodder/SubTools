package org.lodder.subtools.sublibrary.data.imdb;

import static org.lodder.subtools.sublibrary.PageContentParams.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbSearchIdException;
import util.Utils;

record ImdbSearchIdApi(Manager manager) {

    private static final Pattern IMDB_URL_ID_PATTERN = Pattern.compile("/title/tt(\\d*)");

    public Set<ProviderSerieId> getImdbIdOnImdb(String title, @Nullable Integer year) throws ImdbSearchIdException {
        return manager.getCache(CacheType.MEMORY, "IMDB-imdbid-imdb-$title-$year")
            .getCollection(() -> {
                StringBuilder sb = new StringBuilder("https://www.imdb.com/find?q=");
                sb.append(URLEncoder.encode(title, StandardCharsets.UTF_8));
                if (year != null) {
                    sb.append("+%28").append(year).append("%29");
                }
                String url = sb.toString();
                try {
                    Elements searchResults = manager.getAsJsoupDocument(url(url))
                        .selectAllByCss("#main .findList .findResult .result_text");
                    return getImdbIdCommon(searchResults,
                        e -> e.selectFirstByTag("a").text() + " " + e.text(),
                        e -> e.selectFirst("a").attr("href"));
                } catch (Exception e) {
                    throw new ImdbSearchIdException("Error getImdbIdOnImdb", url, e);
                }
            });
    }

    public Set<ProviderSerieId> getImdbIdOnYahoo(String title, @Nullable Integer year) throws ImdbSearchIdException {
        return manager.getCache(CacheType.MEMORY, "IMDB-imdbid-yahoo-$title-$year")
            .getCollection(() -> {
                StringBuilder sb =
                    new StringBuilder("http://search.yahoo.com/search;_ylt=A1f4cfvx9C1I1qQAACVjAQx.?p=");
                sb.append(URLEncoder.encode(title, StandardCharsets.UTF_8));
                if (year != null) {
                    sb.append("+%28").append(year).append("%29");
                }

                sb.append("+site%3Aimdb.com&fr=yfp-t-501&ei=UTF-8&rd=r1");
                String url = sb.toString();

                try {
                    Elements searchResults = manager.getAsJsoupDocument(url(url))
                        .selectAllByCss("a[href~='https%3a%2f%2fwww.imdb.com%2ftitle%2ftt']");
                    Function<Element, String> toStringMapper = e -> Optional.ofNullable(e.selectFirst("h3"))
                        .map(e2 -> e2.text().replace(" - IMDb", ""))
                        .orElse(null);
                    Function<Element, String> toHrefMapper =
                        e -> URLDecoder.decode(e.attr("href"), StandardCharsets.UTF_8);
                    return getImdbIdCommon(searchResults, toStringMapper, toHrefMapper);
                } catch (Exception e) {
                    throw new ImdbSearchIdException("Error getImdbIdOnYahoo", url, e);
                }
            });
    }

    public Set<ProviderSerieId> getImdbIdOnGoogle(String title, @Nullable Integer year) throws ImdbSearchIdException {
        return manager.getCache(CacheType.MEMORY, "IMDB-imdbid-google-$title-$year")
            .getCollection(() -> {
                StringBuilder sb = new StringBuilder("http://www.google.com/search?q=");
                sb.append(URLEncoder.encode(title, StandardCharsets.UTF_8));
                if (year != null) {
                    sb.append("+%28").append(year).append("%29");
                }
                sb.append("+site%3Awww.imdb.com&meta=");
                String url = sb.toString();
                try {
                    Elements searchResults = manager.getAsJsoupDocument(url(url))
                        .selectAllByCss("a[href*='https://www.imdb.com/title/tt']");
                    Function<Element, String> toStringMapper =
                        e -> e.selectFirstByTag("span").text().replace(" - IMDb", "");
                    Function<Element, String> toHrefMapper = e -> e.attr("href");
                    return getImdbIdCommon(searchResults, toStringMapper, toHrefMapper);
                } catch (Exception e) {
                    throw new ImdbSearchIdException("Error getImdbIdOnGoogle", url, e);
                }
            });
    }

    private Set<ProviderSerieId> getImdbIdCommon(Elements searchResults, Function<Element, String> toStringMapper,
        Function<Element, String> toHrefMapper) {
        return searchResults.stream().collect(Utils.setCollector(
            (set, element) -> {
                String name = toStringMapper.apply(element);
                if (StringUtils.isBlank(name)) {
                    return;
                }
                String href = toHrefMapper.apply(element);
                Matcher matcher = IMDB_URL_ID_PATTERN.matcher(href);
                if (matcher.find()) {
                    set.add(new ProviderSerieId(name, matcher.group().replace("/title/tt", "")));
                }
            }));
    }
}
