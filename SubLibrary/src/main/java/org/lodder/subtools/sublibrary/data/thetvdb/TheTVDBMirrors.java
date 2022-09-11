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
import org.lodder.subtools.sublibrary.xml.XmlExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lombok.experimental.ExtensionMethod;

/**
 * @author lodder
 *         Code found on Web: http://code.google.com/p/moviejukebox/
 */
@ExtensionMethod({ XmlExtension.class })
public class TheTVDBMirrors {

    public static final String TYPE_XML = "XML";
    public static final String TYPE_BANNER = "BANNER";
    public static final String TYPE_ZIP = "ZIP";

    private static final int MASK_XML = 1;
    private static final int MASK_BANNER = 2;
    private static final int MASK_ZIP = 4;

    private static final Random RNDM = new Random();

    private final List<String> xmlList = new ArrayList<>();
    private final List<String> bannerList = new ArrayList<>();
    private final List<String> zipList = new ArrayList<>();

    private final XmlHTTP xmlHTTPAPI;

    public TheTVDBMirrors(String apikey, Manager manager) throws ManagerSetupException, ManagerException, ParserConfigurationException, IOException {
        // Make this synchronized so that only one
        synchronized (this) {
            xmlHTTPAPI = new XmlHTTP(manager);
            String urlString = "http://www.thetvdb.com/api/" + apikey + "/mirrors.xml";
            Document doc = xmlHTTPAPI.getXML(urlString);

            doc.getElementsByTagName("Mirror").stream()
                    .filter(nMirror -> nMirror.getNodeType() == Node.ELEMENT_NODE)
                    .map(Element.class::cast)
                    .forEach(eMirror -> {
                        String url = XMLHelper.getStringTagValue("mirrorpath", eMirror);
                        int typeMask = XMLHelper.getIntTagValue("typemask", eMirror);
                        addMirror(typeMask, url);
                    });
        }
    }

    public String getMirror(String type) {
        if (TYPE_XML.equals(type) && !xmlList.isEmpty()) {
            return xmlList.get(RNDM.nextInt(xmlList.size()));
        } else if (TYPE_BANNER.equals(type) && !bannerList.isEmpty()) {
            return bannerList.get(RNDM.nextInt(bannerList.size()));
        } else if (TYPE_ZIP.equals(type) && !zipList.isEmpty()) {
            return zipList.get(RNDM.nextInt(zipList.size()));
        } else {
            return null;
        }
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
