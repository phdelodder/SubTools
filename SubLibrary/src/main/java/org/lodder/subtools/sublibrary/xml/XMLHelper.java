package org.lodder.subtools.sublibrary.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLHelper.class);

    private static String xmlCleanup(String text) {
        return text.replace("&amp;", "&");
    }

    private static String htmlCleanup(String text) {
        return StringUtils.unescapeHTML(text);
    }

    public static String getStringTagValue(String sTag, Element eElement) {
        LOGGER.trace("getStringTagValue: sTag [{}]", sTag);
        return htmlCleanup(xmlCleanup(getStringTagRawValue(sTag, eElement)));
    }

    public static String getStringTagRawValue(String sTag, Element eElement) {
        LOGGER.trace("getStringTagRawValue: sTag [{}]", sTag);
        if (eElement.getElementsByTagName(sTag).getLength() > 0) {
            Node nValue = eElement.getElementsByTagName(sTag).item(0).getChildNodes().item(0);
            if (nValue != null) {
                return nValue.getNodeValue();
            }
        }
        return "";
    }

    public static int getIntTagValue(String sTag, Element eElement) {
        LOGGER.trace("getIntTagValue: sTag [{}]", sTag);
        Node nValue = eElement.getElementsByTagName(sTag).item(0).getChildNodes().item(0);
        return nValue == null ? 0 : Integer.parseInt(nValue.getNodeValue());
    }

    public static Document getDocument(String string) throws ParserConfigurationException, IOException {
        return getDocument(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
    }

    public static Document getDocument(InputStream inputStream) throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Use the factory to create a builder
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        try {
            return builder.parse(inputStream);
        } catch (SAXException | IOException e) {
            throw new IOException("XML input could not be converted to a document");
        }
    }
}
