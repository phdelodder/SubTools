package org.lodder.subtools.sublibrary.data.OMDB;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.XmlHTTP;
import org.lodder.subtools.sublibrary.data.OMDB.model.OMDBDetails;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class OMDBAPI extends XmlHTTP {

    public OMDBAPI(Manager manager) {
        super(manager);
    }

    public OMDBDetails getOMDBMovieDetails(String imdbid) throws OMDBException {
        final String xml = "http://www.omdbapi.com/?i=" + imdbid + "&plot=short&r=xml";
        Document doc;
        try {
            doc = getXMLDisk(xml);
            NodeList nodeList = doc.getElementsByTagName("movie");
            if (nodeList.getLength() > 0) {
                return parseOMDBDetails((Element) nodeList.item(0));
            }
        } catch (Exception e) {
            throw new OMDBException("Error OMDBAPI", xml, e);
        }

        return null;
    }

    private OMDBDetails parseOMDBDetails(Element item) {
        return new OMDBDetails(item.getAttribute("title"), Integer.parseInt(item.getAttribute("year")));
    }

}
