package org.lodder.subtools.sublibrary.data.omdb;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.XmlHTTP;
import org.lodder.subtools.sublibrary.data.omdb.exception.OmdbException;
import org.lodder.subtools.sublibrary.data.omdb.model.OmdbDetails;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.w3c.dom.Element;

class OmdbApi extends XmlHTTP {

    public OmdbApi(Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager);
    }

    public Optional<OmdbDetails> getMovieDetails(int imdbId) throws OmdbException {
        final String url = "http://www.omdbapi.com/?i=tt" + StringUtils.leftPad(String.valueOf(imdbId), 7, "0") + "&plot=short&r=xml";
        try {
            return getXML(url).cacheType(CacheType.NONE).getAsDocument()
                    .map(doc -> doc.getElementsByTagName("movie"))
                    .filter(nodeList -> nodeList.getLength() > 0)
                    .map(nodeList -> parseOMDBDetails((Element) nodeList.item(0)));
        } catch (Exception e) {
            throw new OmdbException("Error OMDBAPI", url, e);
        }
    }

    private OmdbDetails parseOMDBDetails(Element item) {
        return new OmdbDetails(item.getAttribute("title"), Integer.parseInt(item.getAttribute("year")));
    }

}
