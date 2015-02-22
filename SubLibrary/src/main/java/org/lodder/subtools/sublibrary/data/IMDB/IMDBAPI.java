package org.lodder.subtools.sublibrary.data.IMDB;

import org.lodder.subtools.sublibrary.data.IMDB.model.IMDBDetails;
import org.lodder.subtools.sublibrary.data.XmlHTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;


/**
 * Created by IntelliJ IDEA.
 * User: lodder
 * Date: 21/08/11
 * Time: 19:32
 * To change this template use File | Settings | File Templates.
 */
public class IMDBAPI extends XmlHTTP {

    public IMDBAPI() {
        super();
    }

    public IMDBDetails getIMDBMovieDetails(String imdbid) throws IMDBException {
        final String xml = "http://www.imdbapi.com/?i=" + imdbid + "&r=xml";
        Document doc = getXMLDisk(xml);
        NodeList nodeList = doc.getElementsByTagName("movie");
        if (nodeList.getLength() > 0) {
            return parseIMDBDetails((Element) nodeList.item(0));
        }
        throw new IMDBException("Error IMDBAPI",xml);
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
        	//do nothing
        }
        return details;
    }
}
