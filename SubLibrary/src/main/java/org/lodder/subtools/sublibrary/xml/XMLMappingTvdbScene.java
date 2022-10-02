package org.lodder.subtools.sublibrary.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.settings.model.TvdbMapping;
import org.lodder.subtools.sublibrary.settings.model.TvdbMappings;
import org.lodder.subtools.sublibrary.util.http.DropBoxClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ XmlExtension.class })
public class XMLMappingTvdbScene {

    public final static String MAPPINGS = "/MultiSubDownloader/Mappings.xml";
    public final static String VERSION = "/MultiSubDownloader/mappings.version.xml";

    public final static String MAPPING_NAME = "mapping";
    public final static String TVDBID_NAME = "tvdbid";
    public final static String SCENE_NAME = "scene";

    public static void write(Manager manager, File f) throws Throwable {
        Document newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootElement = newDoc.createElement("MappingTvdbScene");
        newDoc.appendChild(rootElement);

        TvdbMappings.getPersistedTvdbMappings(manager).forEach(tvdbMapping -> {
            Element mappingElem = newDoc.createElement(MAPPING_NAME);
            Element tvdbIdElem = newDoc.createElement(TVDBID_NAME);
            tvdbIdElem.appendChild(newDoc.createTextNode(Integer.toString(tvdbMapping.getId())));
            mappingElem.appendChild(tvdbIdElem);

            Element sceneElem = newDoc.createElement(SCENE_NAME);
            sceneElem.appendChild(newDoc.createTextNode(tvdbMapping.getName()));
            mappingElem.appendChild(sceneElem);

            rootElement.appendChild(mappingElem);
        });
        XMLHelper.writeToFile(f, newDoc);
    }

    public static List<TvdbMapping> read(File f) throws Throwable {
        return read(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f));
    }

    public static List<TvdbMapping> read(Document newDoc) throws Throwable {
        return newDoc.getElementsByTagName(MAPPING_NAME).stream()
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(Element.class::cast)
                .map(eList -> {
                    int tvdbId = XMLHelper.getIntTagValue(TVDBID_NAME, eList);
                    String scene = XMLHelper.getStringTagValue(SCENE_NAME, eList);
                    return new TvdbMapping(tvdbId, scene);
                }).toList();
    }

    public static int getMappingsVersionNumber() throws IOException {
        String content = DropBoxClient.getDropBoxClient().getFile(VERSION);
        return Integer.parseInt(content);
    }
}
