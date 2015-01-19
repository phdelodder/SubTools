package org.lodder.subtools.multisubdownloader.lib.control;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.lib.JTVRageAdapter;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBEpisode;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageShowInfo;
import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public class EpisodeFileControl extends VideoFileControl {

  private JTheTVDBAdapter jtvdba;
  private final JTVRageAdapter tvra;

  public EpisodeFileControl(EpisodeFile episodeFile, Settings settings) {
    super(episodeFile, settings);
    jtvdba = JTheTVDBAdapter.getAdapter();
    tvra = new JTVRageAdapter();
  }

  public void processTvdb(List<MappingTvdbScene> dict) throws VideoControlException {
    setTvdbID(dict);
    if (((EpisodeFile) release).getTvdbid() > 0) {
      TheTVDBEpisode thetvdbepisode = jtvdba.getEpisode(((EpisodeFile) release));
      if (thetvdbepisode != null) {
        ((EpisodeFile) release).updateTvdbEpisodeInfo(thetvdbepisode);
      } else {
        throw new VideoControlException("Season " + ((EpisodeFile) release).getSeason()
            + " Episode " + ((EpisodeFile) release).getEpisodeNumbers().toString()
            + "not found, check file", release);
      }
    } else {
      throw new VideoControlException("Show not found, check file", release);
    }
  }

  public void processTVRage() throws VideoControlException {
    setTvrageID();
    TVRageEpisode tvrEpisode =
        tvra.getEpisodeInfo(((EpisodeFile) release).getTvrageid(), ((EpisodeFile) release)
            .getSeason(), ((EpisodeFile) release).getEpisodeNumbers().get(0));
    if (tvrEpisode != null) {
      ((EpisodeFile) release).updateTVRageEpisodeInfo(tvrEpisode);
    } else {
      throw new VideoControlException("Season " + ((EpisodeFile) release).getSeason()
          + " Episode " + ((EpisodeFile) release).getEpisodeNumbers().toString()
          + "not found, check file", release);
    }
  }

  public void process(List<MappingTvdbScene> dict) throws VideoControlException {
    Logger.instance.trace("EpisodeFileControl", "process", "");

    EpisodeFile episodeFile = (EpisodeFile) release;
    // return episodeFile;
    if (episodeFile.getShow().equals("")) {
      throw new VideoControlException("Unable to extract episode details, check file", release);
    } else {
      Logger.instance.debug("Showname: " + episodeFile.getShow());
      Logger.instance.debug("Season: " + episodeFile.getSeason());
      Logger.instance.debug("Episode: " + episodeFile.getEpisodeNumbers());

      if (episodeFile.isSpecial()) {
        processSpecial(dict);
      } else {
        if (settings.getProcessEpisodeSource().equals(SettingsProcessEpisodeSource.TVRAGE)) {
          processTVRage();
        }
        processTvdb(dict);
      }
    }
  }

  /**
   * @param dict
   * 
   */
  private void processSpecial(List<MappingTvdbScene> dict) {
    TVRageEpisode tvrEpisode = null;
    TheTVDBEpisode thetvdbepisode = null;
    setTvrageID();
    if (((EpisodeFile) release).getTvrageid() > 0) {
      tvrEpisode =
          tvra.getEpisodeInfo(((EpisodeFile) release).getTvrageid(), ((EpisodeFile) release)
              .getSeason(), ((EpisodeFile) release).getEpisodeNumbers().get(0));
      if (tvrEpisode != null
          & settings.getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVRAGE)
        ((EpisodeFile) release).updateTVRageEpisodeInfo(tvrEpisode);
    }
    setTvdbID(dict);
    if (((EpisodeFile) release).getTvdbid() > 0) {
      thetvdbepisode = jtvdba.getEpisode(((EpisodeFile) release));
      if (thetvdbepisode != null
          & settings.getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVDB)
        ((EpisodeFile) release).updateTvdbEpisodeInfo(thetvdbepisode);
    }
  }

  public void processWithSubtitles(List<MappingTvdbScene> dict, String languageCode)
      throws VideoControlException {
    Logger.instance.trace("EpisodeFileControl", "processWithSubtitles", "");
    process(dict);
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    listFoundSubtitles.addAll(sc.getSubtitles((EpisodeFile) release, languageCode));
    release.setMatchingSubs(listFoundSubtitles);
  }

  private void setTvdbID(List<MappingTvdbScene> dict) {
    int tvdbid = 0;
    for (MappingTvdbScene mapping : dict) {
      if (mapping.getSceneName().replaceAll("[^A-Za-z]", "")
          .equalsIgnoreCase(((EpisodeFile) release).getShow().replaceAll("[^A-Za-z]", ""))) {
        tvdbid = mapping.getTvdbId();
      }
    }

    TheTVDBSerie thetvdbserie = null;
    if (tvdbid == 0) {
      thetvdbserie = jtvdba.getSerie((EpisodeFile) release);
    } else {
      thetvdbserie = jtvdba.getSerie(tvdbid);
    }

    ((EpisodeFile) release).setOriginalShowName(thetvdbserie.getSerieName());
    ((EpisodeFile) release).setTvdbid(Integer.parseInt(thetvdbserie.getId()));
  }

  private void setTvrageID() {
    TVRageShowInfo tvrShowInfo = tvra.searchShow((EpisodeFile) release);
    if (tvrShowInfo != null) ((EpisodeFile) release).setTvrageid(tvrShowInfo.getShowID());
  }
}
