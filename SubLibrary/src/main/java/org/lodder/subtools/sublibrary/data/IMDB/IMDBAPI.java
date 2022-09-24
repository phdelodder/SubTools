package org.lodder.subtools.sublibrary.data.IMDB;

import java.net.URL;
import java.util.Optional;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.XmlHTTP;
import org.lodder.subtools.sublibrary.data.IMDB.model.IMDBDetails;
import org.w3c.dom.Element;

public class IMDBAPI extends XmlHTTP {

    public IMDBAPI(Manager manager) {
        super(manager);
    }

    public Optional<IMDBDetails> getIMDBMovieDetails(String imdbId) throws IMDBException {
        final String xml = "http://www.imdbapi.com/?i=" + imdbId + "&r=xml";
        return getManager().getValueBuilder()
                .key("IMDB-MovieDetails:" + imdbId)
                .cacheType(CacheType.DISK)
                .optionalSupplier(() -> {
                    try {
                        return getXML(xml).cacheType(CacheType.NONE).getAsDocument()
                                .map(doc -> doc.getElementsByTagName("movie"))
                                .filter(nodeList -> nodeList.getLength() > 0)
                                .map(nodeList -> parseIMDBDetails((Element) nodeList.item(0)));
                    } catch (Exception e) {
                        throw new IMDBException("Error IMDBAPI", xml, e);
                    }
                }).getOptional();
    }

    private IMDBDetails parseIMDBDetails(Element e) {
        IMDBDetails details = new IMDBDetails();
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
