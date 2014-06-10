package org.lodder.subtools.multisubdownloader.lib.library;

import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;

public class LibraryBuilder {

  protected final LibrarySettings librarySettings;

  public LibraryBuilder(LibrarySettings librarySettings) {
    this.librarySettings = librarySettings;
  }

  static String replaceWindowsChars(String text) {
    text = text.replace("|", "");
    text = text.replace("\"", "");
    text = text.replace("<", "");
    text = text.replace(">", "");
    text = text.replace("?", "");
    text = text.replace("*", "");
    text = text.replace(":", "");
    text = text.replace("/", "");
    text = text.replace("\\", "");
    if (text.substring(text.length() - 1).equals(".")) {
      text = text.substring(0, text.length() - 1);
    }
    return text.trim();
  }

  public String replaceFormatedEpisodeNumber(String structure, String tag,
      List<Integer> episodeNumbers, boolean leadingZero) {

    String formatedEpisodeNumber = "";
    if (structure.contains(tag)) {
      int posEnd = structure.indexOf(tag);
      String structurePart = structure.substring(0, posEnd);
      int posBegin = structurePart.lastIndexOf("%");
      String seperator = structure.substring(posBegin + 1, posEnd);


      for (final int epNum : episodeNumbers) {
        formatedEpisodeNumber += seperator + formatedNumber(epNum, leadingZero);
      }

      // strip the first seperator off
      formatedEpisodeNumber = formatedEpisodeNumber.substring(1, formatedEpisodeNumber.length());
    }
    return structure.replace(tag, formatedEpisodeNumber);

  }

  public String formatedNumber(int number, boolean leadingZero) {
    if (number < 10 && leadingZero) {
      return "0" + number;
    }
    return Integer.toString(number);
  }
}
