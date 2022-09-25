package org.lodder.subtools.multisubdownloader.actions;

import java.util.Collections;
import java.util.List;

import org.lodder.subtools.multisubdownloader.lib.SubtitleSelection;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.SubtitleComparator;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubtitleSelectionAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubtitleSelectionAction.class);

    private SubtitleSelection subtitleSelection;
    private final Settings settings;

    public SubtitleSelectionAction(Settings settings) {
        this.settings = settings;
    }

    public void setSubtitleSelection(SubtitleSelection subtitleSelection) {
        this.subtitleSelection = subtitleSelection;
    }

    /**
     * @param release
     * @param subtitleSelectionDialog
     * @return integer which subtitle is selected for downloading
     */
    public int subtitleSelection(final Release release, final boolean subtitleSelectionDialog) {
        return this.subtitleSelection(release, subtitleSelectionDialog, false);
    }

    /**
     * @param release
     * @param subtitleSelectionDialog
     * @param dryRun
     * @return integer which subtitle is selected for downloading
     */
    public int subtitleSelection(Release release, final boolean subtitleSelectionDialog, final boolean dryRun) {

        // Sort subtitles by score
        Collections.sort(release.getMatchingSubs(), new SubtitleComparator());
        if (dryRun) {
            if (release.getMatchingSubs().size() > 0) {
                subtitleSelection.dryRunOutput(release);
            }
        } else {
            if (release.getMatchingSubs().size() > 0) {
                LOGGER.debug("determineWhatSubtitleDownload for videoFile: [{}] # found subs: [{}]",
                        release.getFileName(), release.getMatchingSubs().size());
                if (settings.isOptionsAlwaysConfirm()) {
                    return subtitleSelection.getUserInput(release);
                } else if (release.getMatchingSubs().size() == 1
                        && release.getMatchingSubs().get(0).getSubtitleMatchType() == SubtitleMatchType.EXACT) {
                    LOGGER.debug("determineWhatSubtitleDownload: Exact Match");
                    return 0;
                } else if (release.getMatchingSubs().size() > 1) {
                    LOGGER.debug("determineWhatSubtitleDownload: Multiple subs detected");

                    // Automatic selection
                    List<Subtitle> shortlist =
                            subtitleSelection.getAutomaticSelection(release.getMatchingSubs());
                    shortlist.forEach(release::addMatchingSubs);
                    // automatic selection results in 1 result
                    if (shortlist.size() == 1) {
                        return 0;
                    }
                    // nothing match the minimum automatic selection value
                    if (shortlist.size() == 0) {
                        return -1;
                    }

                    // still more then 1 subtitle, let the user decide!
                    if (subtitleSelectionDialog) {
                        LOGGER.debug("determineWhatSubtitleDownload: Select subtitle with dialog");
                        return subtitleSelection.getUserInput(release);
                    } else {
                        LOGGER.info(
                                "Multiple subs detected for: [{}] Unhandleable for CMD! switch to GUI or use '--selection' as switch in de CMD",
                                release.getFileName());
                    }
                } else if (release.getMatchingSubs().size() == 1) {
                    LOGGER.debug("determineWhatSubtitleDownload: only one sub taking it!!!!");
                    return 0;
                }
            }
            LOGGER.debug("determineWhatSubtitleDownload: No subs found for  [{}]", release.getFileName());
        }
        return -1;
    }
}
