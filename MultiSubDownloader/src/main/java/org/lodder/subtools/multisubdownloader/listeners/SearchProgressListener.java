package org.lodder.subtools.multisubdownloader.listeners;

import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.model.Release;

public interface SearchProgressListener extends StatusListener {

    void progress(SubtitleProvider provider, int jobsLeft, Release release);

    void progress(int progress);

    void completed();

    void reset();
}
