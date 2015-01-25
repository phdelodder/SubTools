package org.lodder.subtools.multisubdownloader.lib;

import java.io.File;

import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.VideoFileParseException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoType;

public class SubtitleMatchingScore {

  private ReleaseParser parser;

  public SubtitleMatchingScore() {
    parser = new ReleaseParser();
  }

  public void calculateMax(Release release) {
    int score = 0;
    
    score += calculateBasicScore(release, release);
    score += calculateTagsScore(release, release);
    score += calculateReleaseGroup(release, release);
    
    release.setMaxScore(score);
  }

  public void calculate(Release release) {
    for (Subtitle subtitle : release.getSubtitles()) {
      int score = 0;
      try {
        Release subtitleRelease = parser.parse(new File(subtitle.getFilename()), new File(""));

        score += calculateBasicScore(release, subtitleRelease);
        score += calculateTagsScore(release, subtitleRelease);
        score += calculateReleaseGroup(release, subtitleRelease);

      } catch (VideoFileParseException e) {
        Logger.instance.error("Unable to parse subtitle filename, no basic score available");
      }

      subtitle.setScore(score);
    }
  }

  private int calculateReleaseGroup(Release release, Release subtitleRelease) {
    int score = 0;

    if (release.getReleasegroup().equalsIgnoreCase(subtitleRelease.getReleasegroup())) score += 8;

    return score;
  }

  private int calculateTagsScore(Release release, Release subtitleRelease) {
    int score = 0;

    for (String tag : release.getTags()) {
      for (String subtitleTag : subtitleRelease.getTags()) {
        if (tag.equalsIgnoreCase(subtitleTag)) score += 2;
      }
    }

    return score;
  }

  private int calculateBasicScore(Release release, Release subtitleRelease) {
    int score = 0;

    if (release.getVideoType() == VideoType.EPISODE
        && subtitleRelease.getVideoType() == VideoType.EPISODE) {
      TvRelease tvRelease = (TvRelease) release;
      TvRelease tvSubtitleRelease = (TvRelease) subtitleRelease;
      if (tvRelease.getShow().equalsIgnoreCase(tvSubtitleRelease.getShow())) score += 1;
      if (tvRelease.getSeason() == tvSubtitleRelease.getSeason()) score += 1;
      if (tvRelease.getEpisodeNumbers().containsAll(tvSubtitleRelease.getEpisodeNumbers()))
        score += 1;
    } else if (release.getVideoType() == VideoType.MOVIE
        && subtitleRelease.getVideoType() == VideoType.MOVIE) {
      MovieRelease movieRelease = (MovieRelease) release;
      MovieRelease movieSubtitleRelease = (MovieRelease) subtitleRelease;
      if (movieRelease.getTitle().equalsIgnoreCase(movieSubtitleRelease.getTitle())) score += 1;
      if (movieRelease.getYear() == movieSubtitleRelease.getYear()) score += 1;
    }

    return score;
  }
}
