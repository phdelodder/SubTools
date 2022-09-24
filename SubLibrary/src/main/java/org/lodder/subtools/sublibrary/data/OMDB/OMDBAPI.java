package org.lodder.subtools.sublibrary.data.OMDB;

import java.util.Optional;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.XmlHTTP;
import org.lodder.subtools.sublibrary.data.OMDB.model.OMDBDetails;
import org.w3c.dom.Element;

public class OMDBAPI extends XmlHTTP {

    public OMDBAPI(Manager manager) {
        super(manager);
    }

    public Optional<OMDBDetails> getOMDBMovieDetails(String imdbId) throws OMDBException {
        return getManager().getValueBuilder()
                .key("OMDB-MovieDetails:" + imdbId)
                .cacheType(CacheType.DISK)
                .optionalValueSupplier(() -> {
                    final String url = "http://www.omdbapi.com/?i=" + imdbId + "&plot=short&r=xml";
                    try {
                        return getXML(url).cacheType(CacheType.NONE).getAsDocument()
                                .map(doc -> doc.getElementsByTagName("movie"))
                                .filter(nodeList -> nodeList.getLength() > 0)
                                .map(nodeList -> parseOMDBDetails((Element) nodeList.item(0)));
                    } catch (Exception e) {
                        throw new OMDBException("Error OMDBAPI", url, e);
                    }
                }).getOptional();
    }

    private OMDBDetails parseOMDBDetails(Element item) {
        return new OMDBDetails(item.getAttribute("title"), Integer.parseInt(item.getAttribute("year")));
    }

}
