package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReleasegroupFilter extends Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleasegroupFilter.class);

    @Override
    public List<Subtitle> doFilter(Release release, List<Subtitle> Subtitles) {
        List<Subtitle> filteredList = new ArrayList<>();

        for (Subtitle subtitle : Subtitles) {
            // make sure the release is filled up!
            if (subtitle.getReleaseGroup().isEmpty()) {
                subtitle.setReleaseGroup(ReleaseParser.extractReleasegroup(subtitle.getFileName(), subtitle.getFileName().endsWith(".srt")));
            }

            if ((release.getReleaseGroup() != null && StringUtils.containsIgnoreCase(subtitle.getReleaseGroup(), release.getReleaseGroup()))
                    || (subtitle.getReleaseGroup() != null
                            && StringUtils.containsIgnoreCase(release.getReleaseGroup(), subtitle.getReleaseGroup()))) {
                LOGGER.debug("getSubtitlesFiltered: found KEYWORD based TEAM match [{}] ", subtitle.getFileName());

                subtitle.setSubtitleMatchType(SubtitleMatchType.TEAM);

                filteredList.add(subtitle);
            }
        }

        return filteredList;
    }

}
