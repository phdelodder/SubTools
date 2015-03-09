package org.lodder.subtools.sublibrary.data;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlHTTP {

  private Manager manager;

  public XmlHTTP(Manager manager) {
    this.manager = manager;
  }

  public Document getXML(String url) throws ParserConfigurationException,
      IOException, ManagerSetupException, ManagerException {
    Logger.instance.trace("XmlHTTPAPI", "getXML", "");
    return XMLHelper.getDocument(manager.getContentStream(url, null, false));
  }

  public Document getXMLDisk(String url) throws ManagerSetupException, ManagerException, ParserConfigurationException, SAXException, IOException {
    Logger.instance.trace("XmlHTTPAPI", "getXMLDisk", "");
    String content = manager.getContent(url, null, true);
    // Bierdopje issue! OElig
    content = content.replaceAll("&OElig;", "Œ");
    // Bierdopje issue! &Pi;
    content = content.replaceAll("&Pi;", "\u003A0").replaceAll("&pi;", "\u003A0");
    // BIerdopje issue! &hellip;
    content = content.replaceAll("&hellip;", "...");
    return XMLHelper.getDocument(content);
  }

  public void removeCacheEntry(String url) throws ManagerSetupException {
    manager.removeCacheObject(url);
  }

}
