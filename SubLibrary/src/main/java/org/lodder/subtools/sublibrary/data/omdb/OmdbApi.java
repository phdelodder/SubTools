package org.lodder.subtools.sublibrary.data.omdb;

import java.util.Optional;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.XmlHTTP;
import org.lodder.subtools.sublibrary.data.omdb.exception.OmdbException;
import org.lodder.subtools.sublibrary.data.omdb.model.OmdbDetails;
import org.w3c.dom.Element;

class OmdbApi extends XmlHTTP {

    public OmdbApi(Manager manager) {
        super(manager);
    }

    public Optional<OmdbDetails> getMovieDetails(String imdbId) throws OmdbException {
        return getManager().getValueBuilder()
                .key("OMDB-MovieDetails:" + imdbId)
                .cacheType(CacheType.DISK)
                .optionalSupplier(() -> {
                    final String url = "http://www.omdbapi.com/?i=" + imdbId + "&plot=short&r=xml";
                    try {
                        return getXML(url).cacheType(CacheType.NONE).getAsDocument()
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
