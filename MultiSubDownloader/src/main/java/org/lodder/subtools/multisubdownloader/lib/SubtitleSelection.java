package org.lodder.subtools.multisubdownloader.lib;

import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.VideoFile;

public class SubtitleSelection {

  private Settings settings;
  private VideoFile videoFile;

  public SubtitleSelection(Settings settings, VideoFile videoFile) {
    this.settings = settings;
    this.videoFile = videoFile;
  }

  public int getAutomatic() {
    Logger.instance.debug("getAutomaticSubtitleSelection: # quality rules: "
        + settings.getQualityRuleList().size());
    Logger.instance.debug("getAutomaticSubtitleSelection: quality rules: "
        + settings.getQualityRuleList().toString());

    int result = -1;

    if (settings.isOptionsAutomaticDownloadSelectionQuality()) {
      result = qualityRuleSelectionCompare(true);
      if (result > -1) return result;

      result = qualityRuleSelectionCompare(false);
      if (result > -1) return result;
    }

    if (settings.isOptionsAutomaticDownloadSelectionTeam()) {
      result = teamCompare(true);
      if (result > -1) return result;

      result = teamCompare(false);
      if (result > -1) return result;
    }

    if (settings.isOptionsNoRuleMatchTakeFirst()) {
      Logger.instance.debug("getAutomaticSubtitleSelection: Using taking first rule");
      return 0;
    } else {
      Logger.instance.debug("getAutomaticSubtitleSelection: Nothing found");
      return -1;
    }
  }

  private int teamCompare(boolean equal) {
    Logger.instance.trace("SubtitleSelection", "teamCompare", "equal: " + equal);
    List<Subtitle> matchingSubs = videoFile.getMatchingSubs();
    Subtitle subtitle;
    Logger.instance.trace("teamCompare", "teamCompare", "videofile team: " + videoFile.getTeam());
    for (int i = 0; i < matchingSubs.size(); i++) {
      subtitle = matchingSubs.get(i);
      Logger.instance.trace("SubtitleSelection", "qualityRuleSelectionCompare", "subtitle team: "
          + subtitle.getTeam());
      if (equal) {
        if (subtitle.getTeam().equalsIgnoreCase(videoFile.getTeam())) return i;
      } else {
        for (String t : subtitle.getTeam().split(" ")) {
          if (videoFile.getQuality().toLowerCase().contains(t.toLowerCase())) return i;
        }
      }
    }

    return -1;
  }

  private int qualityRuleSelectionCompare(boolean equal) {
    Logger.instance.trace("SubtitleSelection", "qualityRuleSelectionCompare", "equal: " + equal);
    List<Subtitle> matchingSubs = videoFile.getMatchingSubs();
    Subtitle subtitle;
    for (String quality : settings.getQualityRuleList()) {
      Logger.instance.trace("SubtitleSelection", "qualityRuleSelectionCompare",
          "Quality Rule checked: " + quality);
      for (int i = 0; i < matchingSubs.size(); i++) {
        subtitle = matchingSubs.get(i);
        Logger.instance.debug("qualityRuleSelectionCompare: subtitle quality: "
            + subtitle.getQuality());
        Logger.instance.trace("SubtitleSelection", "qualityRuleSelectionCompare",
            "subtitle quality: " + subtitle.getQuality());
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
