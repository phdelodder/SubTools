package org.lodder.subtools.multisubdownloader.lib;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.JAddic7edApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.model.Addic7edSubtitleDescriptor;
import org.lodder.subtools.sublibrary.JSubAdapter;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.*;
import org.lodder.subtools.sublibrary.util.StringUtils;

public class JAddic7edAdapter implements JSubAdapter, SubtitleProvider{

  private static JAddic7edApi jaapi;

  public JAddic7edAdapter(boolean isLoginEnabled, String username, String password) {
    try {
      if (jaapi == null) {
        if (isLoginEnabled) {
          jaapi = new JAddic7edApi(username, password);
        } else {
          jaapi = new JAddic7edApi();
        }
      }
    } catch (Exception e) {
      Logger.instance.error("API JAddic7ed INIT: " + e.getCause());
    }
  }

  @Override
  public String getName() {
    return "Addic7ed";
  }

  @Override
  public List<Subtitle> search(Release release, String languageCode) {
    if (release instanceof MovieRelease) {
      return this.searchSubtitles((MovieRelease) release, languageCode);
    } else {
      return this.searchSubtitles((TvRelease) release, languageCode);
    }
  }

  @Override
  public List<Subtitle> searchSubtitles(TvRelease tvRelease, String... sublanguageids) {
    List<Addic7edSubtitleDescriptor> lSubtitles = new ArrayList<Addic7edSubtitleDescriptor>();
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    try {
      String showName = "";

      if (tvRelease.getShow().length() > 0) {
        showName = jaapi.searchSerieName(tvRelease.getShow());
      }

      if (showName.length() == 0) {
        if (tvRelease.getOriginalShowName().length() > 0) {
          showName = jaapi.searchSerieName(tvRelease.getOriginalShowName());
        }
      }

      lSubtitles.addAll(jaapi.searchSubtitles(showName, tvRelease.getSeason(), tvRelease
          .getEpisodeNumbers().get(0), tvRelease.getTitle()));

    } catch (Exception e) {
      Logger.instance.error("API JAddic7ed searchSubtitles using title: " + e);
    }
    for (Addic7edSubtitleDescriptor sub : lSubtitles) {
      if (sub.getLanguage().equals("Dutch")) sub.setLanguage("nl");
      if (sub.getLanguage().equals("English")) sub.setLanguage("en");
      if (sublanguageids[0].equals(sub.getLanguage())) {
        listFoundSubtitles.add(new Subtitle(Subtitle.SubtitleSource.ADDIC7ED, StringUtils
            .removeIllegalFilenameChars(sub.getTitel() + " " + sub.getVersion()), sub.getUrl(), sub
            .getLanguage(), sub.getVersion(), SubtitleMatchType.EVERYTHING, sub.getVersion(), sub
            .getUploader(), sub.isHearingImpaired()));
      }
    }
    return listFoundSubtitles;
  }

  @Override
  public List<Subtitle> searchSubtitles(MovieRelease movieRelease, String... sublanguageids) {
    // TODO Auto-generated method stub
    return null;
  }

}
