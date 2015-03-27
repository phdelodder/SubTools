package org.lodder.subtools.multisubdownloader.lib.control.subtitles.filters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReleasegroupFilter extends Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReleasegroupFilter.class);

  @Override
  public List<Subtitle> doFilter(Release release, List<Subtitle> Subtitles) {
    List<Subtitle> filteredList = new ArrayList<Subtitle>();

    for (Subtitle subtitle : Subtitles) {
      // make sure the release is filled up!
      if (subtitle.getReleasegroup().isEmpty())
        subtitle.setReleasegroup(ReleaseParser.extractReleasegroup(subtitle.getFilename(),
            FilenameUtils.isExtension(subtitle.getFilename(), "srt")));

      if (subtitle.getReleasegroup().toLowerCase()
          .contains(release.getReleasegroup().toLowerCase())
          || release.getReleasegroup().toLowerCase()
              .contains(subtitle.getReleasegroup().toLowerCase())) {
        LOGGER.debug("getSubtitlesFiltered: found KEYWORD based TEAM match [{}] ",
            subtitle.getFilename());

        subtitle.setSubtitleMatchType(SubtitleMatchType.TEAM);

        filteredList.add(subtitle);
      }
    }

    return filteredList;
  }

}
