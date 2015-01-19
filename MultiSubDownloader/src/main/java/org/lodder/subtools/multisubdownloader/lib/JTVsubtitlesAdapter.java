package org.lodder.subtools.multisubdownloader.lib;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.model.MovieFile;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.subtitlesource.tvsubtitles.JTVSubtitlesApi;
import org.lodder.subtools.sublibrary.subtitlesource.tvsubtitles.model.TVsubtitlesSubtitleDescriptor;

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
  public List<Subtitle> searchSubtitles(EpisodeFile episodeFile, String... sublanguageids) {
    List<TVsubtitlesSubtitleDescriptor> lSubtitles = new ArrayList<TVsubtitlesSubtitleDescriptor>();
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    try {
      String showName = "";
      if (episodeFile.getOriginalShowName().length() > 0) {
        showName = episodeFile.getOriginalShowName();
      } else {
        showName = episodeFile.getShow();
      }

      if (showName.length() > 0) {
        if (showName.contains("(") && showName.contains(")")) {
          String alterName = showName.substring(0, showName.indexOf("(") - 1).trim();
          lSubtitles =
              jtvapi.searchSubtitles(alterName, episodeFile.getSeason(), episodeFile
                  .getEpisodeNumbers().get(0), episodeFile.getTitle(), sublanguageids[0]);
        }
        lSubtitles.addAll(jtvapi.searchSubtitles(showName, episodeFile.getSeason(), episodeFile
            .getEpisodeNumbers().get(0), episodeFile.getTitle(), sublanguageids[0]));
      }
    } catch (Exception e) {
      Logger.instance.error("API JTVsubtitles searchSubtitles using title: " + e);
    }
    for (TVsubtitlesSubtitleDescriptor sub : lSubtitles) {
      listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.TVSUBTITLES, sub.Filename,
          sub.Url, sublanguageids[0], sub.Rip, SubtitleMatchType.EVERYTHING, ReleaseParser
              .extractTeam(sub.Filename), sub.Author, false));
    }
    return listFoundSubtitles;
  }

  @Override
  public List<Subtitle> searchSubtitles(MovieFile movieFile, String... sublanguageids) {
    // TODO Auto-generated method stub
    return null;
  }

}
