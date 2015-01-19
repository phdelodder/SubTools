package org.lodder.subtools.multisubdownloader.lib;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.MovieFile;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.SubtitleMatchType;
import org.lodder.subtools.sublibrary.subtitlesource.subsmax.JSubsMaxApi;
import org.lodder.subtools.sublibrary.subtitlesource.subsmax.model.SubMaxSubtitleDescriptor;

public class JSubsMaxAdapter implements JSubAdapter {

  private static JSubsMaxApi jsmapi;

  public JSubsMaxAdapter() {
    try {
      if (jsmapi == null) {
        jsmapi = new JSubsMaxApi();
      }
    } catch (Exception e) {
      Logger.instance.error("API JSubsMax INIT: " + e.getCause());
    }
  }

  @Override
  public List<Subtitle> searchSubtitles(TvRelease tvRelease, String... sublanguageids) {
    String showName = "";
    if (tvRelease.getOriginalShowName().length() > 0) {
      showName = tvRelease.getOriginalShowName();
    } else {
      showName = tvRelease.getShow();
    }
    
    List<SubMaxSubtitleDescriptor> lSubtitles =
        jsmapi.searchSubtitles(showName, tvRelease.getSeason(), tvRelease
            .getEpisodeNumbers().get(0), sublanguageids[0]);
    
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    
    for (SubMaxSubtitleDescriptor sub:lSubtitles){
      listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.SUBSMAX, sub.getFilename(),
        sub.getLink(), sublanguageids[0], "", SubtitleMatchType.EVERYTHING, ReleaseParser
            .extractTeam(sub.getFilename()), "", false));
    }
    
    return listFoundSubtitles;
  }

  @Override
  public List<Subtitle> searchSubtitles(MovieFile movieFile, String... sublanguageids) {
    // TODO Auto-generated method stub
    return null;
  }

}
