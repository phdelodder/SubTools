package org.lodder.subtools.multisubdownloader.lib;

import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.VideoFile;

public abstract class SubtitleSelection {

  private Settings settings;
  private VideoFile videoFile;

  public SubtitleSelection(Settings settings, VideoFile videoFile) {
    this.settings = settings;
    this.videoFile = videoFile;
  }

  public int getAutomatic() {
    return this.getAutomatic(this.videoFile.getMatchingSubs());
  }

  public int getAutomatic(List<Subtitle> matchingSubs) {
    Logger.instance.debug("getAutomaticSubtitleSelection: # quality rules: "
        + settings.getQualityRuleList().size());
    Logger.instance.debug("getAutomaticSubtitleSelection: quality rules: "
        + settings.getQualityRuleList().toString());

    int result = -1;

    if (settings.isOptionsAutomaticDownloadSelectionQuality()) {
      result = qualityRuleSelectionCompare(matchingSubs, true);
      if (result > -1) return result;

      result = qualityRuleSelectionCompare(matchingSubs, false);
      if (result > -1) return result;
    }

    if (settings.isOptionsAutomaticDownloadSelectionReleaseGroup()) {

      result = teamCompare(matchingSubs,videoFile.getTeam(),true);
      if (result > -1) return result;

      result = teamCompare(matchingSubs,videoFile.getTeam(),false);
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

  private int teamCompare(List<Subtitle> matchingSubs, String team, boolean equal) {
    Logger.instance.trace("SubtitleSelection", "teamCompare", "equal: " + equal);
    String subtitleTeam;
    team = team.toLowerCase();


    Logger.instance.trace("teamCompare", "teamCompare", "videofile team: " + videoFile.getTeam());
    for (int i = 0; i < matchingSubs.size(); i++) {
      subtitleTeam = matchingSubs.get(i).getTeam().toLowerCase();

      Logger.instance.trace("SubtitleSelection", "qualityRuleSelectionCompare", "subtitle team: " + subtitleTeam);


      if (equal) {
        if (subtitleTeam.equals(team)) return i;

      } else {
        for (String t : subtitleTeam.split(" ")) {
          if (team.contains(t)) return i;
        }
      }
    }

    return -1;
  }

  private int qualityRuleSelectionCompare(List<Subtitle> matchingSubs, boolean equal) {
    Logger.instance.trace("SubtitleSelection", "qualityRuleSelectionCompare", "equal: " + equal);

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

  protected abstract int getUserInput(VideoFile videoFile);
}
