package org.lodder.subtools.sublibrary.privateRepo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.privateRepo.model.IndexSubtitle;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class PrivateRepoIndex {

  public static List<IndexSubtitle> getIndexOld(String index) {
    Document doc;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    ArrayList<IndexSubtitle> list = new ArrayList<IndexSubtitle>();
    try {
      // Use the factory to create a builder
      DocumentBuilder builder;
      builder = factory.newDocumentBuilder();
      doc = builder.parse(new ByteArrayInputStream(index.getBytes("UTF-8")));

      NodeList nList = doc.getElementsByTagName("PrivateRepoItem");

      for (int i = 0; i < nList.getLength(); i++) {
        if (nList.item(i).getNodeType() == Node.ELEMENT_NODE) {
          int season = XMLHelper.getIntTagValue("season", (Element) nList.item(i));
          int episode = XMLHelper.getIntTagValue("episode", (Element) nList.item(i));
          int tvdbid = XMLHelper.getIntTagValue("tvdbid", (Element) nList.item(i));
          String name = XMLHelper.getStringTagValue("name", (Element) nList.item(i));
          String language = XMLHelper.getStringTagValue("language", (Element) nList.item(i));
          String filename = XMLHelper.getStringTagValue("filename", (Element) nList.item(i));
          String uploader = XMLHelper.getStringTagValue("uploader", (Element) nList.item(i));
          String originalSource =
              XMLHelper.getStringTagValue("originalSource", (Element) nList.item(i));
          IndexSubtitle item =
              new IndexSubtitle(name, season, episode, filename, language, tvdbid, uploader,
                  originalSource);
          list.add(item);
        }
      }

    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return list;

  }

  public static List<IndexSubtitle> getIndex(String index) {
    List<IndexSubtitle> repoList = null;
    IndexSubtitle currIndexSubtitle = null;
    String tagContent = null;

    byte[] byteArray;
    try {
      byteArray = index.getBytes("UTF-8");

      ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);

      int prevEvent = 0;
      while (reader.hasNext()) {
        int event = reader.next();

        switch (event) {
          case XMLStreamConstants.START_ELEMENT:
            tagContent = "";
            if ("PrivateRepoItem".equals(reader.getLocalName())) {
              currIndexSubtitle = new IndexSubtitle();
              // currIndexSubtitle.id = reader.getAttributeValue(0);
            }
            if ("PrivateRepoIndex".equals(reader.getLocalName())) {
              repoList = new ArrayList<>();
            }
            break;

          case XMLStreamConstants.CHARACTERS:
            if (prevEvent == event) {
              tagContent = tagContent + reader.getText();
            } else {
              tagContent = reader.getText();
            }
            break;

          case XMLStreamConstants.END_ELEMENT:
            switch (reader.getLocalName()) {
              case "PrivateRepoItem":
                repoList.add(currIndexSubtitle);
                break;
              case "name":
                currIndexSubtitle.setName(tagContent);
                break;
              case "season":
                currIndexSubtitle.setSeason(Integer.parseInt(tagContent));
                break;
              case "episode":
                currIndexSubtitle.setEpisode(Integer.parseInt(tagContent));
                break;
              case "language":
                currIndexSubtitle.setLanguage(tagContent);
                break;
              case "filename":
                currIndexSubtitle.setFilename(tagContent);
                break;
              case "tvdbid":
                currIndexSubtitle.setTvdbid(Integer.parseInt(tagContent));
                break;
              case "uploader":
                currIndexSubtitle.setUploader(tagContent);
                break;
              case "originalSource":
                currIndexSubtitle.setOriginalSource(tagContent);
                break;
            }
            break;

          case XMLStreamConstants.START_DOCUMENT:
            repoList = new ArrayList<>();
            break;
        }

        prevEvent = event;

      }

    } catch (UnsupportedEncodingException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (XMLStreamException e) {
      Logger.instance.error(Logger.stack2String(e));
    }

    return repoList;
  }

  public static String getStringAtributeValue(String sTag, String sAtribute, Element eElement) {
    NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
    return ((Element) nlList).getAttribute(sAtribute);
  }

  public static int getIntTagValue(String sTag, Element eElement) {
    NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
    Node nValue = nlList.item(0);

    if (nValue == null) {
      return 0;
    } else {
      return Integer.parseInt(nValue.getNodeValue());
    }

  }

  public static String setIndex(List<IndexSubtitle> index) {
    Document newDoc;
    try {
      newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      Element rootElement = newDoc.createElement("PrivateRepoIndex");
      newDoc.appendChild(rootElement);

      for (IndexSubtitle item : index) {
        Element privateRepoItem = newDoc.createElement("PrivateRepoItem");
        Element name = newDoc.createElement("name");
        name.appendChild(newDoc.createTextNode(item.getName()));
        privateRepoItem.appendChild(name);
        Element season = newDoc.createElement("season");
        season.appendChild(newDoc.createTextNode(Integer.toString(item.getSeason())));
        privateRepoItem.appendChild(season);
        Element episode = newDoc.createElement("episode");
        episode.appendChild(newDoc.createTextNode(Integer.toString(item.getEpisode())));
        privateRepoItem.appendChild(episode);
        Element language = newDoc.createElement("language");
        language.appendChild(newDoc.createTextNode(item.getLanguage()));
        privateRepoItem.appendChild(language);
        Element filename = newDoc.createElement("filename");
        filename.appendChild(newDoc.createTextNode(item.getFilename()));
        privateRepoItem.appendChild(filename);
        Element tvdbid = newDoc.createElement("tvdbid");
        tvdbid.appendChild(newDoc.createTextNode(Integer.toString(item.getTvdbid())));
        privateRepoItem.appendChild(tvdbid);
        Element uploader = newDoc.createElement("uploader");
        uploader.appendChild(newDoc.createTextNode(item.getUploader()));
        privateRepoItem.appendChild(uploader);
        Element originalsource = newDoc.createElement("originalSource");
        originalsource.appendChild(newDoc.createTextNode(item.getOriginalSource()));
        privateRepoItem.appendChild(originalsource);

        rootElement.appendChild(privateRepoItem);
      }

      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");

      StreamResult result = new StreamResult(new StringWriter());
      DOMSource source = new DOMSource(newDoc);
      transformer.transform(source, result);

      return result.getWriter().toString();
    } catch (ParserConfigurationException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (TransformerConfigurationException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (TransformerFactoryConfigurationError e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (TransformerException e) {
      Logger.instance.error(Logger.stack2String(e));
    }
    return "";

  }

  public static String extractUploader(String filename) {
    return extract(filename, "uploader-");
  }

  public static String extractOriginalSource(String filename) {
    return extract(filename, "originalSource-");
  }

  public static String extract(String filename, String extractWord) {
    String splitter = "--";
    int intExtractWordLength = extractWord.length();
    if (filename.contains(extractWord)) {
      int startPos = filename.indexOf(extractWord);
      String temp = filename.substring(startPos + intExtractWordLength);
      return temp.split(splitter)[0];
    }
    return "";
  }

  public static String getFullFilename(String filename, String uploader, String originalSource) {
    if (uploader.isEmpty() & originalSource.isEmpty()) {
      return filename;
    } else {
      return FilenameUtils.removeExtension(filename) + "--" + "uploader-" + uploader + "--"
          + "originalSource-" + originalSource + "--" + "." + FilenameUtils.getExtension(filename);
    }
  }

  /**
   * @param filename
   * @return
   */
  public static String extractOriginalFilename(String name) {
    if (name.contains("--")) return name.split("--")[0] + "." + FilenameUtils.getExtension(name);
    return name;
  }

  /**
   * @param indexSubtitle
   * @return
   */
  public static String getFullFilename(IndexSubtitle indexSubtitle) {
    return getFullFilename(indexSubtitle.getFilename(), indexSubtitle.getUploader(),
        indexSubtitle.getOriginalSource());
  }

}
