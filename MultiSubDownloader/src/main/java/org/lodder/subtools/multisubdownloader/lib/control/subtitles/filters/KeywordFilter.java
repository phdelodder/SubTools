package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.ArrayList;
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
    public List<Subtitle> doFilter(Release release, List<Subtitle> Subtitles) {
        List<Subtitle> filteredList = new ArrayList<>();
        String keywordsFile = ReleaseParser.getQualityKeyword(getReleasename(release));

        for (Subtitle subtitle : Subtitles) {
            if (subtitle.getQuality().isEmpty()) {
                subtitle.setQuality(ReleaseParser.getQualityKeyword(subtitle.getFilename()));
            }

            boolean checkKeywordMatch = checkKeywordSubtitleMatch(subtitle, keywordsFile);

            if (checkKeywordMatch) {
                LOGGER.debug("getSubtitlesFiltered: found KEYWORD match [{}] ", subtitle.getFilename());

                subtitle.setSubtitleMatchType(SubtitleMatchType.KEYWORD);

                filteredList.add(subtitle);
            }
        }

        return filteredList;
    }

}
