package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KeywordFilter extends SubtitleFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeywordFilter.class);

    @Override
    public boolean useSubtitle(Release release, Subtitle subtitle) {
        String keywordsFile = ReleaseParser.getQualityKeyword(getReleaseName(release));

        if (StringUtils.isEmpty(subtitle.quality)) {
            subtitle.quality = ReleaseParser.getQualityKeyword(subtitle.fileName);
        }
        if (!checkKeywordSubtitleMatch(subtitle, keywordsFile)) {
            return false;
        }
        LOGGER.debug("getSubtitlesFiltered: found KEYWORD match [{}] ", subtitle.fileName);
        subtitle.subtitleMatchType = SubtitleMatchType.KEYWORD;
        return true;
    }

}
