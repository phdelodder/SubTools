package org.lodder.subtools.multisubdownloader.subtitleproviders.subsmax;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.lodder.subtools.multisubdownloader.subtitleproviders.subsmax.model.SubMaxSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.Html;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSubsMaxApi extends Html {

    private static String DEFAULTUSERAGENT = "Mozilla/5.25 Netscape/5.0 (Windows; I; Win95)";
    private static final Logger LOGGER = LoggerFactory.getLogger(JSubsMaxApi.class);

    public JSubsMaxApi(Manager manager) {
        super(manager, DEFAULTUSERAGENT);
    }

    public List<SubMaxSubtitleDescriptor> searchSubtitles(String name, int season, int episode, String languageid) {

        List<SubMaxSubtitleDescriptor> lSubtitles = new ArrayList<>();
        SubMaxSubtitleDescriptor submaxitem = null;
        String tagContent = null;
        String language = "";

        if ("nl".equals(languageid)) {
            language = "dutch";
        } else if ("en".equals(languageid)) {
            language = "english";
        }

        String url = "http://subsmax.com/api/50/" + name.replace(" ", "%20") + "-s" + season + "e" + episode + "-" + language;

        byte[] byteArray;
        try {
            String html = this.getHtml(url);
            byteArray = html.getBytes(StandardCharsets.UTF_8);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);

            int prevEvent = 0;
            while (reader.hasNext()) {
                int event = reader.next();

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        tagContent = "";
                        if ("item".equals(reader.getLocalName())) {
                            submaxitem = new SubMaxSubtitleDescriptor();
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
                            case "item":
                                if (submaxitem != null) {
                                    lSubtitles.add(submaxitem);
                                }
                                break;
                            case "title":
                                if (submaxitem != null) {
                                    submaxitem.setTitle(tagContent);
                                }
                                break;
                            case "link":
                                if (submaxitem != null) {
                                    int pos = tagContent.lastIndexOf('/');
                                    tagContent =
                                            tagContent.substring(0, pos) + "/download-subtitle"
                                                    + tagContent.substring(pos);
                                    submaxitem.setLink(tagContent);
                                }
                                break;
                            case "filename":
                                if (submaxitem != null) {
                                    submaxitem.setFilename(tagContent);
                                }
                                break;
                            case "languages":
                                if (submaxitem != null) {
                                    submaxitem.setLanguages(tagContent);
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }

                prevEvent = event;
            }
        } catch (Exception e) {
            LOGGER.error("SUBSMAXAPI searchSubtitles", e);
        }

        return lSubtitles;
    }
}
