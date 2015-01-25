package org.lodder.subtools.multisubdownloader.lib;

import org.lodder.subtools.multisubdownloader.settings.model.SearchSubtitlePriority;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;

public class Info {

  public static void subtitleSources(Settings settings) {
    Logger.instance.log("----- Subtitle Sources ------");
    for (SearchSubtitlePriority prio : settings.getListSearchSubtitlePriority()) {
      Logger.instance.log(" - Source : " + prio.getSubtitleSource().toString() + " , Prio : " + prio.getPriority());
    }
    Logger.instance.log("-----------------------------");
  }

  public static void subtitleFiltering(Settings settings) {
    Logger.instance.log("----- Subtitle Filtering ------");
    Logger.instance.log(" - OptionSubtitleExcludeHearingImpaired : "
        + settings.isOptionSubtitleExcludeHearingImpaired());
    Logger.instance.log("-------------------------------");
  }

  public static void downloadOptions(Settings settings) {
    Logger.instance.log("----- Download Options ------");
    Logger.instance.log(" - OptionsAlwaysConfirm : " + settings.isOptionsAlwaysConfirm());
    Logger.instance.log(" - OptionsAutomaticDownloadSelection : "
        + settings.isOptionsAutomaticDownloadSelection());
    Logger.instance.log(" - OptionsAutomaticDownloadSelectionQuality : "
        + settings.isOptionsAutomaticDownloadSelectionQuality());
    Logger.instance.log(" - QualityRuleList : " + settings.getQualityRuleList());
    for (String q : settings.getQualityRuleList()) {
      Logger.instance.log("    - Rule : " + q);
    }
    Logger.instance.log(" - OptionsAutomaticDownloadSelectionReleaseGroup : "
        + settings.isOptionsAutomaticDownloadSelectionReleaseGroup());
    Logger.instance.log(" - OptionsNoRuleMatchTakeFirst : "
        + settings.isOptionsNoRuleMatchTakeFirst());
    Logger.instance.log("-----------------------------");
  }
}
