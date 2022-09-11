package org.lodder.subtools.multisubdownloader.lib.control.subtitles;

import static java.util.function.Predicate.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final Filter exactname;
    private final Filter keyword;
    private final Filter releasegroup;

    private static final Logger LOGGER = LoggerFactory.getLogger(Filtering.class);

    public Filtering(Settings settings) {
        this.settings = settings;
        exactname = new ExactNameFilter();
        keyword = new KeywordFilter();
        releasegroup = new ReleasegroupFilter();
    }

    public List<Subtitle> getFiltered(List<Subtitle> listFoundSubtitles, Release release) {
        LOGGER.trace("getFiltered: release [{}] available subtitles [{}]", release, listFoundSubtitles);

        List<Subtitle> listFilteredSubtitles = new ArrayList<>();

        List<Subtitle> subtitles = listFoundSubtitles;
        if (settings.isOptionSubtitleExcludeHearingImpaired()) {
            subtitles = subtitles.stream().filter(not(Subtitle::isHearingImpaired)).collect(Collectors.toList());
        }

        if (settings.isOptionSubtitleKeywordMatch()) {
            listFilteredSubtitles = keyword.doFilter(release, subtitles);
            if (listFilteredSubtitles.size() > 0) {
                subtitles = listFilteredSubtitles;
            }
            listFilteredSubtitles = releasegroup.doFilter(release, subtitles);
            if (listFilteredSubtitles.size() > 0) {
                subtitles = listFilteredSubtitles;
            }
        }

        if (settings.isOptionSubtitleExactMatch()) {
            listFilteredSubtitles = exactname.doFilter(release, subtitles);
            if (listFilteredSubtitles.size() > 0) {
                subtitles = listFilteredSubtitles;
            }
        }

        return subtitles;
    }

}
