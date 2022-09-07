package org.lodder.subtools.multisubdownloader.subtitleproviders;

import java.util.List;

import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;

public interface SubtitleProvider {

    SubtitleSource getSubtitleSource();

    /**
     * @return The name of the SubtitleProvider
     */
    default String getName() {
        return getSubtitleSource().getName();
    }

    /**
     * Starts a search for subtitles
     *
     * @param release The release being searched for
     * @param languageCode The language of the desired subtitles
     * @return The found subtitles
     */
    List<Subtitle> search(Release release, String languageCode);
}
