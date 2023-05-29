package org.lodder.subtools.multisubdownloader.lib.xml;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.lodder.subtools.multisubdownloader.settings.model.PathOrRegex;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.lodder.subtools.sublibrary.xml.XmlExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ XmlExtension.class })
public class XMLExclude {

    public static void write(List<PathOrRegex> list, Path f) throws Throwable {
        Document newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootElement = newDoc.createElement("Exclude-Bierdopje-Scene");
        newDoc.appendChild(rootElement);

        for (PathOrRegex item : list) {
            Element excludeitem = newDoc.createElement("excludeitem");
            Element description = newDoc.createElement("description");
            description.appendChild(newDoc.createTextNode(item.getValue()));
            excludeitem.appendChild(description);
            rootElement.appendChild(excludeitem);
        }

        XMLHelper.writeToFile(f, newDoc);
    }

    public static List<PathOrRegex> read(Path f) throws Throwable {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Files.newInputStream(f))
                .getElementsByTagName("excludeitem").stream()
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(node -> new PathOrRegex(XMLHelper.getStringTagValue("description", (Element) node))).toList();
    }

}
