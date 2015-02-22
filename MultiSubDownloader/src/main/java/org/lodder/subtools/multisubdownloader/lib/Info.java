package org.lodder.subtools.multisubdownloader.lib;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;

public class Info {

  public static void subtitleSources(Settings settings) {
    Logger.instance.log("----- Subtitle Providers ------");
    for (SubtitleSource source : SubtitleSource.values()) {
      boolean enabled = false;
      switch (source){
        case ADDIC7ED:
          enabled = settings.isSerieSourceAddic7ed();
          break;
        case LOCAL:
          enabled = settings.isSerieSourceLocal();
          break;
        case OPENSUBTITLES:
          enabled = settings.isSerieSourceOpensubtitles();
          break;
        case PODNAPISI:
          enabled = settings.isSerieSourcePodnapisi();
          break;
        case SUBSMAX:
          enabled = settings.isSerieSourceSubsMax();
          break;
        case TVSUBTITLES:
          enabled = settings.isSerieSourceTvSubtitles();
          break;
        default:
          break;
        
      }
      Logger.instance.log(" - provider : " + source.toString() + " enabled: " + enabled);
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
