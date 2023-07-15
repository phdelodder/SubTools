package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeywordFilter extends SubtitleFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeywordFilter.class);


    @Override
    public boolean useSubtitle(Release release, Subtitle subtitle) {
        String keywordsFile = ReleaseParser.getQualityKeyword(getReleaseName(release));

        if (subtitle.getQuality().isEmpty()) {
            subtitle.setQuality(ReleaseParser.getQualityKeyword(subtitle.getFileName()));
        }
        if(!checkKeywordSubtitleMatch(subtitle, keywordsFile)){
            return false;
        }
         LOGGER.debug("getSubtitlesFiltered: found KEYWORD match [{}] ", subtitle.getFileName());
         subtitle.setSubtitleMatchType(SubtitleMatchType.KEYWORD);
         return true;
    }

}
