package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReleaseGroupFilter extends SubtitleFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseGroupFilter.class);

    @Override
    public boolean useSubtitle(Release release, Subtitle subtitle) {
        if (StringUtils.isEmpty(subtitle.releaseGroup)) {
            subtitle.releaseGroup =
                ReleaseParser.extractReleaseGroup(subtitle.fileName, StringUtils.endsWith(subtitle.fileName, ".srt"));
        }
        if (!StringUtils.containsAnyIgnoreCase(subtitle.releaseGroup, release.releaseGroup, subtitle.releaseGroup)) {
            return false;
        }
        LOGGER.debug("getSubtitlesFiltered: found KEYWORD based TEAM match [{}] ", subtitle.fileName);
        subtitle.subtitleMatchType = SubtitleMatchType.TEAM;
        return true;
    }

}
