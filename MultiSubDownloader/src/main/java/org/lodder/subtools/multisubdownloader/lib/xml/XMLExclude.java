package org.lodder.subtools.multisubdownloader.lib.xml;

import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeItem;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XMLExclude {

    public static void write(List<SettingsExcludeItem> list, File f) throws Throwable {
        Document newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootElement = newDoc.createElement("Exclude-Bierdopje-Scene");
        newDoc.appendChild(rootElement);

        for (SettingsExcludeItem item : list) {
            Element excludeitem = newDoc.createElement("excludeitem");
            Element type = newDoc.createElement("type");
            type.appendChild(newDoc.createTextNode(item.getType().toString()));
            excludeitem.appendChild(type);
            Element description = newDoc.createElement("description");
            description.appendChild(newDoc.createTextNode(item.getDescription()));
            excludeitem.appendChild(description);
            rootElement.appendChild(excludeitem);
        }

        XMLHelper.writeToFile(f, newDoc);
    }

    public static ArrayList<SettingsExcludeItem> read(File f) throws Throwable {
        ArrayList<SettingsExcludeItem> list = new ArrayList<>();
        Document newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
        NodeList nList = newDoc.getElementsByTagName("excludeitem");

        for (int i = 0; i < nList.getLength(); i++) {
            if (nList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                String type = XMLHelper.getStringTagValue("type", (Element) nList.item(i));
                String description = XMLHelper.getStringTagValue("description", (Element) nList.item(i));
                SettingsExcludeItem item = new SettingsExcludeItem(description, SettingsExcludeType.valueOf(type));
                list.add(item);
            }
        }
        return list;
    }

}
