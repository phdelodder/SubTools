package org.lodder.subtools.multisubdownloader.lib;

import lombok.experimental.UtilityClass;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("java:S106")
@UtilityClass
public class Info {

    private static final Logger LOGGER = LoggerFactory.getLogger(Info.class);

    public static void subtitleSources(Settings settings, boolean isCli) {
        if (isCli) {
            System.out.println("----- Subtitle Providers ------");
        } else {
            LOGGER.info("----- Subtitle Providers ------");
        }
        for (SubtitleSource source : SubtitleSource.values()) {
            boolean enabled = switch (source) {
                case ADDIC7ED -> settings.serieSourceAddic7ed;
                case LOCAL -> settings.serieSourceLocal;
                case OPENSUBTITLES -> settings.serieSourceOpensubtitles;
                case PODNAPISI -> settings.serieSourcePodnapisi;
                case TVSUBTITLES -> settings.serieSourceTvSubtitles;
                case SUBSCENE -> settings.serieSourceSubscene;
            };
            if (isCli) {
                System.out.println(" - provider : " + source + " enabled: " + enabled);
            } else {
                LOGGER.info(" - provider : " + source + " enabled: " + enabled);
            }
        }
        if (isCli) {
            System.out.println("-----------------------------");
        } else {
            LOGGER.info("-----------------------------");
        }
    }

    public static void subtitleFiltering(Settings settings, boolean isCli) {
        if (isCli) {
            System.out.println("----- Subtitle Filtering ------");
            System.out.println(" - OptionSubtitleExactMatch : " + settings.optionSubtitleExactMatch);
            System.out.println(" - OptionSubtitleKeywordMatch : " + settings.optionSubtitleKeywordMatch);
            System.out.println(
                    " - OptionSubtitleExcludeHearingImpaired : " + settings.optionSubtitleExcludeHearingImpaired);
            System.out.println("-------------------------------");
        } else {
            LOGGER.info("----- Subtitle Filtering ------");
            LOGGER.info(" - OptionSubtitleExactMatch: {} ", settings.optionSubtitleExactMatch);
            LOGGER.info(" - OptionSubtitleKeywordMatch: {} ", settings.optionSubtitleKeywordMatch);
            LOGGER.info(" - OptionSubtitleExcludeHearingImpaired: {} ", settings.optionSubtitleExcludeHearingImpaired);
            LOGGER.info("-------------------------------");
        }

    }

    public static void downloadOptions(Settings settings, boolean isCli) {
        if (isCli) {
            System.out.println("----- Download Options ------");
            System.out.println(" - OptionsAlwaysConfirm : " + settings.optionsAlwaysConfirm);
            System.out.println("-----------------------------");
        } else {
            LOGGER.info("----- Download Options ------");
            LOGGER.info(" - OptionsAlwaysConfirm : " + settings.optionsAlwaysConfirm);
            LOGGER.info("-----------------------------");
        }

    }
}
