package org.lodder.subtools.multisubdownloader.lib.xml;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;

import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeItem;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.lodder.subtools.sublibrary.xml.XmlExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ XmlExtension.class })
public class XMLExclude {

    public static void write(List<SettingsExcludeItem> list, Path f) throws Throwable {
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

    public static List<SettingsExcludeItem> read(Path f) throws Throwable {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Files.newInputStream(f)).getElementsByTagName("excludeitem").stream()
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(node -> {
                    String type = XMLHelper.getStringTagValue("type", (Element) node);
                    String description = XMLHelper.getStringTagValue("description", (Element) node);
                    return new SettingsExcludeItem(description, SettingsExcludeType.valueOf(type));
                }).collect(Collectors.toList());
    }

}
