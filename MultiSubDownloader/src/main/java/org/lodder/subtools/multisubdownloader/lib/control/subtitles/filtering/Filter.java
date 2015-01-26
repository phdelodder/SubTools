package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filtering;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;

public class Filter {

  private Settings settings;

  public Filter(Settings settings) {
    this.settings = settings;
  }

  public List<Subtitle> getFiltered(List<Subtitle> listFoundSubtitles, Release release,
      boolean includeEverytingIfNoResults) {
    Logger.instance.trace("filter", "filter", "");

    boolean foundExactMatch = false;
    boolean foundKeywordMatch = false;
    List<Subtitle> listFilteredSubtitles;
    listFilteredSubtitles = new ArrayList<Subtitle>();

    if (settings.isOptionSubtitleExcludeHearingImpaired()) {
      Iterator<Subtitle> i = listFoundSubtitles.iterator();
      while (i.hasNext()) {
        Subtitle sub = i.next();
        if (sub.isHearingImpaired()) i.remove();
      }
    }

    String subRequest = " ";
    if (!(release.getFilename() == null)) {
      subRequest = release.getFilename().toLowerCase();
      subRequest = subRequest.replace("." + release.getExtension(), "");
    }

    if (settings.isOptionSubtitleExactMatch()) {
      Pattern p = Pattern.compile(subRequest.replaceAll(" ", "[. ]"), Pattern.CASE_INSENSITIVE);

      for (Subtitle subtitle : listFoundSubtitles) {
        Matcher m = p.matcher(subtitle.getFilename().toLowerCase().replace(".srt", ""));
        if (m.matches()) {
          subtitle.setSubtitleMatchType(SubtitleMatchType.EXACT);
          subtitle.setQuality(ReleaseParser.getQualityKeyword(subtitle.getFilename()));
          Logger.instance.debug("getSubtitlesFiltered: found EXACT match: "
              + subtitle.getFilename());
          addToFoundSubtitleList(listFilteredSubtitles, subtitle);
          foundExactMatch = true;
        }
      }
    }

    if (settings.isOptionSubtitleKeywordMatch()) {
      // check keywords
      String keywordsFile = ReleaseParser.getQualityKeyword(subRequest);

      for (Subtitle subtitle : listFoundSubtitles) {
        boolean checkKeywordMatch = checkKeywordSubtitleMatch(subtitle, keywordsFile);

        if (checkKeywordMatch) {
          subtitle.setSubtitleMatchType(SubtitleMatchType.KEYWORD);
          subtitle.setQuality(ReleaseParser.getQualityKeyword(subtitle.getFilename()));
          Logger.instance.debug("getSubtitlesFiltered: found KEYWORD match: "
              + subtitle.getFilename());
          addToFoundSubtitleList(listFilteredSubtitles, subtitle);
          foundKeywordMatch = true;
        }

        // check team match, use contains since some other info migth be
        // present!
        // Always check for team since some sites only give the team!
        if (!checkKeywordMatch
            && subtitle.getTeam().toLowerCase().contains(release.getReleasegroup().toLowerCase())) {
          subtitle.setSubtitleMatchType(SubtitleMatchType.TEAM);
          Logger.instance.debug("getSubtitlesFiltered: found KEYWORD based TEAM match: "
              + subtitle.getFilename());
          addToFoundSubtitleList(listFilteredSubtitles, subtitle);
          foundKeywordMatch = true;
        }
      }

      if (!foundKeywordMatch && release.getPath() != null && release.getFilename() != null) {
        // check keywords based on filesize if no keywords found in
        // file.
        keywordsFile =
            ReleaseParser.getQualityKeyword(release.getPath().getAbsolutePath()
                + release.getFilename());
        if (keywordsFile.equalsIgnoreCase("")) {
          long size = (new File(release.getPath(), release.getFilename())).length() / 1024 / 1024;
          if (size < 400) {
            keywordsFile = "dvdrip xvid hdtv";
          } else if (size < 1200) {
            keywordsFile = "720p hdtv";
          } else if (size < 1600) {
            keywordsFile = "web dl";
          }
        }

        for (Subtitle subtitle : listFoundSubtitles) {
          boolean checkKeywordMatch = checkKeywordSubtitleMatch(subtitle, keywordsFile);

          if (checkKeywordMatch) {
            subtitle.setSubtitleMatchType(SubtitleMatchType.KEYWORD);
            subtitle.setQuality(ReleaseParser.getQualityKeyword(subtitle.getFilename()));
            Logger.instance.debug("getSubtitlesFiltered: found KEYWORD based FILESIZE match: "
                + subtitle.getFilename());
            addToFoundSubtitleList(listFilteredSubtitles, subtitle);
            foundKeywordMatch = true;
          }
        }
      }
    }

    if (!foundKeywordMatch && !foundExactMatch && includeEverytingIfNoResults) {
      for (Subtitle subtitle : listFoundSubtitles) {
        subtitle.setSubtitleMatchType(SubtitleMatchType.EVERYTHING);
        subtitle.setQuality(ReleaseParser.getQualityKeyword(subtitle.getFilename()));
        Logger.instance.debug("getSubtitlesFiltered: found EVERYTHING match: "
            + subtitle.getFilename());
        addToFoundSubtitleList(listFilteredSubtitles, subtitle);
      }
    }
    return listFilteredSubtitles;
  }

  private void addToFoundSubtitleList(List<Subtitle> listFilteredSubtitles, Subtitle subtitle) {
    for (Subtitle sub : listFilteredSubtitles) {
      if (sub.getDownloadlink().equals(subtitle.getDownloadlink())) return;
    }
    listFilteredSubtitles.add(subtitle);
  }

  private boolean checkKeywordSubtitleMatch(Subtitle subtitle, String keywordsFile) {
    String keywordsSub = ReleaseParser.getQualityKeyword(subtitle.getFilename());

    boolean foundKeywordMatch = false;
    if (keywordsFile.equalsIgnoreCase(keywordsSub)) {
      foundKeywordMatch = true;
    } else {
      foundKeywordMatch = keywordCheck(keywordsFile, keywordsSub);
    }
    return foundKeywordMatch;
  }

  private boolean keywordCheck(String videoFileName, String subFileName) {
    Logger.instance.trace("SubtitleControl", "keywordCheck", "");
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
    } else if (videoFileName.contains("dvdrip") && subFileName.contains("dvdrip")) {
      foundKeywordMatch = true;
    }
    return foundKeywordMatch;
  }
}
