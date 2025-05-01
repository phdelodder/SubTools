package org.lodder.subtools.sublibrary.data.tvdb;

import static org.lodder.subtools.sublibrary.PageContentParams.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author lodder
 * <a href="http://code.google.com/p/moviejukebox/">Source</a>
 */
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

    public TheTvdbMirrors(String apikey, Manager manager) throws ManagerException, ParserConfigurationException,
        IOException {
        synchronized (this) {
            manager.getAsDocument(url("http://www.thetvdb.com/api/$apikey/mirrors.xml"))
                .getElementsByTagName("Mirror").stream()
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
        return switch (type) {
            case TYPE_XML -> xmlList.isEmpty() ? null : xmlList.get(RNDM.nextInt(xmlList.size()));
            case TYPE_BANNER -> bannerList.isEmpty() ? null : bannerList.get(RNDM.nextInt(bannerList.size()));
            case TYPE_ZIP -> zipList.isEmpty() ? null : zipList.get(RNDM.nextInt(zipList.size()));
            default -> null;
        };
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
