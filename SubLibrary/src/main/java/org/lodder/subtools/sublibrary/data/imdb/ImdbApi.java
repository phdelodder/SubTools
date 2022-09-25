package org.lodder.subtools.sublibrary.data.imdb;

import java.net.URL;
import java.util.Optional;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.XmlHTTP;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbException;
import org.lodder.subtools.sublibrary.data.imdb.model.ImdbDetails;
import org.w3c.dom.Element;

public class ImdbApi extends XmlHTTP {

    public ImdbApi(Manager manager) {
        super(manager);
    }

    public Optional<ImdbDetails> getMovieDetails(String imdbId) throws ImdbException {
        final String xml = "http://www.imdbapi.com/?i=" + imdbId + "&r=xml";
        return getManager().getValueBuilder()
                .key("IMDB-MovieDetails:" + imdbId)
                .cacheType(CacheType.DISK)
                .optionalSupplier(() -> {
                    try {
                        return getXML(xml).cacheType(CacheType.NONE).getAsDocument()
                                .map(doc -> doc.getElementsByTagName("movie"))
                                .filter(nodeList -> nodeList.getLength() > 0)
                                .map(nodeList -> parseImdbDetails((Element) nodeList.item(0)));
                    } catch (Exception e) {
                        throw new ImdbException("Error IMDBAPI", xml, e);
                    }
                }).getOptional();
    }

    private ImdbDetails parseImdbDetails(Element e) {
        ImdbDetails details = new ImdbDetails();
        details.setTitle(e.getAttribute("title"));
        details.setYear(Integer.parseInt(e.getAttribute("year")));
        details.setActors(e.getAttribute("actors"));
        details.setDirector(e.getAttribute("director"));
        details.setGenre(e.getAttribute("genre"));
        details.setId(e.getAttribute("id"));
        details.setPlot(e.getAttribute("plot"));
        details.setRated(e.getAttribute("rated"));
        details.setWriter(e.getAttribute("writer"));
        details.setReleased(e.getAttribute("released"));
        details.setRuntime(e.getAttribute("runtime"));
        details.setRating(e.getAttribute("rating"));
        details.setVotes(e.getAttribute("votes"));
        try {
            details.setPoster(new URL(e.getAttribute("poster")));
        } catch (Exception ex) {
            // do nothing
        }
        return details;
    }
}
