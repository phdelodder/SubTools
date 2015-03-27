package org.lodder.subtools.multisubdownloader.lib;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public abstract class SubtitleSelection {

  private Settings settings;

  public SubtitleSelection(Settings settings) {
    this.settings = settings;
  }

  public List<Subtitle> getAutomaticSelection(List<Subtitle> subtitles) {
    List<Subtitle> shortlist = new ArrayList<Subtitle>(subtitles);

    if (settings.isOptionsMinAutomaticSelection()) {
      for (int i = shortlist.size() - 1; i >= 0; i--) {
        if (shortlist.get(i).getScore() < settings.getOptionsMinAutomaticSelectionValue())
          shortlist.remove(i);
      }
    }

    if (settings.isOptionsDefaultSelection()) {
      List<Subtitle> defaultSelectionsFound = new ArrayList<Subtitle>();
      for (String q : settings.getOptionsDefaultSelectionQualityList()) {
        for (Subtitle subtitle : shortlist) {
          if (subtitle.getQuality().toLowerCase().contains(q.toLowerCase())) {
            if (!defaultSelectionsFound.contains(subtitle)) defaultSelectionsFound.add(subtitle);
          }
        }
      }

      if (defaultSelectionsFound.size() > 0) shortlist = defaultSelectionsFound;
    }

    return shortlist;
  }

  public abstract int getUserInput(Release release);
}
