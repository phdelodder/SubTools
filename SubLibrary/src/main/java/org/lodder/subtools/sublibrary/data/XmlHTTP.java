package org.lodder.subtools.sublibrary.data;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlHTTP {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlHTTP.class);

    private Manager manager;

    public XmlHTTP(Manager manager) {
        this.manager = manager;
    }

    public Document getXML(String url) throws ParserConfigurationException, IOException,
            ManagerSetupException, ManagerException {
        LOGGER.trace("getXML {}", url);
        return XMLHelper.getDocument(manager.getContentStream(url, null, false));
    }

    public Document getXMLDisk(String url) throws ManagerSetupException, ManagerException,
            ParserConfigurationException, SAXException, IOException {
        LOGGER.trace("getXMLDisk {}", url);
        String content = manager.getContent(url, null, true);
        // Bierdopje issue! OElig
        content = content.replace("&OElig;", "Œ");
        // Bierdopje issue! &Pi;
        content = content.replace("&Pi;", "\u003A0").replace("&pi;", "\u003A0");
        // BIerdopje issue! &hellip;
        content = content.replace("&hellip;", "...");
        return XMLHelper.getDocument(content);
    }

    public void removeCacheEntry(String url) throws ManagerSetupException {
        LOGGER.trace("removeCacheEntry {}", url);
        manager.removeCacheObject(url);
    }

}
