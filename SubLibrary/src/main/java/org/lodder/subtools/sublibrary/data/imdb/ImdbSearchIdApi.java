package org.lodder.subtools.sublibrary.data.imdb;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.UserInteractionHandler;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbSearchIdException;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
class ImdbSearchIdApi {

    private static String DEFAULT_USER_AGENT = "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)";
    private static final Logger LOGGER = LoggerFactory.getLogger(ImdbSearchIdApi.class);
    private static final Pattern IMDB_URL_ID_PATTERN = Pattern.compile("\\/title\\/tt([0-9]*?)\\/.*");
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

    public ImdbSearchIdApi(Manager manager, UserInteractionHandler userInteractionHandler) {
        this.manager = manager;
        this.userInteractionHandler = userInteractionHandler;
    }

    public OptionalInt getImdbId(String title, int year) throws ImdbSearchIdException {
        return getImdbIdOnImdb(title, year)
                .orElseMap(() -> getImdbIdOnGoogle(title, year))
                .orElseMap(() -> getImdbIdOnYahoo(title, year))
                .orElseMap(() -> promtUserToEnterImdbId(title, year));
    }

    private OptionalInt promtUserToEnterImdbId(String title, int year) {
        return userInteractionHandler.enter("IMDB", Messages.getString("Prompter.EnterImdbMatchForSerie").formatted(title),
                Messages.getString("Prompter.ValueIsNotValid"), StringUtils::isNumeric).mapToInt(Integer::parseInt);
    }

    private OptionalInt getImdbIdOnImdb(String title, int year) throws ImdbSearchIdException {
        return manager.getValueBuilder()
                .key("IMDB-imdb-imdb-%s-%s".formatted(title, year))
                .cacheType(CacheType.DISK)
                .optionalIntSupplier(() -> {
                    StringBuilder sb = new StringBuilder("https://www.imdb.com/find?q=");
                    sb.append(URLEncoder.encode(title, StandardCharsets.UTF_8));
                    if (year > 0) {
                        sb.append("+%28").append(year).append("%29");
                    }
                    String url = sb.toString();
                    try {
                        Elements searchResults = manager.getPageContentBuilder()
                                .url(url)
                                .userAgent(DEFAULT_USER_AGENT)
                                .cacheType(CacheType.NONE)
                                .getAsJsoupDocument()
                                .select(".ipc-metadata-list li.ipc-metadata-list-summary-item a.ipc-metadata-list-summary-item__t");
                        return getImdbIdCommon(searchResults, title, year, Element::text, e -> e.attr("href"));
                    } catch (Exception e) {
                        throw new ImdbSearchIdException("Error getImdbIdOnImdb", url, e);
                    }
                }).getOptionalInt();
    }

    private OptionalInt getImdbIdOnYahoo(String title, int year) throws ImdbSearchIdException {
        return manager.getValueBuilder()
                .key("IMDB-imdb-yahoo-%s-%s".formatted(title, year))
                .cacheType(CacheType.DISK)
                .optionalIntSupplier(() -> {

                    StringBuilder sb = new StringBuilder("http://search.yahoo.com/search;_ylt=A1f4cfvx9C1I1qQAACVjAQx.?p=");
                    sb.append(URLEncoder.encode(title, StandardCharsets.UTF_8));

                    if (year > 0) {
                        sb.append("+%28").append(year).append("%29");
                    }

                    sb.append("+site%3Aimdb.com&fr=yfp-t-501&ei=UTF-8&rd=r1");
                    String url = sb.toString();

                    try {
                        Elements searchResults = manager.getPageContentBuilder()
                                .url(url)
                                .userAgent(DEFAULT_USER_AGENT)
                                .cacheType(CacheType.NONE)
                                .getAsJsoupDocument()
                                .select("a[href~='https%3a%2f%2fwww.imdb.com%2ftitle%2ftt'");
                        Function<Element, String> toStringMapper = e -> e.selectFirst("h3").text().replace(" - IMDb", "");
                        Function<Element, String> toHrefMapper = e -> URLDecoder.decode(e.attr("href"), StandardCharsets.UTF_8);
                        return getImdbIdCommon(searchResults, title, year, toStringMapper, toHrefMapper);
                    } catch (Exception e) {
                        throw new ImdbSearchIdException("Error getImdbIdOnImdb", url, e);
                    }
                }).getOptionalInt();

    }

    private OptionalInt getImdbIdOnGoogle(String title, int year) throws ImdbSearchIdException {
        return manager.getValueBuilder()
                .key("IMDB-imdb-google-%s-%s".formatted(title, year))
                .cacheType(CacheType.DISK)
                .optionalIntSupplier(() -> {

                    StringBuilder sb = new StringBuilder("http://www.google.com/search?q=");
                    sb.append(URLEncoder.encode(title, StandardCharsets.UTF_8));
                    if (year > 0) {
                        sb.append("+%28").append(year).append("%29");
                    }
                    sb.append("+site%3Awww.imdb.com&meta=");
                    String url = sb.toString();
                    try {
                        Elements searchResults = manager.getPageContentBuilder()
                                .url(url)
                                .userAgent(DEFAULT_USER_AGENT)
                                .cacheType(CacheType.NONE)
                                .getAsJsoupDocument()
                                .select("a[href^='https://www.imdb.com/title/tt'");
                        Function<Element, String> toStringMapper = e -> e.selectFirst("h3").text().replace(" - IMDb", "");
                        Function<Element, String> toHrefMapper = e -> e.attr("href");
                        return getImdbIdCommon(searchResults, title, year, toStringMapper, toHrefMapper);
                    } catch (Exception e) {
                        throw new ImdbSearchIdException("Error getImdbIdOnImdb", url, e);
                    }
                }).getOptionalInt();
    }

    private OptionalInt getImdbIdCommon(Elements searchResults, String title, int year, Function<Element, String> toStringMapper,
            Function<Element, String> toHrefMapper) {
        List<Element> matchingElements = searchResults
                .stream().filter(elem -> elem.text().replaceAll("[^A-Za-z]", "").equalsIgnoreCase(title.replaceAll("[^A-Za-z]", "")))
                .toList();
        if (!userInteractionHandler.getSettings().isOptionsConfirmProviderMapping() && matchingElements.size() == 1) {
            // found single exact match
            String href = toHrefMapper.apply(matchingElements.get(0));
            Matcher matcher = IMDB_URL_ID_PATTERN.matcher(href);
            return matcher.find() ? OptionalInt.of(Integer.parseInt(matcher.group())) : OptionalInt.empty();
        }
        String formattedTitle = title.replaceAll("[^A-Za-z]", "");
        return userInteractionHandler
                .selectFromList(
                        searchResults.stream()
                                .sorted(Comparator.comparing(
                                        e -> formattedTitle.equalsIgnoreCase(toStringMapper.apply(e).replaceAll("[^A-Za-z]", "")),
                                        Comparator.reverseOrder()))
                                .toList(),
                        Messages.getString("SelectImdbMatchForSerie").formatted(title),
                        "IMDB",
                        toStringMapper)
                .mapToOptionalObj(e -> {
                    Matcher matcher = IMDB_URL_ID_PATTERN.matcher(toHrefMapper.apply(e));
                    return matcher.matches() ? Optional.of(Integer.parseInt(matcher.group(1))) : Optional.empty();
                }).mapToInt(i -> i);
    }

}
