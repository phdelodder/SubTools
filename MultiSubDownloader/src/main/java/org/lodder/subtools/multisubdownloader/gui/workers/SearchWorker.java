package org.lodder.subtools.multisubdownloader.gui.workers;

import java.util.ArrayList;
import java.util.List;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class SearchWorker extends Thread {

  protected final SubtitleProvider provider;
  private final SearchManager scheduler;
  private boolean busy = false;

  public SearchWorker(SubtitleProvider provider, SearchManager scheduler) {
    this.provider = provider;
    this.scheduler = scheduler;
  }

  @Override
  public void run() {
    String language = this.scheduler.getLanguage();
    this.busy = false;
    while (!this.isInterrupted()) {
      this.busy = true;
      Release release = this.scheduler.getNextRelease(provider.getName());
      if (release == null) {
        this.busy = false;
        break;
      }
      Logger.instance.debug("[Search] " + this.provider.getName() + " searching " + release.toString());

      List<Subtitle> subtitles = this.provider.search(release, language);
      if (subtitles == null) {
        subtitles = new ArrayList<>();
      }

      /* clone to prevent other threads from ever messing with it */
      subtitles = new ArrayList<>(subtitles);

      this.busy = false;
      Logger.instance.debug("[Search] " + this.provider.getName() + " found " + subtitles.size() + " subtitles for " + release.toString());

      if (!this.isInterrupted()) {
        this.scheduler.onCompleted(release, subtitles);
      }
    }
  }

  public boolean isBusy() {
    return busy;
  }
}
