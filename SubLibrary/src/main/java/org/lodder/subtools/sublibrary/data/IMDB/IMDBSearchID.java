package org.lodder.subtools.sublibrary.data.IMDB;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.StringTokenizer;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class IMDBSearchID {

    private final Manager manager;
    private static String DEFAULTUSERAGENT = "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)";
    private static final Logger LOGGER = LoggerFactory.getLogger(IMDBSearchID.class);

    public IMDBSearchID(Manager manager) {
        this.manager = manager;
    }

    public OptionalInt getImdbId(String title, int year) throws IMDBSearchIDException {
        return this.getImdbIdOnGoogle(title, year).orElseMap(() -> this.getImdbIdOnYahoo(title, year));
    }

    public OptionalInt getImdbIdOnYahoo(String title, int year) throws IMDBSearchIDException {
        return manager.getValueBuilder()
                .key("IMDB-imdb-yahoo-%s-%s".formatted(title, year))
                .cacheType(CacheType.DISK)
                .optionalValueSupplier(() -> {

                    StringBuilder sb = new StringBuilder("http://search.yahoo.com/search;_ylt=A1f4cfvx9C1I1qQAACVjAQx.?p=");
                    sb.append(URLEncoder.encode(title, StandardCharsets.UTF_8));

                    if (year > 0) {
                        sb.append("+%28").append(year).append("%29");
                    }

                    sb.append("+site%3Aimdb.com&fr=yfp-t-501&ei=UTF-8&rd=r1");
                    String url = sb.toString();

                    try {
                        String xml = manager.getPageContentBuilder().url(url).userAgent(DEFAULTUSERAGENT).cacheType(CacheType.NONE).get();
                        int beginIndex = xml.indexOf("/title/tt");
                        StringTokenizer st = new StringTokenizer(xml.substring(beginIndex + 7), "/\"");
                        String imdbId = st.nextToken();

                        if (imdbId.startsWith("tt")) {
                            LOGGER.trace("Found imdbid [{}] with yahoo", imdbId);
                            return Optional.of(Integer.parseInt(imdbId.substring(2)));
                        }

                    } catch (Exception e) {
                        throw new IMDBSearchIDException("Error getImdbIdOnYahoo", url, e);
                    }
                    return Optional.empty();
                }).getOptional().mapToInt(i -> i);
    }

    public OptionalInt getImdbIdOnGoogle(String title, int year) throws IMDBSearchIDException {
        return manager.getValueBuilder()
                .key("IMDB-imdb-google-%s-%s".formatted(title, year))
                .cacheType(CacheType.DISK)
                .optionalValueSupplier(() -> {

                    StringBuilder sb = new StringBuilder("http://www.google.com/search?q=");
                    sb.append(URLEncoder.encode(title, StandardCharsets.UTF_8));

                    if (year > 0) {
                        sb.append("+%28").append(year).append("%29");
                    }

                    sb.append("+site%3Awww.imdb.com&meta=");
                    String url = sb.toString();

                    try {
                        String xml = manager.getPageContentBuilder().url(url).userAgent(DEFAULTUSERAGENT).cacheType(CacheType.NONE).get();
                        String imdbId = "";

                        int beginIndex = xml.indexOf("/title/tt");
                        if (beginIndex > -1) {
                            StringTokenizer st = new StringTokenizer(xml.substring(beginIndex + 7), "/\"");
                            imdbId = st.nextToken();
                        }

                        if (imdbId.startsWith("tt")) {
                            LOGGER.trace("Found imdbid [{}] with google", imdbId);
                            return Optional.of(Integer.parseInt(imdbId.substring(2)));
                        }

                    } catch (Exception e) {
                        throw new IMDBSearchIDException("Error getImdbIdOnGoogle", url, e);
                    }
                    return Optional.empty();
                }).getOptional().mapToInt(i -> i);
    }

}
