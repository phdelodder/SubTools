package org.lodder.subtools.multisubdownloader.lib.control;

import org.lodder.subtools.multisubdownloader.lib.JTVRageAdapter;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvdb.JTheTvdbAdapter;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbSerie;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageShowInfo;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class TvReleaseControl extends ReleaseControl {

    private final JTheTvdbAdapter jtvdba;
    private final JTVRageAdapter tvra;
    private final TvRelease tvRelease;

    private static final Logger LOGGER = LoggerFactory.getLogger(TvReleaseControl.class);

    public TvReleaseControl(TvRelease tvRelease, Settings settings, Manager manager) {
        super(settings, manager);
        this.tvRelease = tvRelease;
        this.jtvdba = JTheTvdbAdapter.getAdapter(manager);
        this.tvra = new JTVRageAdapter(manager);
    }

    public void processTvdb() throws ReleaseControlException {
        if (tvRelease.getTvdbId() > 0) {
            jtvdba.getEpisode(tvRelease.getTvdbId(), tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0))
                    .ifPresentOrThrow(tvRelease::updateTvdbEpisodeInfo,
                            () -> new ReleaseControlException("Season %s Episode %s not found, check file".formatted(tvRelease.getSeason(),
                                    tvRelease.getEpisodeNumbers().toString()), tvRelease));
        } else {
            throw new ReleaseControlException("Show not found, check file", tvRelease);
        }
    }

    public void processTVRage() throws ReleaseControlException {
        setTvrageId();
        tvra.getEpisodeInfo(tvRelease.getTvrageId(), tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0))
                .ifPresentDo(tvRelease::updateTVRageEpisodeInfo)
                .orElseThrow(() -> new ReleaseControlException("Season " + tvRelease.getSeason() + " Episode "
                        + tvRelease.getEpisodeNumbers().toString() + "not found, check file", tvRelease));
    }

    @Override
    public void process() throws ReleaseControlException {
        // return episodeFile;
        if ("".equals(tvRelease.getName())) {
            throw new ReleaseControlException("Unable to extract episode details, check file", tvRelease);
        } else {
            LOGGER.debug("process: showname [{}], season [{}], episode [{}]", tvRelease.getName(),
                    tvRelease.getSeason(), tvRelease.getEpisodeNumbers());

            if (tvRelease.isSpecial()) {
                processSpecial();
            } else {
                if (SettingsProcessEpisodeSource.TVRAGE.equals(getSettings().getProcessEpisodeSource())) {
                    processTVRage();
                }
                processTvdb();
            }
        }
    }

    private void processSpecial() {
        setTvrageId();
        if (tvRelease.getTvrageId() > 0 && getSettings().getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVRAGE) {
            tvra.getEpisodeInfo(tvRelease.getTvrageId(), tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0))
                    .ifPresent(tvRelease::updateTVRageEpisodeInfo);
        }

        setTvdbId();
        if (tvRelease.getTvdbId() > 0 && getSettings().getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVDB) {
            jtvdba.getEpisode(tvRelease.getTvdbId(), tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0))
                    .ifPresent(tvRelease::updateTvdbEpisodeInfo);
        }
    }

    private void setTvdbId() {
        jtvdba.getSerie(tvRelease.getName()).map(TheTvdbSerie::getId).mapToInt(Integer::parseInt).ifPresent(tvRelease::setTvdbId);
    }

    private void setTvrageId() {
        tvra.getShowInfo(tvRelease.getName()).map(TVRageShowInfo::getShowId).ifPresent(tvRelease::setTvrageId);
    }

    @Override
    public Release getVideoFile() {
        return tvRelease;
    }
}
