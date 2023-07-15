package org.lodder.subtools.sublibrary.data.tvdb;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.experimental.ExtensionMethod;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.lodder.subtools.sublibrary.xml.XmlExtension;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author lodder
 * <a href="http://code.google.com/p/moviejukebox/">Source</a>
 */
@ExtensionMethod({ XmlExtension.class })
public class TheTvdbMirrors {

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

    public TheTvdbMirrors(String apikey, Manager manager) throws ManagerException, ParserConfigurationException {
        synchronized (this) {
            manager.getPageContentBuilder()
                    .url("http://www.thetvdb.com/api/" + apikey + "/mirrors.xml")
                    .getAsDocument().ifPresent(doc -> {
                        doc.getElementsByTagName("Mirror").stream()
                                .filter(nMirror -> nMirror.getNodeType() == Node.ELEMENT_NODE)
                                .map(Element.class::cast)
                                .forEach(eMirror -> {
                                    String url = XMLHelper.getStringTagValue("mirrorpath", eMirror);
                                    int typeMask = XMLHelper.getIntTagValue("typemask", eMirror);
                                    addMirror(typeMask, url);
                                });
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
