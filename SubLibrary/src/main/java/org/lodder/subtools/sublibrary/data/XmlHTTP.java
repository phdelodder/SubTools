package org.lodder.subtools.sublibrary.data;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.PageContentBuilderCacheTypeIntf;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter(value = AccessLevel.PROTECTED)
public class XmlHTTP {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlHTTP.class);

    @Getter(value = AccessLevel.PROTECTED)
    private final Manager manager;

    public PageContentBuilderCacheTypeIntf getXML(String url)
            throws ParserConfigurationException, IOException, ManagerSetupException, ManagerException {
        return manager.getPageContentBuilder().url(url).userAgent(null);
    }

    // public Document getXMLDisk(String url) throws ManagerSetupException, ManagerException, ParserConfigurationException, SAXException, IOException
    // {
    // LOGGER.trace("getXMLDisk {}", url);
    // String content = manager.getPageContentBuilder().url(url).userAgent(null).cacheType(CacheType.DISK).get();
    // // Bierdopje issue! OElig
    // content = content.replace("&OElig;", "Œ");
    // // Bierdopje issue! &Pi;
    // content = content.replace("&Pi;", "\u003A0").replace("&pi;", "\u003A0");
    // // BIerdopje issue! &hellip;
    // content = content.replace("&hellip;", "...");
    // return XMLHelper.getDocument(content);
    // }
}
