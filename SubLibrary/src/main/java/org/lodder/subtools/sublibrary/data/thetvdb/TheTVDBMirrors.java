package org.lodder.subtools.sublibrary.data.thetvdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.data.XmlHTTP;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author lodder
 *         Code found on Web: http://code.google.com/p/moviejukebox/
 *
 */

public class TheTVDBMirrors {

    public static final String TYPE_XML = "XML";
    public static final String TYPE_BANNER = "BANNER";
    public static final String TYPE_ZIP = "ZIP";

    private static final int MASK_XML = 1;
    private static final int MASK_BANNER = 2;
    private static final int MASK_ZIP = 4;

    private static final Random RNDM = new Random();

    private List<String> xmlList = new ArrayList<>();
    private List<String> bannerList = new ArrayList<>();
    private List<String> zipList = new ArrayList<>();

    private final XmlHTTP xmlHTTPAPI;

    public TheTVDBMirrors(String apikey, Manager manager) throws ManagerSetupException, ManagerException, ParserConfigurationException, IOException {
        // Make this synchronized so that only one
        synchronized (this) {
            xmlHTTPAPI = new XmlHTTP(manager);
            String urlString = "http://www.thetvdb.com/api/" + apikey + "/mirrors.xml";
            Document doc = xmlHTTPAPI.getXML(urlString);

            int typeMask = 0;
            String url = null;

            NodeList nlMirror = doc.getElementsByTagName("Mirror");
            for (int nodeLoop = 0; nodeLoop < nlMirror.getLength(); nodeLoop++) {
                Node nMirror = nlMirror.item(nodeLoop);

                if (nMirror.getNodeType() == Node.ELEMENT_NODE) {
                    Element eMirror = (Element) nMirror;
                    url = XMLHelper.getStringTagValue("mirrorpath", eMirror);
                    typeMask = XMLHelper.getIntTagValue("typemask", eMirror);
                    addMirror(typeMask, url);
                }
            }
        }
    }

    public String getMirror(String type) {
        String url = null;
        if (TYPE_XML.equals(type) && !xmlList.isEmpty()) {
            url = xmlList.get(RNDM.nextInt(xmlList.size()));
        } else if (TYPE_BANNER.equals(type) && !bannerList.isEmpty()) {
            url = bannerList.get(RNDM.nextInt(bannerList.size()));
        } else if (TYPE_ZIP.equals(type) && !zipList.isEmpty()) {
            url = zipList.get(RNDM.nextInt(zipList.size()));
        }
        return url;
    }

    private void addMirror(int typeMask, String url) {
        switch (typeMask) {
            case MASK_XML -> xmlList.add(url);
            case MASK_BANNER -> bannerList.add(url);
            case MASK_XML + MASK_BANNER -> {
                xmlList.add(url);
                bannerList.add(url);
            }
            case MASK_ZIP -> zipList.add(url);
            case MASK_XML + MASK_ZIP -> {
                xmlList.add(url);
                zipList.add(url);
            }
            case MASK_BANNER + MASK_ZIP -> {
                bannerList.add(url);
                zipList.add(url);
            }
            case MASK_XML + MASK_BANNER + MASK_ZIP -> {
                xmlList.add(url);
                bannerList.add(url);
                zipList.add(url);
            }
            default -> {
            }
        }
    }

}
