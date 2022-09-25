package org.lodder.subtools.multisubdownloader.lib.control;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvdb.TheTvdbAdapter;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class TvReleaseControl extends ReleaseControl {

    private final TheTvdbAdapter jtvdba;
    private final TvRelease tvRelease;

    private static final Logger LOGGER = LoggerFactory.getLogger(TvReleaseControl.class);

    public TvReleaseControl(TvRelease tvRelease, Settings settings, Manager manager) {
        super(settings, manager);
        this.tvRelease = tvRelease;
        this.jtvdba = TheTvdbAdapter.getInstance(manager);
    }

    @Override
    public void process() throws ReleaseControlException {
        // return episodeFile;
        if (StringUtils.isBlank(tvRelease.getName())) {
            throw new ReleaseControlException("Unable to extract episode details, check file", tvRelease);
        } else {
            LOGGER.debug("process: showname [{}], season [{}], episode [{}]", tvRelease.getName(),
                    tvRelease.getSeason(), tvRelease.getEpisodeNumbers());

            if (tvRelease.isSpecial()) {
                processSpecial();
            } else {
                processTvdb();
            }
        }
    }

    public void processTvdb() throws ReleaseControlException {
        setTvdbInfo();
        if (tvRelease.getTvdbId() > 0) {
            jtvdba.getEpisode(tvRelease.getTvdbId(), tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0))
                    .ifPresentOrThrow(tvRelease::updateTvdbEpisodeInfo,
                            () -> new ReleaseControlException("Season %s Episode %s not found, check file".formatted(tvRelease.getSeason(),
                                    tvRelease.getEpisodeNumbers().toString()), tvRelease));
        } else {
            throw new ReleaseControlException("Show not found, check file", tvRelease);
        }
    }

    private void processSpecial() {
        setTvdbInfo();
        if (tvRelease.getTvdbId() > 0 && getSettings().getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVDB) {
            jtvdba.getEpisode(tvRelease.getTvdbId(), tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0))
                    .ifPresent(tvRelease::updateTvdbEpisodeInfo);
        }
    }

    private void setTvdbInfo() {
        jtvdba.getSerie(tvRelease.getName()).ifPresent(tvdbSerie -> {
            tvRelease.setTvdbId(Integer.parseInt(tvdbSerie.getId()));
            tvRelease.setOriginalName(tvdbSerie.getSerieName());
        });
    }

    @Override
    public Release getVideoFile() {
        return tvRelease;
    }
}
