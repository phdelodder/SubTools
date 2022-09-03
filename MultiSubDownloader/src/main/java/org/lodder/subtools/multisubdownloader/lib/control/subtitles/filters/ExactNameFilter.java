package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExactNameFilter extends Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExactNameFilter.class);

    @Override
    public List<Subtitle> doFilter(Release release, List<Subtitle> Subtitles) {
        List<Subtitle> filteredList = new ArrayList<>();
        Pattern p = Pattern.compile(getReleasename(release).replace(" ", "[. ]"), Pattern.CASE_INSENSITIVE);

        for (Subtitle subtitle : Subtitles) {
            Matcher m = p.matcher(subtitle.getFilename().toLowerCase().replace(".srt", ""));
            if (m.matches()) {
                LOGGER.debug("getSubtitlesFiltered: found EXACT match [{}] ", subtitle.getFilename());

                subtitle.setSubtitleMatchType(SubtitleMatchType.EXACT);

                filteredList.add(subtitle);
            }
        }

        return filteredList;
    }
}
