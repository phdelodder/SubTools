package org.lodder.subtools.sublibrary.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;

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
    public final static String ALTNAMES_NAME = "altNames";
    public final static String ALTNAME_NAME = "altName";

    public static void write(TvdbMappings tvdbMappings, File f) throws Throwable {
        Document newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootElement = newDoc.createElement("MappingTvdbScene");
        newDoc.appendChild(rootElement);

        tvdbMappings.forEach((tvdbId, tvdbMapping) -> {
            Element mappingElem = newDoc.createElement(MAPPING_NAME);
            Element tvdbIdElem = newDoc.createElement(TVDBID_NAME);
            tvdbIdElem.appendChild(newDoc.createTextNode(Integer.toString(tvdbId)));
            mappingElem.appendChild(tvdbIdElem);

            Element sceneElem = newDoc.createElement(SCENE_NAME);
            sceneElem.appendChild(newDoc.createTextNode(tvdbMapping.getName()));
            mappingElem.appendChild(sceneElem);

            Element altNamesElem = newDoc.createElement(ALTNAMES_NAME);
            tvdbMapping.getAlternativeNames().forEach(altName -> {
                Element altNameElem = newDoc.createElement(ALTNAME_NAME);
                altNamesElem.appendChild(altNameElem);
                altNameElem.appendChild(newDoc.createTextNode(altName));
            });
            mappingElem.appendChild(altNamesElem);

            rootElement.appendChild(mappingElem);
        });
        XMLHelper.writeToFile(f, newDoc);
    }

    public static TvdbMappings read(File f) throws Throwable {
        return read(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f));
    }

    public static TvdbMappings read(Document newDoc) throws Throwable {
        TvdbMappings tvdbMappings = new TvdbMappings();
        newDoc.getElementsByTagName(MAPPING_NAME).stream()
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(Element.class::cast)
                .forEach(eList -> {
                    int tvdbId = XMLHelper.getIntTagValue(TVDBID_NAME, eList);
                    String scene = XMLHelper.getStringTagValue(SCENE_NAME, eList);
                    Element altNamesElem = (Element) eList.getElementsByTagName(ALTNAMES_NAME).item(0);
                    TvdbMapping tvdbMapping = new TvdbMapping(scene);
                    altNamesElem.getElementsByTagName(ALTNAME_NAME).stream()
                            .map(altNameNode -> XMLHelper.getStringTagValue(ALTNAME_NAME, (Element) altNameNode))
                            .forEach(tvdbMapping::addAlternativename);
                    tvdbMappings.add(tvdbId, tvdbMapping);
                });
        return tvdbMappings;
    }

    public static int getMappingsVersionNumber() throws IOException {
        String content = DropBoxClient.getDropBoxClient().getFile(VERSION);
        return Integer.parseInt(content);
    }
}
