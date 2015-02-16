package org.lodder.subtools.multisubdownloader.listeners;

import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.model.Release;

public interface SearchProgressListener extends StatusListener {

  public void progress(SubtitleProvider provider, int jobsLeft, Release release);

  public void progress(int progress);

  public void completed();
}
