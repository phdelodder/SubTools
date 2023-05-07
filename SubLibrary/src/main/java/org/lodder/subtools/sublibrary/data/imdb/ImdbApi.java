package org.lodder.subtools.sublibrary.data.imdb;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbException;
import org.lodder.subtools.sublibrary.data.imdb.model.ImdbDetails;

import lombok.Getter;

@RequiredArgsConstructor
public class ImdbApi {

    private static final String DOMAIN = "https://www.imdb.com";
    private final Manager manager;

    public Optional<ImdbDetails> getMovieDetails(int imdbId) throws ImdbException {
        return manager.valueBuilder()
                .memoryCache()
                .key("%s-moviedetails-%s".formatted("IMDB", imdbId))
                .optionalSupplier(() -> {
                    final String url = "%s/title/tt%s/releaseinfo".formatted(DOMAIN, StringUtils.leftPad(String.valueOf(imdbId), 7, "0"));
                    try {
                        org.jsoup.nodes.Element element = manager.getPageContentBuilder()
                                .url(url)
                                .getAsJsoupDocument()
                                .selectFirst(".article .subpage_title_block .subpage_title_block__right-column");
                        String imdbName = element.selectFirst("a[itemprop='url']").text();
                        int year = Integer.parseInt(element.selectFirst("span.nobr").text().replaceAll("[^0-9]", ""));
                        return Optional.of(new ImdbDetails(imdbName, year));
                    } catch (Exception e) {
                        throw new ImdbException("Error IMDBAPI", url, e);
                    }
                }).getOptional();
    }
}
