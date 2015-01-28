package org.lodder.subtools.multisubdownloader.lib;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.JTVSubtitlesApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model.TVsubtitlesSubtitleDescriptor;
import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;

public class JTVsubtitlesAdapter implements JSubAdapter {

  private static JTVSubtitlesApi jtvapi;

  public JTVsubtitlesAdapter() {
    try {
      if (jtvapi == null) jtvapi = new JTVSubtitlesApi();
    } catch (Exception e) {
      Logger.instance.error("API JTVsubtitles INIT: " + e.getCause());
    }
  }

  @Override
  public List<Subtitle> searchSubtitles(TvRelease tvRelease, String... sublanguageids) {
    List<TVsubtitlesSubtitleDescriptor> lSubtitles = new ArrayList<TVsubtitlesSubtitleDescriptor>();
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    try {
      String showName = "";
      if (tvRelease.getOriginalShowName().length() > 0) {
        showName = tvRelease.getOriginalShowName();
      } else {
        showName = tvRelease.getShow();
      }

      if (showName.length() > 0) {
        if (showName.contains("(") && showName.contains(")")) {
          String alterName = showName.substring(0, showName.indexOf("(") - 1).trim();
          lSubtitles =
              jtvapi.searchSubtitles(alterName, tvRelease.getSeason(), tvRelease
                  .getEpisodeNumbers().get(0), tvRelease.getTitle(), sublanguageids[0]);
        }
        lSubtitles.addAll(jtvapi.searchSubtitles(showName, tvRelease.getSeason(), tvRelease
            .getEpisodeNumbers().get(0), tvRelease.getTitle(), sublanguageids[0]));
      }
    } catch (Exception e) {
      Logger.instance.error("API JTVsubtitles searchSubtitles using title: " + e);
    }
    for (TVsubtitlesSubtitleDescriptor sub : lSubtitles) {
      listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.TVSUBTITLES, sub.Filename,
          sub.Url, sublanguageids[0], sub.Rip, SubtitleMatchType.EVERYTHING, ReleaseParser
              .extractReleasegroup(sub.Filename), sub.Author, false));
    }
    return listFoundSubtitles;
  }

  @Override
  public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String... sublanguageids) {
    // TODO Auto-generated method stub
    return null;
  }

}
