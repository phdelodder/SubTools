package org.lodder.subtools.multisubdownloader.lib;

import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.VideoFile;

public class SubtitleSelection {
  
  private Settings settings;
  private VideoFile videoFile;

  public SubtitleSelection(Settings settings, VideoFile videoFile){
    this.settings = settings;
    this.videoFile = videoFile;
  }

  public int getAutomaticSubtitleSelection() { 
    Logger.instance.debug("getAutomaticSubtitleSelection: # quality rules: "
        + settings.getQualityRuleList().size());
    Logger.instance.debug("getAutomaticSubtitleSelection: quality rules: "
        + settings.getQualityRuleList().toString());
    Logger.instance.trace("Actions", "getAutomaticSubtitleSelection", "First run, using equal");
    
    List<Subtitle> matchingSubs = videoFile.getMatchingSubs();
    
    int result = qualityRuleSelectionCompare(matchingSubs, true);
    if (result > -1) return result;

    Logger.instance.trace("Actions", "getAutomaticSubtitleSelection",
        "Second run, using word exists in");
    result = qualityRuleSelectionCompare(matchingSubs, false);
    if (result > -1) return result;

    if (settings.isOptionsNoRuleMatchTakeFirst()) {
      Logger.instance.debug("getAutomaticSubtitleSelection: Using taking first rule");
      return 0;
    } else {
      Logger.instance.debug("getAutomaticSubtitleSelection: Nothing found");
      return -1;
    }
  }
  
  private int qualityRuleSelectionCompare(List<Subtitle> matchingSubs, boolean equal) {
    Subtitle subtitle;
    for (String quality : settings.getQualityRuleList()) {
      Logger.instance.trace("Actions", "qualityRuleSelectionCompare", "Quality Rule checked: "
          + quality);
      for (int i = 0; i < matchingSubs.size(); i++) {
        subtitle = matchingSubs.get(i);
        Logger.instance.debug("qualityRuleSelectionCompare: subtitle quality: "
            + subtitle.getQuality());
        Logger.instance.trace("Actions", "qualityRuleSelectionCompare", "subtitle quality: "
            + subtitle.getQuality());
        if (equal && quality.equalsIgnoreCase(subtitle.getQuality())) return i;
        if (!equal) {
          for (String q : quality.split(" ")) {
            if (subtitle.getQuality().toLowerCase().contains(q.toLowerCase())) return i;
          }
        }
      }
    }
    return -1;
  }
}
