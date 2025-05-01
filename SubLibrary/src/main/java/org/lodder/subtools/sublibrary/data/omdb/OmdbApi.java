package org.lodder.subtools.sublibrary.data.omdb;

import static org.lodder.subtools.sublibrary.PageContentParams.*;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.omdb.exception.OmdbException;
import org.lodder.subtools.sublibrary.data.omdb.model.OmdbDetails;
import org.w3c.dom.Node;

@RequiredArgsConstructor
class OmdbApi {

    private static final String DOMAIN = "http://www.omdbapi.com";
    private final Manager manager;

    public Optional<OmdbDetails> getMovieDetails(int imdbId) throws OmdbException {
        return manager.getCache(CacheType.MEMORY, "OMDB-moviedetails-$imdbId")
            .getOptional(() -> {
                final String url = "$DOMAIN/?i=tt$%07d&plot=short&r=xml".formatted(imdbId);
                try {
                    return manager.getAsDocument(url(url))
                        .getElementsByTagName("movie").stream()
                        .map(this::parseOMDBDetails).findFirst();
                } catch (Exception e) {
                    throw new OmdbException("Error OMDB API", url, e);
                }
            });
    }

    private OmdbDetails parseOMDBDetails(Node node) {
        return new OmdbDetails(node.getAttribute("title"), Integer.parseInt(node.getAttribute("year")));
    }

}
