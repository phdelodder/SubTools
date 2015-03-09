package org.lodder.subtools.sublibrary.data.IMDB;

import java.net.URL;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.XmlHTTP;
import org.lodder.subtools.sublibrary.data.IMDB.model.IMDBDetails;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IMDBAPI extends XmlHTTP {

  public IMDBAPI(Manager manager) {
    super(manager);
  }

  public IMDBDetails getIMDBMovieDetails(String imdbid) throws IMDBException {
    final String xml = "http://www.imdbapi.com/?i=" + imdbid + "&r=xml";
    Document doc;
    try {
      doc = getXMLDisk(xml);
      NodeList nodeList = doc.getElementsByTagName("movie");
      if (nodeList.getLength() > 0) {
        return parseIMDBDetails((Element) nodeList.item(0));
      }
    } catch (Exception e) {
      throw new IMDBException("Error IMDBAPI", xml, e);
    }

    return null;
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
