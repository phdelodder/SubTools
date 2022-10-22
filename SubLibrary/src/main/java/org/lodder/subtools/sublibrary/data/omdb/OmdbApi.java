package org.lodder.subtools.sublibrary.data.omdb;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.omdb.exception.OmdbException;
import org.lodder.subtools.sublibrary.data.omdb.model.OmdbDetails;
import org.w3c.dom.Element;

import lombok.Getter;

class OmdbApi {

    @Getter
    private final Manager manager;

    public OmdbApi(Manager manager) {
        this.manager = manager;
    }

    public Optional<OmdbDetails> getMovieDetails(int imdbId) throws OmdbException {
        return getManager().valueBuilder()
                .memoryCache()
                .key("%s-moviedetails-%s".formatted("OMDB", imdbId))
                .optionalSupplier(() -> {
                    final String url = "http://www.omdbapi.com/?i=tt" + StringUtils.leftPad(String.valueOf(imdbId), 7, "0") + "&plot=short&r=xml";
                    try {
                        return manager.getPageContentBuilder()
                                .url(url)
                                .getAsDocument()
                                .map(doc -> doc.getElementsByTagName("movie"))
                                .filter(nodeList -> nodeList.getLength() > 0)
                                .map(nodeList -> parseOMDBDetails((Element) nodeList.item(0)));
                    } catch (Exception e) {
                        throw new OmdbException("Error OMDBAPI", url, e);
                    }
                }).getOptional();
    }

    private OmdbDetails parseOMDBDetails(Element item) {
        return new OmdbDetails(item.getAttribute("title"), Integer.parseInt(item.getAttribute("year")));
    }

}
