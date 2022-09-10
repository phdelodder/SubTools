package org.lodder.subtools.multisubdownloader.workers;

import java.util.List;

import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public interface SearchHandler {
    void onFound(Release release, List<Subtitle> subtitles);
}
