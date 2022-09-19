package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.List;

import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public abstract class Filter {

    public abstract List<Subtitle> doFilter(Release release, List<Subtitle> Subtitles);

    protected String getReleasename(Release release) {
        String name = "";

        if (!(release.getFileName() == null)) {
            name = release.getFileName().toLowerCase();
            name = name.replace("." + release.getExtension(), "");
        }

        return name;
    }

    protected boolean checkKeywordSubtitleMatch(Subtitle subtitle, String keywordsFile) {
        String keywordsSub = ReleaseParser.getQualityKeyword(subtitle.getFileName());

        boolean foundKeywordMatch = false;
        if (keywordsFile.equalsIgnoreCase(keywordsSub)) {
            foundKeywordMatch = true;
        } else {
            foundKeywordMatch = keywordCheck(keywordsFile, keywordsSub);
        }
        return foundKeywordMatch;
    }

    private boolean keywordCheck(String videoFileName, String subFileName) {
        boolean foundKeywordMatch = false;

        videoFileName = videoFileName.toLowerCase();
        subFileName = subFileName.toLowerCase();

        if (videoFileName.contains("dl") && subFileName.contains("dl")
                && videoFileName.contains("720p") && subFileName.contains("720p")
                && videoFileName.contains("web") && subFileName.contains("web")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("720p") && subFileName.contains("720p")
                && videoFileName.contains("web") && subFileName.contains("web")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("dl") && subFileName.contains("dl")
                && videoFileName.contains("1080p") && subFileName.contains("1080p")
                && videoFileName.contains("web") && subFileName.contains("web")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("1080p") && subFileName.contains("1080p")
                && videoFileName.contains("web") && subFileName.contains("web")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("dl") && subFileName.contains("dl")
                && videoFileName.contains("web") && subFileName.contains("web")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("hdtv") && subFileName.contains("hdtv")
                && videoFileName.contains("720p") && subFileName.contains("720p")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("1080p") && subFileName.contains("1080p")
                && videoFileName.contains("bluray") && subFileName.contains("bluray")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("720p") && subFileName.contains("720p")
                && videoFileName.contains("bluray") && subFileName.contains("bluray")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("x264") && subFileName.contains("x264")
                && videoFileName.contains("bluray") && subFileName.contains("bluray")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("x265") && subFileName.contains("x265")
                && videoFileName.contains("bluray") && subFileName.contains("bluray")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("xvid") && subFileName.contains("xvid")
                && videoFileName.contains("dvdrip") && subFileName.contains("dvdrip")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("xvid") && subFileName.contains("xvid")
                && videoFileName.contains("hdtv") && subFileName.contains("hdtv")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("720p") && subFileName.contains("720p")
                && videoFileName.contains("brrip") && subFileName.contains("brrip")
                && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("ts") && subFileName.contains("ts")
                && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("bdrip") && subFileName.contains("bdrip")
                && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("480p") && subFileName.contains("480p")
                && videoFileName.contains("brrip") && subFileName.contains("brrip")
                && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("720p") && subFileName.contains("720p")
                && videoFileName.contains("brrip") && subFileName.contains("brrip")
                && videoFileName.contains("x264") && subFileName.contains("x264")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("720p") && subFileName.contains("720p")
                && videoFileName.contains("brrip") && subFileName.contains("brrip")
                && videoFileName.contains("x265") && subFileName.contains("x265")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("dvdscreener") && subFileName.contains("dvdscreener")
                && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("r5") && subFileName.contains("r5")
                && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("cam") && subFileName.contains("cam")
                && videoFileName.contains("xvid") && subFileName.contains("xvid")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("hdtv") && subFileName.contains("hdtv")
                && videoFileName.contains("x264") && subFileName.contains("x264")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("hdtv") && subFileName.contains("hdtv")
                && videoFileName.contains("x265") && subFileName.contains("x265")) {
            foundKeywordMatch = true;
        } else if (videoFileName.contains("dvdrip") && subFileName.contains("dvdrip")) {
            foundKeywordMatch = true;
        }
        return foundKeywordMatch;
    }
}
