package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;

public class ExactNameFilter extends Filter {

  @Override
  public List<Subtitle> doFilter(Release release, List<Subtitle> Subtitles) {
    List<Subtitle> filteredList = new ArrayList<Subtitle>();
    Pattern p = Pattern.compile(getReleasename(release).replaceAll(" ", "[. ]"), Pattern.CASE_INSENSITIVE);

    for (Subtitle subtitle : Subtitles) {
      Matcher m = p.matcher(subtitle.getFilename().toLowerCase().replace(".srt", ""));
      if (m.matches()) {
        Logger.instance.debug("getSubtitlesFiltered: found EXACT match: " + subtitle.getFilename());
        
        subtitle.setSubtitleMatchType(SubtitleMatchType.EXACT);
        subtitle.setQuality(ReleaseParser.getQualityKeyword(subtitle.getFilename()));
        
        filteredList.add(subtitle);
      }
    }
    
    return filteredList;
  }
}
