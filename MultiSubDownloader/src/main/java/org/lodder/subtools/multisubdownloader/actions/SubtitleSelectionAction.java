package org.lodder.subtools.multisubdownloader.actions;

import java.util.Collections;
import java.util.List;

import org.lodder.subtools.multisubdownloader.lib.SubtitleSelection;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.SubtitleComparator;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;

public class SubtitleSelectionAction {

  private SubtitleSelection subtitleSelection;
  private Settings settings;

  public SubtitleSelectionAction(Settings settings) {
    this.settings = settings;
  }

  public void setSubtitleSelection(SubtitleSelection subtitleSelection) {
    this.subtitleSelection = subtitleSelection;
  }

  public int subtitleSelection(final Release release,
      final boolean subtitleSelectionDialog) {

    // Sort subtitles by score
    Collections.sort(release.getMatchingSubs(), new SubtitleComparator());

    if (release.getMatchingSubs().size() > 0) {
      Logger.instance.debug("determineWhatSubtitleDownload for videoFile: " + release.getFilename()
          + " # found subs: " + release.getMatchingSubs().size());
      if (settings.isOptionsAlwaysConfirm()) {
        return subtitleSelection.getUserInput(release);
      } else if (release.getMatchingSubs().size() == 1
          && release.getMatchingSubs().get(0).getSubtitleMatchType() == SubtitleMatchType.EXACT) {
        Logger.instance.debug("determineWhatSubtitleDownload: Exact Match");
        return 0;
      } else if (release.getMatchingSubs().size() > 1) {
        Logger.instance.debug("determineWhatSubtitleDownload: Multiple subs detected");

        // Automatic selection
        List<Subtitle> shortlist =
            subtitleSelection.getAutomaticSelection(release.getMatchingSubs());
        release.setMatchingSubs(shortlist);
        // automatic selection results in 1 result
        if (shortlist.size() == 1) return 0;
        // nothing match the minimum automatic selection value
        if (shortlist.size() == 0) return -1;

        // still more then 1 subtitle, let the user decide!
        if (subtitleSelectionDialog) {
          Logger.instance.debug("determineWhatSubtitleDownload: Select subtitle with dialog");
          return subtitleSelection.getUserInput(release);
        } else {
          Logger.instance.log("Multiple subs detected for: " + release.getFilename()
              + " Unhandleable for CMD! switch to GUI"
              + " or use '--selection' as switch in de CMD");
        }
      } else if (release.getMatchingSubs().size() == 1) {
        Logger.instance.debug("determineWhatSubtitleDownload: only one sub taking it!!!!");
        return 0;
      }
    }
    Logger.instance.debug("determineWhatSubtitleDownload: No subs found for: "
        + release.getFilename());
    return -1;
  }
}
