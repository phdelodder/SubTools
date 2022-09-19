package org.lodder.subtools.multisubdownloader.lib.control;

import org.lodder.subtools.multisubdownloader.lib.JTVRageAdapter;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageEpisode;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageShowInfo;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.TvdbMappings;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class TvReleaseControl extends ReleaseControl {

    private final JTheTVDBAdapter jtvdba;
    private final JTVRageAdapter tvra;

    private static final Logger LOGGER = LoggerFactory.getLogger(TvReleaseControl.class);

    public TvReleaseControl(TvRelease tvRelease, Settings settings, Manager manager) {
        super(tvRelease, settings, manager);
        jtvdba = JTheTVDBAdapter.getAdapter(manager);
        tvra = new JTVRageAdapter(manager);
    }

    public void processTvdb(TvdbMappings tvdbMappings) throws ReleaseControlException {
        setTvdbID(tvdbMappings);
        TvRelease tvRelease = (TvRelease) release;
        if (tvRelease.getTvdbId() > 0) {
            jtvdba.getEpisode(tvRelease)
                    .ifPresentOrThrow(tvRelease::updateTvdbEpisodeInfo, () -> new ReleaseControlException(
                            "Season %s Episode %s not found, check file".formatted(tvRelease.getSeason(), tvRelease.getEpisodeNumbers().toString()),
                            release));
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
    public void process(TvdbMappings tvdbMappings) throws ReleaseControlException {
        TvRelease tvRelease = (TvRelease) release;
        // return episodeFile;
        if ("".equals(tvRelease.getName())) {
            throw new ReleaseControlException("Unable to extract episode details, check file", release);
        } else {
            LOGGER.debug("process: showname [{}], season [{}], episode [{}]", tvRelease.getName(),
                    tvRelease.getSeason(), tvRelease.getEpisodeNumbers());

            if (tvRelease.isSpecial()) {
                processSpecial(tvdbMappings);
            } else {
                if (SettingsProcessEpisodeSource.TVRAGE.equals(settings.getProcessEpisodeSource())) {
                    processTVRage();
                }
                processTvdb(tvdbMappings);
            }
        }
    }

    private void processSpecial(TvdbMappings tvdbMappings) throws ReleaseControlException {
        setTvrageID();
        TvRelease tvRelease = (TvRelease) release;
        if (tvRelease.getTvrageId() > 0 && settings.getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVRAGE) {
            TVRageEpisode tvrEpisode = tvra.getEpisodeInfo(tvRelease.getTvrageId(), tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0));
            if (tvrEpisode != null) {
                tvRelease.updateTVRageEpisodeInfo(tvrEpisode);
            }
        }
        setTvdbID(tvdbMappings);
        if (tvRelease.getTvdbId() > 0 && settings.getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVDB) {
            jtvdba.getEpisode(tvRelease).ifPresent(tvRelease::updateTvdbEpisodeInfo);
        }
    }

    private void setTvdbID(TvdbMappings tvdbMappings) throws ReleaseControlException {
        TvRelease tvRelease = (TvRelease) release;
        tvdbMappings.setInfo(tvRelease,
                () -> JTheTVDBAdapter.getAdapter(manager).getSerie(tvRelease).map(TheTVDBSerie::getId).mapToInt(Integer::parseInt));

    }

    private void setTvrageID() {
        TVRageShowInfo tvrShowInfo = tvra.searchShow((TvRelease) release);
        if (tvrShowInfo != null) {
            ((TvRelease) release).setTvrageId(tvrShowInfo.getShowId());
        }
    }
}
