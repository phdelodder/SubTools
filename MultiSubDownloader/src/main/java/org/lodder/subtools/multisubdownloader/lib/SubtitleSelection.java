package org.lodder.subtools.multisubdownloader.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public abstract class SubtitleSelection {

    private final Settings settings;

    public SubtitleSelection(Settings settings) {
        this.settings = settings;
    }

    public List<Subtitle> getAutomaticSelection(List<Subtitle> subtitles) {
        final List<Subtitle> shortlist;

        if (settings.isOptionsMinAutomaticSelection()) {
            shortlist = subtitles.stream()
                    .filter(subtitle -> subtitle.getScore() >= settings.getOptionsMinAutomaticSelectionValue())
                    .collect(Collectors.toList());
        } else {
            shortlist = new ArrayList<>(subtitles);
        }

        if (settings.isOptionsDefaultSelection()) {
            List<Subtitle> defaultSelectionsFound = settings.getOptionsDefaultSelectionQualityList().stream()
                    .flatMap(q -> shortlist.stream().filter(subtitle -> subtitle.getQuality().toLowerCase().contains(q.toLowerCase())))
                    .distinct()
                    .collect(Collectors.toList());

            if (defaultSelectionsFound.size() > 0) {
                return defaultSelectionsFound;
            }
        }
        return shortlist;
    }

    public abstract int getUserInput(Release release);

    public abstract void dryRunOutput(Release release);
}
