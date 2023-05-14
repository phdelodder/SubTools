package org.lodder.subtools.multisubdownloader.lib.control.subtitles;

import static java.util.function.Predicate.*;

import java.util.List;

import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.ExactNameFilter;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.Filter;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.KeywordFilter;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters.ReleasegroupFilter;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Filtering {

    private final Settings settings;
    private final Filter exactName;
    private final Filter keyword;
    private final Filter releaseGroup;

    private static final Logger LOGGER = LoggerFactory.getLogger(Filtering.class);

    public Filtering(Settings settings) {
        this.settings = settings;
        this.exactName = new ExactNameFilter();
        this.keyword = new KeywordFilter();
        this.releaseGroup = new ReleasegroupFilter();
    }

    public List<Subtitle> getFiltered(List<Subtitle> foundSubtitles, Release release) {
        LOGGER.trace("getFiltered: release [{}] available subtitles [{}]", release, foundSubtitles);

        List<Subtitle> filteredSubtitles;

        List<Subtitle> subtitles = foundSubtitles;
        if (settings.isOptionSubtitleExcludeHearingImpaired()) {
            subtitles = subtitles.stream().filter(not(Subtitle::isHearingImpaired)).toList();
        }

        if (settings.isOptionSubtitleKeywordMatch()) {
            filteredSubtitles = keyword.doFilter(release, subtitles);
            if (!filteredSubtitles.isEmpty()) {
                subtitles = filteredSubtitles;
            }
            filteredSubtitles = releaseGroup.doFilter(release, subtitles);
            if (!filteredSubtitles.isEmpty()) {
                subtitles = filteredSubtitles;
            }
        }

        if (settings.isOptionSubtitleExactMatch()) {
            filteredSubtitles = exactName.doFilter(release, subtitles);
            if (!filteredSubtitles.isEmpty()) {
                subtitles = filteredSubtitles;
            }
        }

        return subtitles;
    }

}
