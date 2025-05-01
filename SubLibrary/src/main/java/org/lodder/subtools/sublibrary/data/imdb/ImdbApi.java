package org.lodder.subtools.sublibrary.data.imdb;

import static org.lodder.subtools.sublibrary.PageContentParams.*;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbException;
import org.lodder.subtools.sublibrary.data.imdb.model.ImdbDetails;

@RequiredArgsConstructor
public class ImdbApi {

    private static final String DOMAIN = "https://www.imdb.com";
    private final Manager manager;

    public Optional<ImdbDetails> getMovieDetails(int imdbId) throws ImdbException {
        return manager.getCache(CacheType.MEMORY, "IMDB-moviedetails-$imdbId")
            .getOptional(() -> {
                final String url = "$DOMAIN/title/tt${%07d/releaseinfo".formatted(imdbId);
                try {
                    org.jsoup.nodes.Element element = manager.getAsJsoupDocument(url(url))
                        .selectFirstByCss(".article .subpage_title_block .subpage_title_block__right-column");
                    String imdbName = element.selectFirstByCss("a[itemprop='url']").text();
                    int year = Integer.parseInt(
                        element.selectFirstByCss("span.nobr").text().replaceAll("[^0-9]", ""));
                    return Optional.of(new ImdbDetails(imdbName, year));
                } catch (Exception e) {
                    throw new ImdbException("Error IMDB API", url, e);
                }
            });
    }
}
