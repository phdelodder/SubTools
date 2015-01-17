package org.lodder.subtools.multisubdownloader.gui.workers;

import java.util.ArrayList;
import org.lodder.subtools.multisubdownloader.lib.SubtitleSelectionCLI;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.VideoFile;

public class SearchWorkerFilter {

  private Settings settings;

  public SearchWorkerFilter(Settings settings) {
    this.settings = settings;
  }

  private void automaticSelectionFilter(VideoFile videoFile) {
    Logger.instance.debug("getSubtitlesFiltered: Automatic download selection detected.");
    SubtitleSelectionCLI subtitleSelection = new SubtitleSelectionCLI(settings, videoFile);

    int index = subtitleSelection.getAutomatic();

    if (index >= 0) {
      Logger.instance.debug("getSubtitlesFiltered: Automatic selection made. index: " + index);
      Subtitle subtitle = videoFile.getFilteredSubs().get(index);

      // empty the filtered list
      videoFile.getFilteredSubs().clear();

      videoFile.getFilteredSubs().add(subtitle);
    } else {
      // If no selection could be made, just empty the list
      videoFile.getFilteredSubs().clear();
    }
  }

  public void filter(VideoFile videoFile) {
    if (videoFile.getMatchingSubs().size() <= 0)
      return;

    // We start by allowing all subtitles
    videoFile.setFilteredSubs(new ArrayList<Subtitle>(videoFile.getMatchingSubs()));

    if (settings.isOptionsAutomaticDownloadSelection())
      automaticSelectionFilter(videoFile);
  }
}
