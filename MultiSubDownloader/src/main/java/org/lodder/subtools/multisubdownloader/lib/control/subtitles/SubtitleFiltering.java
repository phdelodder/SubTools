package org.lodder.subtools.multisubdownloader.lib.control.subtitles;

import lombok.RequiredArgsConstructor;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.ExactNameFilter;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.KeywordFilter;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.ReleaseGroupFilter;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.SubtitleFilter;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

@RequiredArgsConstructor
public class SubtitleFiltering {

    private final Settings settings;
    private final SubtitleFilter exactName = new ExactNameFilter();
    private final SubtitleFilter keyword = new KeywordFilter();
    private final SubtitleFilter releaseGroup = new ReleaseGroupFilter();

    public boolean useSubtitle(Subtitle subtitle, Release release) {
        return !excludeSubtitle(subtitle, release);
    }

    public boolean excludeSubtitle(Subtitle subtitle, Release release) {
        return excludeSubtitleHearingImpaired(subtitle, release)
               || excludeSubtitleKeywordMatch(subtitle, release)
               || excludeSubtitleExactMatch(subtitle, release);
    }

    private boolean excludeSubtitleHearingImpaired(Subtitle subtitle, Release release) {
        return settings.optionSubtitleExcludeHearingImpaired && subtitle.hearingImpaired;
    }

    private boolean excludeSubtitleKeywordMatch(Subtitle subtitle, Release release) {
        return settings.optionSubtitleKeywordMatch &&
               (keyword.excludeSubtitle(release, subtitle) || releaseGroup.excludeSubtitle(release, subtitle));
    }

    private boolean excludeSubtitleExactMatch(Subtitle subtitle, Release release) {
        return settings.optionSubtitleExactMatch && exactName.excludeSubtitle(release, subtitle);
    }
}
