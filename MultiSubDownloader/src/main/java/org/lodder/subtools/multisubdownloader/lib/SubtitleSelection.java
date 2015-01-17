package org.lodder.subtools.multisubdownloader.lib;

import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.VideoFile;

public abstract class SubtitleSelection {

  private Settings settings;

  public SubtitleSelection(Settings settings) {
    this.setSettings(settings);
  }

  public int getAutomatic(VideoFile videoFile) {
    Logger.instance.debug("getAutomaticSubtitleSelection: # quality rules: "
        + getSettings().getQualityRuleList().size());
    Logger.instance.debug("getAutomaticSubtitleSelection: quality rules: "
        + getSettings().getQualityRuleList().toString());

    int result = -1;

    if (getSettings().isOptionsAutomaticDownloadSelectionQuality()) {
      result = qualityRuleSelectionCompare(true, videoFile);
      if (result > -1) return result;

      result = qualityRuleSelectionCompare(false, videoFile);
      if (result > -1) return result;
    }

    if (getSettings().isOptionsAutomaticDownloadSelectionReleaseGroup()) {
      result = teamCompare(true, videoFile);
      if (result > -1) return result;

      result = teamCompare(false, videoFile);
      if (result > -1) return result;
    }

    if (getSettings().isOptionsNoRuleMatchTakeFirst()) {
      Logger.instance.debug("getAutomaticSubtitleSelection: Using taking first rule");
      return 0;
    } else {
      Logger.instance.debug("getAutomaticSubtitleSelection: Nothing found");
      return -1;
    }
  }

  private int teamCompare(boolean equal, VideoFile videoFile) {
    Logger.instance.trace("SubtitleSelection", "teamCompare", "equal: " + equal);
    List<Subtitle> matchingSubs = videoFile.getFilteredSubs();
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
          if (videoFile.getTeam().toLowerCase().contains(t.toLowerCase())) return i;
        }
      }
    }

    return -1;
  }

  private int qualityRuleSelectionCompare(boolean equal, VideoFile videoFile) {
    Logger.instance.trace("SubtitleSelection", "qualityRuleSelectionCompare", "equal: " + equal);
    List<Subtitle> matchingSubs = videoFile.getFilteredSubs();
    Subtitle subtitle;
    for (String quality : getSettings().getQualityRuleList()) {
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
  
  protected abstract int getUserInput(VideoFile videoFile);

  public Settings getSettings() {
    return settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }
}
