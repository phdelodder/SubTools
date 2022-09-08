package org.lodder.subtools.multisubdownloader.subtitleproviders;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;

public interface SubtitleProvider {

    List<Subtitle> searchSubtitles(TvRelease tvRelease, String languageId);

    List<Subtitle> searchSubtitles(MovieRelease movieRelease, String languageId);

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
    default List<Subtitle> search(Release release, String languageCode) {
        if (release instanceof MovieRelease movieRelease) {
            return this.searchSubtitles(movieRelease, languageCode);
        } else if (release instanceof TvRelease tvRelease) {
            return this.searchSubtitles(tvRelease, languageCode);
        }
        return new ArrayList<>();
    }
}
