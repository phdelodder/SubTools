package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.List;

import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeywordFilter extends Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeywordFilter.class);

    @Override
    public List<Subtitle> doFilter(Release release, List<Subtitle> subtitles) {
        String keywordsFile = ReleaseParser.getQualityKeyword(getReleaseName(release));

        return subtitles.stream()
                .peek(subtitle -> {
                    if (subtitle.getQuality().isEmpty()) {
                        subtitle.setQuality(ReleaseParser.getQualityKeyword(subtitle.getFileName()));
                    }
                })
                .filter(subtitle -> checkKeywordSubtitleMatch(subtitle, keywordsFile))
                .peek(subtitle -> LOGGER.debug("getSubtitlesFiltered: found KEYWORD match [{}] ", subtitle.getFileName()))
                .peek(subtitle -> subtitle.setSubtitleMatchType(SubtitleMatchType.KEYWORD))
                .toList();
    }

}
