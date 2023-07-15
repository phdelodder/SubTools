package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.Map;
import java.util.regex.Pattern;

import org.lodder.subtools.sublibrary.cache.LRUMap;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExactNameFilter extends SubtitleFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExactNameFilter.class);

    private final Map<String, Pattern> patterns = new LRUMap<>(10);

    @Override
    public boolean useSubtitle(Release release, Subtitle subtitle) {
        Pattern p = patterns.computeIfAbsent(getReleaseName(release), k ->
                Pattern.compile(getReleaseName(release).replace(" ", "[. ]"), Pattern.CASE_INSENSITIVE));
        if (p.matcher(subtitle.getFileName().toLowerCase().replace(".srt", "")).matches()) {
            LOGGER.debug("getSubtitlesFiltered: found EXACT match [{}] ", subtitle.getFileName());
            subtitle.setSubtitleMatchType(SubtitleMatchType.EXACT);
            return true;
        }
        return false;
    }
}
