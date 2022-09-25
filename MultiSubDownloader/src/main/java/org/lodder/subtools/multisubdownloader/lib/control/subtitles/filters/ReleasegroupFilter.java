package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
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
                subtitle.setReleaseGroup(ReleaseParser.extractReleasegroup(subtitle.getFileName(),
                        FilenameUtils.isExtension(subtitle.getFileName(), "srt")));
            }

            if (subtitle.getReleaseGroup().toLowerCase()
                    .contains(release.getReleaseGroup().toLowerCase())
                    || release.getReleaseGroup().toLowerCase()
                            .contains(subtitle.getReleaseGroup().toLowerCase())) {
                LOGGER.debug("getSubtitlesFiltered: found KEYWORD based TEAM match [{}] ",
                        subtitle.getFileName());

                subtitle.setSubtitleMatchType(SubtitleMatchType.TEAM);

                filteredList.add(subtitle);
            }
        }

        return filteredList;
    }

}
