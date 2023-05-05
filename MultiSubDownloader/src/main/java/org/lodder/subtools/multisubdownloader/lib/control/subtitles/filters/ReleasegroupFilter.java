package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ StringUtils.class })
public class ReleasegroupFilter extends Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleasegroupFilter.class);

    @Override
    public List<Subtitle> doFilter(Release release, List<Subtitle> subtitles) {
        return subtitles.stream()
                .peek(subtitle -> {
                    if (subtitle.getReleaseGroup().isEmpty()) {
                        subtitle.setReleaseGroup(ReleaseParser.extractReleasegroup(subtitle.getFileName(), subtitle.getFileName().endsWith(".srt")));
                    }
                })
                .filter(subtitle -> subtitle.getReleaseGroup().containsIgnoreCase(release.getReleaseGroup())
                        || release.getReleaseGroup().containsIgnoreCase(subtitle.getReleaseGroup()))
                .peek(subtitle -> LOGGER.debug("getSubtitlesFiltered: found KEYWORD based TEAM match [{}] ", subtitle.getFileName()))
                .peek(subtitle -> subtitle.setSubtitleMatchType(SubtitleMatchType.TEAM))
                .toList();
    }

}
