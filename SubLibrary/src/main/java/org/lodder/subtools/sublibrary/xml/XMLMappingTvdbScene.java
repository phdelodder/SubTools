package org.lodder.subtools.sublibrary.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;
import org.lodder.subtools.sublibrary.util.http.DropBoxClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLMappingTvdbScene {
  
  public final static String mappings = "/MultiSubDownloader/Mappings.xml";
  public final static String version = "/MultiSubDownloader/mappings.version.xml";

  public static void write(List<MappingTvdbScene> list, File f) throws Throwable {
    Document newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element rootElement = newDoc.createElement("MappingTvdbScene");
    newDoc.appendChild(rootElement);

    for (MappingTvdbScene item : list) {
      Element mapping = newDoc.createElement("mapping");
      Element tvdbId = newDoc.createElement("tvdbid");
      tvdbId.appendChild(newDoc.createTextNode(Integer.toString(item.getTvdbId())));
      mapping.appendChild(tvdbId);
      Element scene = newDoc.createElement("scene");
      scene.appendChild(newDoc.createTextNode(item.getSceneName()));
      mapping.appendChild(scene);
      rootElement.appendChild(mapping);
    }

    XMLHelper.writeToFile(f, newDoc);
  }

  public static ArrayList<MappingTvdbScene> read(File f) throws Throwable {
    Document newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
    return read(newDoc);
  }

  public static ArrayList<MappingTvdbScene> getOnlineMappingCollection() throws Throwable {
    String content = DropBoxClient.getDropBoxClient().getFile(mappings);
    return read(XMLHelper.getDocument(content));
  }

  public static ArrayList<MappingTvdbScene> read(Document newDoc) throws Throwable {
    ArrayList<MappingTvdbScene> list = new ArrayList<MappingTvdbScene>();
    NodeList nList = newDoc.getElementsByTagName("mapping");

    for (int i = 0; i < nList.getLength(); i++) {
      if (nList.item(i).getNodeType() == Node.ELEMENT_NODE) {
        int tvdbid = XMLHelper.getIntTagValue("tvdbid", (Element) nList.item(i));
        String scene = XMLHelper.getStringTagValue("scene", (Element) nList.item(i));
        MappingTvdbScene item = new MappingTvdbScene(scene, tvdbid);
        list.add(item);
      }
    }
    return list;
  }

  public static int getMappingsVersionNumber() throws IOException {
    String content = DropBoxClient.getDropBoxClient().getFile(version);
    return Integer.parseInt(content);
  }
}
