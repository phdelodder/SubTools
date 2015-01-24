package org.lodder.subtools.multisubdownloader.lib.library;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;

import java.util.List;

public class LibraryBuilder {

  protected final LibrarySettings librarySettings;

  public LibraryBuilder(LibrarySettings librarySettings) {
    this.librarySettings = librarySettings;
  }

  public String replaceFormatedEpisodeNumber(String structure, String tag,
      List<Integer> episodeNumbers, boolean leadingZero) {

    String formatedEpisodeNumber = "";
    if (structure.contains(tag)) {
      int posEnd = structure.indexOf(tag);
      String structurePart = structure.substring(0, posEnd);
      int posBegin = structurePart.lastIndexOf("%");
      String seperator = structure.substring(posBegin + 1, posEnd);

      StringBuilder builder = new StringBuilder();
      for (final int epNum : episodeNumbers) {
        builder.append(seperator).append(formatedNumber(epNum, leadingZero));
      }
      formatedEpisodeNumber += builder.toString();
      
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
