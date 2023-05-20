package org.lodder.subtools.multisubdownloader.actions;

import java.util.List;

import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.SubtitleComparator;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserInteractionHandlerAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInteractionHandlerAction.class);

    private final Settings settings;
    private final UserInteractionHandler userInteractionHandler;

    /**
     * @param release
     * @param subtitleSelectionDialog
     * @return integer which subtitle is selected for downloading
     */
    public List<Subtitle> subtitleSelection(final Release release, final boolean subtitleSelectionDialog) {
        return this.subtitleSelection(release, subtitleSelectionDialog, false);
    }

    /**
     * @param release
     * @param subtitleSelectionDialog
     * @param dryRun
     * @return integer which subtitle is selected for downloading
     */
    public List<Subtitle> subtitleSelection(Release release, final boolean subtitleSelectionDialog, final boolean dryRun) {

        // Sort subtitles by score
        release.getMatchingSubs().sort(new SubtitleComparator());
        if (dryRun) {
            if (!release.getMatchingSubs().isEmpty()) {
                userInteractionHandler.dryRunOutput(release);
            }
        } else {
            if (!release.getMatchingSubs().isEmpty()) {
                LOGGER.debug("determineWhatSubtitleDownload for videoFile: [{}] # found subs: [{}]",
                        release.getFileName(), release.getMatchingSubs().size());
                if (settings.isOptionsAlwaysConfirm()) {
                    return userInteractionHandler.selectSubtitles(release);
                } else if (release.getMatchingSubs().size() == 1
                        && release.getMatchingSubs().get(0).getSubtitleMatchType() == SubtitleMatchType.EXACT) {
                    LOGGER.debug("determineWhatSubtitleDownload: Exact Match");
                    return List.of(release.getMatchingSubs().get(0));
                } else if (release.getMatchingSubs().size() > 1) {
                    LOGGER.debug("determineWhatSubtitleDownload: Multiple subs detected");

                    // Automatic selection
                    List<Subtitle> shortlist = userInteractionHandler.getAutomaticSelection(release.getMatchingSubs());
                    shortlist.forEach(release::addMatchingSub);
                    // automatic selection results in 1 result
                    if (shortlist.size() == 1) {
                        return List.of(release.getMatchingSubs().get(0));
                    }
                    // nothing match the minimum automatic selection value
                    if (shortlist.isEmpty()) {
                        return List.of();
                    }

                    // still more than 1 subtitle, let the user decide!
                    if (subtitleSelectionDialog) {
                        LOGGER.debug("determineWhatSubtitleDownload: Select subtitle with dialog");
                        return userInteractionHandler.selectSubtitles(release);
                    } else {
                        LOGGER.info("Multiple subs detected for: [{}] Unhandleable for CMD! switch to GUI or use '--selection' as switch in de CMD",
                                release.getFileName());
                    }
                } else if (release.getMatchingSubs().size() == 1) {
                    LOGGER.debug("determineWhatSubtitleDownload: only one sub taking it!!!!");
                    return List.of(release.getMatchingSubs().get(0));
                }
            }
            LOGGER.debug("determineWhatSubtitleDownload: No subs found for  [{}]", release.getFileName());
        }
        return List.of();
    }
}
