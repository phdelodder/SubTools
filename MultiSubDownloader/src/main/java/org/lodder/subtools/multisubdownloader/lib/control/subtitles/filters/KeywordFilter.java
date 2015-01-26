package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;

public class KeywordFilter extends Filter {

  @Override
  public List<Subtitle> doFilter(Release release, List<Subtitle> Subtitles) {
    List<Subtitle> filteredList = new ArrayList<Subtitle>();
    String keywordsFile = ReleaseParser.getQualityKeyword(getReleasename(release));

    for (Subtitle subtitle : Subtitles) {
      boolean checkKeywordMatch = checkKeywordSubtitleMatch(subtitle, keywordsFile);

      if (checkKeywordMatch) {
        Logger.instance.debug("getSubtitlesFiltered: found KEYWORD match: "
            + subtitle.getFilename());

        subtitle.setSubtitleMatchType(SubtitleMatchType.KEYWORD);
        subtitle.setQuality(ReleaseParser.getQualityKeyword(subtitle.getFilename()));

        filteredList.add(subtitle);
      }
    }

    return filteredList;
  }

}
