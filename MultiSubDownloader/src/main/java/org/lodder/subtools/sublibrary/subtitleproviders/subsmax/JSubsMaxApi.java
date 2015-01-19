package org.lodder.subtools.sublibrary.subtitleproviders.subsmax;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.lodder.subtools.sublibrary.data.Html;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.subtitleproviders.subsmax.model.SubMaxSubtitleDescriptor;

public class JSubsMaxApi extends Html {

  public JSubsMaxApi() {
    super();
  }

  public List<SubMaxSubtitleDescriptor> searchSubtitles(String name, int season, int episode,
      String languageid) {

    List<SubMaxSubtitleDescriptor> lSubtitles = new ArrayList<SubMaxSubtitleDescriptor>();
    SubMaxSubtitleDescriptor submaxitem = null;
    String tagContent = null;
    String language = "";

    if (languageid.equals("nl")) {
      language = "dutch";
    } else if (languageid.equals("en")) {
      language = "english";
    }

    String url =
        "http://subsmax.com/api/50/" + name.replace(" ", "%20") + "-s" + season + "e" + episode + "-" + language;

    String html = this.getHtml(url);

    byte[] byteArray;
    try {
      byteArray = html.getBytes("UTF-8");

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
                if (submaxitem != null) lSubtitles.add(submaxitem);
                break;
              case "title":
                if (submaxitem != null) submaxitem.setTitle(tagContent);
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
                if (submaxitem != null) submaxitem.setFilename(tagContent);
                break;
              case "languages":
                if (submaxitem != null) submaxitem.setLanguages(tagContent);
                break;
            }
            break;
        }

        prevEvent = event;
      }
    } catch (UnsupportedEncodingException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (XMLStreamException e) {
      Logger.instance.error(Logger.stack2String(e));
    }

    return lSubtitles;
  }
}
