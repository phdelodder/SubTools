package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.Arrays;

import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public abstract class SubtitleFilter {

    public abstract boolean useSubtitle(Release release, Subtitle subtitle);

    public boolean excludeSubtitle(Release release, Subtitle subtitle){
        return !useSubtitle(release, subtitle);
    }

    protected String getReleaseName(Release release) {
        return release.getFileName() == null ? "" : release.getFileName().toLowerCase().replace("." + release.getExtension(), "");
    }

    protected boolean checkKeywordSubtitleMatch(Subtitle subtitle, String keywordsFile) {
        String keywordsSub = ReleaseParser.getQualityKeyword(subtitle.getFileName());
        return keywordsFile.equalsIgnoreCase(keywordsSub) || keywordCheck(keywordsFile, keywordsSub);
    }

    private boolean keywordCheck(String videoFileName, String subFileName) {
        String videoFileNameLower = videoFileName.toLowerCase();
        String subFileNameLower = subFileName.toLowerCase();

        return containsBoth(videoFileNameLower, subFileNameLower, "dl", "720p", "web")
                || containsBoth(videoFileNameLower, subFileNameLower, "720p", "web")
                || containsBoth(videoFileNameLower, subFileNameLower, "dl", "1080p", "web")
                || containsBoth(videoFileNameLower, subFileNameLower, "1080p", "web")
                || containsBoth(videoFileNameLower, subFileNameLower, "dl", "web")
                || containsBoth(videoFileNameLower, subFileNameLower, "hdtv", "720p")
                || containsBoth(videoFileNameLower, subFileNameLower, "1080p", "bluray")
                || containsBoth(videoFileNameLower, subFileNameLower, "720p", "bluray")
                || containsBoth(videoFileNameLower, subFileNameLower, "x264", "bluray")
                || containsBoth(videoFileNameLower, subFileNameLower, "x265", "bluray")
                || containsBoth(videoFileNameLower, subFileNameLower, "xvid", "dvdrip")
                || containsBoth(videoFileNameLower, subFileNameLower, "xvid", "hdtv")
                || containsBoth(videoFileNameLower, subFileNameLower, "720p", "brrip", "xvid")
                || containsBoth(videoFileNameLower, subFileNameLower, "ts", "xvid")
                || containsBoth(videoFileNameLower, subFileNameLower, "bdrip", "xvid")
                || containsBoth(videoFileNameLower, subFileNameLower, "480p", "brrip", "xvid")
                || containsBoth(videoFileNameLower, subFileNameLower, "720p", "brrip", "x264")
                || containsBoth(videoFileNameLower, subFileNameLower, "720p", "brrip", "x265")
                || containsBoth(videoFileNameLower, subFileNameLower, "dvdscreener", "xvid")
                || containsBoth(videoFileNameLower, subFileNameLower, "r5", "xvid")
                || containsBoth(videoFileNameLower, subFileNameLower, "cam", "xvid")
                || containsBoth(videoFileNameLower, subFileNameLower, "hdtv", "x264")
                || containsBoth(videoFileNameLower, subFileNameLower, "hdtv", "x265")
                || containsBoth(videoFileNameLower, subFileNameLower, "dvdrip");
    }

    private boolean containsBoth(String string1, String string2, String... values) {
        return Arrays.stream(values).allMatch(string1::contains) && Arrays.stream(values).allMatch(string2::contains);
    }
}
