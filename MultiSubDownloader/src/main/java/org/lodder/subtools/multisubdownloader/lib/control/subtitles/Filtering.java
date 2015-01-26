package org.lodder.subtools.multisubdownloader.lib.control.subtitles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.ExactNameFilter;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.Filter;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.KeywordFilter;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.ReleasegroupFilter;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class Filtering {

  private Settings settings;
  private Filter exactname;
  private Filter keyword;
  private Filter releasegroup;

  public Filtering(Settings settings) {
    this.settings = settings;
    exactname = new ExactNameFilter();
    keyword = new KeywordFilter();
    releasegroup = new ReleasegroupFilter();
  }

  public List<Subtitle> getFiltered(List<Subtitle> listFoundSubtitles, Release release) {
    Logger.instance.trace("Filtering", "getFiltered", "");

    List<Subtitle> listFilteredSubtitles;
    listFilteredSubtitles = new ArrayList<Subtitle>();

    if (settings.isOptionSubtitleExcludeHearingImpaired()) {
      Iterator<Subtitle> i = listFoundSubtitles.iterator();
      while (i.hasNext()) {
        Subtitle sub = i.next();
        if (sub.isHearingImpaired()) i.remove();
      }
    }
    
    if (settings.isOptionSubtitleKeywordMatch()) {
      listFilteredSubtitles = keyword.doFilter(release, listFoundSubtitles);
      if (listFilteredSubtitles.size() > 0) listFoundSubtitles = listFilteredSubtitles;
      listFilteredSubtitles = releasegroup.doFilter(release, listFoundSubtitles);
      if (listFilteredSubtitles.size() > 0) listFoundSubtitles = listFilteredSubtitles;
    }

    if (settings.isOptionSubtitleExactMatch()) {
      listFilteredSubtitles = exactname.doFilter(release, listFoundSubtitles);
      if (listFilteredSubtitles.size() > 0) listFoundSubtitles = listFilteredSubtitles;
    }

    return listFoundSubtitles;
  }

}
