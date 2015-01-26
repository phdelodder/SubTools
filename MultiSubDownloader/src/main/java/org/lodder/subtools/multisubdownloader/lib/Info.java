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
    Logger.instance.log(" - OptionSubtitleExactMatch : " + settings.isOptionSubtitleExactMatch());
    Logger.instance.log(" - OptionSubtitleKeywordMatch : "
        + settings.isOptionSubtitleKeywordMatch());
    Logger.instance.log(" - OptionSubtitleExcludeHearingImpaired : "
        + settings.isOptionSubtitleExcludeHearingImpaired());
    Logger.instance.log("-------------------------------");
  }

  public static void downloadOptions(Settings settings) {
    Logger.instance.log("----- Download Options ------");
    Logger.instance.log(" - OptionsAlwaysConfirm : " + settings.isOptionsAlwaysConfirm());
    Logger.instance.log("-----------------------------");
  }
}
