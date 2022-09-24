package org.lodder.subtools.multisubdownloader.lib.control.subtitles.sorting.replacers;

import java.util.Map;

import org.lodder.subtools.sublibrary.model.Release;

public interface KeywordReplacer {
    void replace(Release release, Map<String, Integer> weights);
}
