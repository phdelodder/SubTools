package org.lodder.subtools.multisubdownloader.subtitleproviders;

import java.util.List;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public interface SubtitleProvider {

    /**
     * @return The name of the SubtitleProvider
     */
    public abstract String getName();

    /**
     * Starts a search for subtitles
     *
     * @param release The release being searched for
     * @param languageCode The language of the desired subtitles
     * @return The found subtitles
     */
    public List<Subtitle> search(Release release, String languageCode);
}
