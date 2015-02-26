package org.lodder.subtools.multisubdownloader.lib.control;

import java.util.List;

import org.lodder.subtools.multisubdownloader.lib.JTVRageAdapter;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBEpisode;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageShowInfo;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public class TvReleaseControl extends ReleaseControl {

  private JTheTVDBAdapter jtvdba;
  private final JTVRageAdapter tvra;

  public TvReleaseControl(TvRelease tvRelease, Settings settings, Manager manager) {
    super(tvRelease, settings, manager);
    jtvdba = JTheTVDBAdapter.getAdapter(manager);
    tvra = new JTVRageAdapter(manager);
  }

  public void processTvdb(List<MappingTvdbScene> dict) throws ReleaseControlException {
    setTvdbID(dict);
    if (((TvRelease) release).getTvdbid() > 0) {
      TheTVDBEpisode thetvdbepisode = jtvdba.getEpisode(((TvRelease) release));
      if (thetvdbepisode != null) {
        ((TvRelease) release).updateTvdbEpisodeInfo(thetvdbepisode);
      } else {
        throw new ReleaseControlException("Season " + ((TvRelease) release).getSeason()
            + " Episode " + ((TvRelease) release).getEpisodeNumbers().toString()
            + "not found, check file", release);
      }
    } else {
      throw new ReleaseControlException("Show not found, check file", release);
    }
  }

  public void processTVRage() throws ReleaseControlException {
    setTvrageID();
    TVRageEpisode tvrEpisode =
        tvra.getEpisodeInfo(((TvRelease) release).getTvrageid(), ((TvRelease) release).getSeason(),
            ((TvRelease) release).getEpisodeNumbers().get(0));
    if (tvrEpisode != null) {
      ((TvRelease) release).updateTVRageEpisodeInfo(tvrEpisode);
    } else {
      throw new ReleaseControlException("Season " + ((TvRelease) release).getSeason() + " Episode "
          + ((TvRelease) release).getEpisodeNumbers().toString() + "not found, check file", release);
    }
  }

  public void process(List<MappingTvdbScene> dict) throws ReleaseControlException {
    Logger.instance.trace("EpisodeFileControl", "process", "");

    TvRelease tvRelease = (TvRelease) release;
    // return episodeFile;
    if (tvRelease.getShow().equals("")) {
      throw new ReleaseControlException("Unable to extract episode details, check file", release);
    } else {
      Logger.instance.debug("Showname: " + tvRelease.getShow());
      Logger.instance.debug("Season: " + tvRelease.getSeason());
      Logger.instance.debug("Episode: " + tvRelease.getEpisodeNumbers());

      if (tvRelease.isSpecial()) {
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
   * @throws ReleaseControlException
   * 
   */
  private void processSpecial(List<MappingTvdbScene> dict) throws ReleaseControlException {
    TVRageEpisode tvrEpisode = null;
    TheTVDBEpisode thetvdbepisode = null;
    setTvrageID();
    if (((TvRelease) release).getTvrageid() > 0) {
      tvrEpisode =
          tvra.getEpisodeInfo(((TvRelease) release).getTvrageid(),
              ((TvRelease) release).getSeason(), ((TvRelease) release).getEpisodeNumbers().get(0));
      if (tvrEpisode != null
          && settings.getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVRAGE)
        ((TvRelease) release).updateTVRageEpisodeInfo(tvrEpisode);
    }
    setTvdbID(dict);
    if (((TvRelease) release).getTvdbid() > 0) {
      thetvdbepisode = jtvdba.getEpisode(((TvRelease) release));
      if (thetvdbepisode != null
          && settings.getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVDB)
        ((TvRelease) release).updateTvdbEpisodeInfo(thetvdbepisode);
    }
  }

  private void setTvdbID(List<MappingTvdbScene> dict) throws ReleaseControlException {
    int tvdbid = 0;
    for (MappingTvdbScene mapping : dict) {
      if (mapping.getSceneName().replaceAll("[^A-Za-z]", "")
          .equalsIgnoreCase(((TvRelease) release).getShow().replaceAll("[^A-Za-z]", ""))) {
        tvdbid = mapping.getTvdbId();
      }
    }

    TheTVDBSerie thetvdbserie = null;
    if (tvdbid == 0) {
      thetvdbserie = jtvdba.getSerie((TvRelease) release);
    } else {
      thetvdbserie = jtvdba.getSerie(tvdbid);
    }

    if (thetvdbserie == null)
      throw new ReleaseControlException("Tvdb API, returned no result", release);
    ((TvRelease) release).setOriginalShowName(thetvdbserie.getSerieName());
    ((TvRelease) release).setTvdbid(Integer.parseInt(thetvdbserie.getId()));
  }

  private void setTvrageID() {
    TVRageShowInfo tvrShowInfo = tvra.searchShow((TvRelease) release);
    if (tvrShowInfo != null) ((TvRelease) release).setTvrageid(tvrShowInfo.getShowID());
  }
}
