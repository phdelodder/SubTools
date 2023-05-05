package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.List;
import java.util.regex.Pattern;

import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExactNameFilter extends Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExactNameFilter.class);

    @Override
    public List<Subtitle> doFilter(Release release, List<Subtitle> subtitles) {
        Pattern p = Pattern.compile(getReleasename(release).replace(" ", "[. ]"), Pattern.CASE_INSENSITIVE);
        return subtitles.stream()
                .filter(subtitle -> p.matcher(subtitle.getFileName().toLowerCase().replace(".srt", "")).matches())
                .peek(subtitle -> LOGGER.debug("getSubtitlesFiltered: found EXACT match [{}] ", subtitle.getFileName()))
                .peek(subtitle -> subtitle.setSubtitleMatchType(SubtitleMatchType.EXACT))
                .toList();
    }
}
