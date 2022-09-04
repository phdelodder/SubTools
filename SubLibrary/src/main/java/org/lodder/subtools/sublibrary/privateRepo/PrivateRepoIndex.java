package org.lodder.subtools.sublibrary.privateRepo;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.privateRepo.model.IndexSubtitle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PrivateRepoIndex {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrivateRepoIndex.class);

    public static List<IndexSubtitle> getIndex(String index) {
        List<IndexSubtitle> repoList = null;
        IndexSubtitle currIndexSubtitle = null;
        String tagContent = null;

        byte[] byteArray;
        try {
            byteArray = index.getBytes(StandardCharsets.UTF_8);

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
                                if (repoList != null) {
                                    repoList.add(currIndexSubtitle);
                                }
                                break;
                            case "name":
                                if (currIndexSubtitle != null) {
                                    currIndexSubtitle.setName(tagContent);
                                }
                                break;
                            case "season":
                                if (currIndexSubtitle != null) {
                                    currIndexSubtitle.setSeason(Integer.parseInt(tagContent));
                                }
                                break;
                            case "episode":
                                if (currIndexSubtitle != null) {
                                    currIndexSubtitle.setEpisode(Integer.parseInt(tagContent));
                                }
                                break;
                            case "language":
                                if (currIndexSubtitle != null) {
                                    currIndexSubtitle.setLanguage(tagContent);
                                }
                                break;
                            case "filename":
                                if (currIndexSubtitle != null) {
                                    currIndexSubtitle.setFilename(tagContent);
                                }
                                break;
                            case "tvdbid":
                                if (currIndexSubtitle != null) {
                                    currIndexSubtitle.setTvdbid(Integer.parseInt(tagContent));
                                }
                                break;
                            case "uploader":
                                if (currIndexSubtitle != null) {
                                    currIndexSubtitle.setUploader(tagContent);
                                }
                                break;
                            case "originalSource":
                                if (currIndexSubtitle != null) {
                                    currIndexSubtitle.setOriginalSource(tagContent);
                                }
                                break;
                            case "videotype":
                                if (currIndexSubtitle != null) {
                                    currIndexSubtitle.setVideoType(VideoType.valueOf(tagContent));
                                }
                                break;
                            case "imdbid":
                                if (currIndexSubtitle != null) {
                                    currIndexSubtitle.setImdbid(Integer.parseInt(tagContent));
                                }
                                break;
                            case "year":
                                if (currIndexSubtitle != null) {
                                    currIndexSubtitle.setYear(Integer.parseInt(tagContent));
                                }
                                break;
                            default:
                                break;
                        }
                        break;

                    case XMLStreamConstants.START_DOCUMENT:
                        repoList = new ArrayList<>();
                        break;
                    default:
                        break;
                }

                prevEvent = event;

            }

        } catch (XMLStreamException e) {
            LOGGER.error("getIndex()", e);
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
                Element videotype = newDoc.createElement("videotype");
                videotype.appendChild(newDoc.createTextNode(item.getVideoType().toString()));
                privateRepoItem.appendChild(videotype);
                Element imdbid = newDoc.createElement("imdbid");
                imdbid.appendChild(newDoc.createTextNode(Integer.toString(item.getImdbid())));
                privateRepoItem.appendChild(imdbid);
                Element year = newDoc.createElement("year");
                year.appendChild(newDoc.createTextNode(Integer.toString(item.getYear())));
                privateRepoItem.appendChild(year);

                rootElement.appendChild(privateRepoItem);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(newDoc);
            transformer.transform(source, result);

            return result.getWriter().toString();
        } catch (ParserConfigurationException | TransformerFactoryConfigurationError | TransformerException e) {
            LOGGER.error("setIndex()", e);
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
        if (uploader.isEmpty() && originalSource.isEmpty()) {
            return filename;
        } else {
            return FilenameUtils.removeExtension(filename) + "--" + "uploader-" + uploader + "--"
                    + "originalSource-" + originalSource + "--" + "." + FilenameUtils.getExtension(filename);
        }
    }

    public static String extractOriginalFilename(String name) {
        if (name.contains("--")) {
            return name.split("--")[0] + "." + FilenameUtils.getExtension(name);
        }
        return name;
    }

    public static String getFullFilename(IndexSubtitle indexSubtitle) {
        return getFullFilename(indexSubtitle.getFilename(), indexSubtitle.getUploader(),
                indexSubtitle.getOriginalSource());
    }
}
