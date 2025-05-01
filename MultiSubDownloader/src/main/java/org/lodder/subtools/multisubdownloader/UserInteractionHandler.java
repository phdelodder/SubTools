package org.lodder.subtools.multisubdownloader;

import java.util.List;

import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public interface UserInteractionHandler extends org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler {

    default List<Subtitle> getAutomaticSelection(List<Subtitle> subtitles) {
        List<Subtitle> shortlist = !settings.optionsMinAutomaticSelection ? subtitles :
                subtitles.stream()
                        .filter(subtitle -> subtitle.score >= settings.optionsMinAutomaticSelectionValue)
                        .toList();
        if (settings.optionsDefaultSelection) {
            List<Subtitle> defaultSelectionsFound = settings.optionsDefaultSelectionQualityList.stream()
                    .flatMap(q -> shortlist.stream().filter(subtitle -> q.isTypeForValue(subtitle.quality)))
                    .distinct().toList();
            if (!defaultSelectionsFound.isEmpty()) {
                return defaultSelectionsFound;
            }
        }
        return shortlist;
    }

    List<Subtitle> selectSubtitles(Release release);

    void dryRunOutput(Release release);

}
