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
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TvReleaseControl extends ReleaseControl {

    private final JTheTVDBAdapter jtvdba;
    private final JTVRageAdapter tvra;

    private static final Logger LOGGER = LoggerFactory.getLogger(TvReleaseControl.class);

    public TvReleaseControl(TvRelease tvRelease, Settings settings, Manager manager) {
        super(tvRelease, settings, manager);
        jtvdba = JTheTVDBAdapter.getAdapter(manager);
        tvra = new JTVRageAdapter(manager);
    }

    public void processTvdb(List<MappingTvdbScene> dict) throws ReleaseControlException {
        setTvdbID(dict);
        TvRelease tvRelease = (TvRelease) release;
        if (tvRelease.getTvdbId() > 0) {
            TheTVDBEpisode thetvdbepisode = jtvdba.getEpisode(tvRelease);
            if (thetvdbepisode != null) {
                tvRelease.updateTvdbEpisodeInfo(thetvdbepisode);
            } else {
                throw new ReleaseControlException("Season " + tvRelease.getSeason()
                        + " Episode " + tvRelease.getEpisodeNumbers().toString() + "not found, check file", release);
            }
        } else {
            throw new ReleaseControlException("Show not found, check file", release);
        }
    }

    public void processTVRage() throws ReleaseControlException {
        setTvrageID();
        TvRelease tvRelease = (TvRelease) release;
        TVRageEpisode tvrEpisode = tvra.getEpisodeInfo(tvRelease.getTvrageId(), tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0));
        if (tvrEpisode != null) {
            tvRelease.updateTVRageEpisodeInfo(tvrEpisode);
        } else {
            throw new ReleaseControlException("Season " + tvRelease.getSeason() + " Episode "
                    + tvRelease.getEpisodeNumbers().toString() + "not found, check file", release);
        }
    }

    @Override
    public void process(List<MappingTvdbScene> dict) throws ReleaseControlException {
        TvRelease tvRelease = (TvRelease) release;
        // return episodeFile;
        if ("".equals(tvRelease.getShowName())) {
            throw new ReleaseControlException("Unable to extract episode details, check file", release);
        } else {
            LOGGER.debug("process: showname [{}], season [{}], episode [{}]", tvRelease.getShowName(),
                    tvRelease.getSeason(), tvRelease.getEpisodeNumbers());

            if (tvRelease.isSpecial()) {
                processSpecial(dict);
            } else {
                if (SettingsProcessEpisodeSource.TVRAGE.equals(settings.getProcessEpisodeSource())) {
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
        TvRelease tvRelease = (TvRelease) release;
        if (tvRelease.getTvrageId() > 0) {
            tvrEpisode = tvra.getEpisodeInfo(tvRelease.getTvrageId(), tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0));
            if (tvrEpisode != null && settings.getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVRAGE) {
                tvRelease.updateTVRageEpisodeInfo(tvrEpisode);
            }
        }
        setTvdbID(dict);
        if (tvRelease.getTvdbId() > 0) {
            thetvdbepisode = jtvdba.getEpisode(tvRelease);
            if (thetvdbepisode != null && settings.getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVDB) {
                tvRelease.updateTvdbEpisodeInfo(thetvdbepisode);
            }
        }
    }

    private void setTvdbID(List<MappingTvdbScene> dict) throws ReleaseControlException {
        TvRelease tvRelease = (TvRelease) release;
        int tvdbid = dict.stream()
                .filter(mapping -> mapping.getSceneName().replaceAll("[^A-Za-z]", "")
                        .equalsIgnoreCase(tvRelease.getShowName().replaceAll("[^A-Za-z]", "")))
                .map(MappingTvdbScene::getTvdbId)
                .findAny().orElse(0);

        TheTVDBSerie thetvdbserie = tvdbid == 0 ? jtvdba.getSerie(tvRelease) : jtvdba.getSerie(tvdbid);

        if (thetvdbserie == null) {
            throw new ReleaseControlException("Tvdb API, returned no result", release);
        }
        tvRelease.setOriginalShowName(thetvdbserie.getSerieName());
        tvRelease.setTvdbId(Integer.parseInt(thetvdbserie.getId()));
    }

    private void setTvrageID() {
        TVRageShowInfo tvrShowInfo = tvra.searchShow((TvRelease) release);
        if (tvrShowInfo != null) {
            ((TvRelease) release).setTvrageId(tvrShowInfo.getShowId());
        }
    }
}
