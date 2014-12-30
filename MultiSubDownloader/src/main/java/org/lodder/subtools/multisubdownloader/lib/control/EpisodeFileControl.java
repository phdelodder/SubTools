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
    if (((EpisodeFile) videoFile).getTvdbid() > 0) {
      TheTVDBEpisode thetvdbepisode = jtvdba.getEpisode(((EpisodeFile) videoFile));
      if (thetvdbepisode != null) {
        ((EpisodeFile) videoFile).updateTvdbEpisodeInfo(thetvdbepisode);
      } else {
        throw new VideoControlException("Season " + ((EpisodeFile) videoFile).getSeason()
            + " Episode " + ((EpisodeFile) videoFile).getEpisodeNumbers().toString()
            + "not found, check file", videoFile);
      }
    } else {
      throw new VideoControlException("Show not found, check file", videoFile);
    }
  }

  public void processTVRage() throws VideoControlException {
    setTvrageID();
    TVRageEpisode tvrEpisode =
        tvra.getEpisodeInfo(((EpisodeFile) videoFile).getTvrageid(), ((EpisodeFile) videoFile)
            .getSeason(), ((EpisodeFile) videoFile).getEpisodeNumbers().get(0));
    if (tvrEpisode != null) {
      ((EpisodeFile) videoFile).updateTVRageEpisodeInfo(tvrEpisode);
    } else {
      throw new VideoControlException("Season " + ((EpisodeFile) videoFile).getSeason()
          + " Episode " + ((EpisodeFile) videoFile).getEpisodeNumbers().toString()
          + "not found, check file", videoFile);
    }
  }

  public void process(List<MappingTvdbScene> dict) throws VideoControlException {
    Logger.instance.trace("EpisodeFileControl", "process", "");

    EpisodeFile episodeFile = (EpisodeFile) videoFile;
    // return episodeFile;
    if (episodeFile.getShow().equals("")) {
      throw new VideoControlException("Unable to extract episode details, check file", videoFile);
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
    if (((EpisodeFile) videoFile).getTvrageid() > 0) {
      tvrEpisode =
          tvra.getEpisodeInfo(((EpisodeFile) videoFile).getTvrageid(), ((EpisodeFile) videoFile)
              .getSeason(), ((EpisodeFile) videoFile).getEpisodeNumbers().get(0));
      if (tvrEpisode != null
          & settings.getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVRAGE)
        ((EpisodeFile) videoFile).updateTVRageEpisodeInfo(tvrEpisode);
    }
    setTvdbID(dict);
    if (((EpisodeFile) videoFile).getTvdbid() > 0) {
      thetvdbepisode = jtvdba.getEpisode(((EpisodeFile) videoFile));
      if (thetvdbepisode != null
          & settings.getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVDB)
        ((EpisodeFile) videoFile).updateTvdbEpisodeInfo(thetvdbepisode);
    }
  }

  public void processWithSubtitles(List<MappingTvdbScene> dict, String languageCode)
      throws VideoControlException {
    Logger.instance.trace("EpisodeFileControl", "processWithSubtitles", "");
    process(dict);
    List<Subtitle> listFoundSubtitles = new ArrayList<Subtitle>();
    listFoundSubtitles.addAll(sc.getSubtitles((EpisodeFile) videoFile, languageCode));
    videoFile.setMatchingSubs(listFoundSubtitles);
  }

  private void setTvdbID(List<MappingTvdbScene> dict) {
    int tvdbid = 0;
    for (MappingTvdbScene mapping : dict) {
      if (mapping.getSceneName().replaceAll("[^A-Za-z]", "")
          .equalsIgnoreCase(((EpisodeFile) videoFile).getShow().replaceAll("[^A-Za-z]", ""))) {
        tvdbid = mapping.getTvdbId();
      }
    }

    TheTVDBSerie thetvdbserie = null;
    if (tvdbid == 0) {
      thetvdbserie = jtvdba.getSerie((EpisodeFile) videoFile);
    } else {
      thetvdbserie = jtvdba.getSerie(tvdbid);
    }

    ((EpisodeFile) videoFile).setOriginalShowName(thetvdbserie.getSerieName());
    ((EpisodeFile) videoFile).setTvdbid(Integer.parseInt(thetvdbserie.getId()));
  }

  private void setTvrageID() {
    TVRageShowInfo tvrShowInfo = tvra.searchShow((EpisodeFile) videoFile);
    if (tvrShowInfo != null) ((EpisodeFile) videoFile).setTvrageid(tvrShowInfo.getShowID());
  }
}
