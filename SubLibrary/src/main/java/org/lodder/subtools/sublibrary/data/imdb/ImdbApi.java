package org.lodder.subtools.sublibrary.data.imdb;

import java.net.URL;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.imdb.exception.ImdbException;
import org.lodder.subtools.sublibrary.data.imdb.model.ImdbDetails;
import org.w3c.dom.Element;

import lombok.Getter;

public class ImdbApi {

    private static final String DOMAIN = "http://www.imdbapi.com";
    @Getter
    private final Manager manager;

    public ImdbApi(Manager manager) {
        this.manager = manager;
    }

    public Optional<ImdbDetails> getMovieDetails(int imdbId) throws ImdbException {
        return getManager().valueBuilder()
                .memoryCache()
                .key("%s-moviedetails-%s".formatted("IMDB", imdbId))
                .optionalSupplier(() -> {
                    final String url = "%s/?i=tt%s&r=xml".formatted(DOMAIN, StringUtils.leftPad(String.valueOf(imdbId), 7, "0"));
                    try {
                        return manager.getPageContentBuilder()
                                .url(url)
                                .getAsDocument()
                                .map(doc -> doc.getElementsByTagName("movie"))
                                .filter(nodeList -> nodeList.getLength() > 0)
                                .map(nodeList -> parseImdbDetails((Element) nodeList.item(0)));
                    } catch (Exception e) {
                        throw new ImdbException("Error IMDBAPI", url, e);
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
