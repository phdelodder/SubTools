package org.lodder.subtools.multisubdownloader.lib.control;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.tvdb.JTheTvdbAdapter;
import org.lodder.subtools.sublibrary.data.tvdb.model.TheTvdbSerie;
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
    private final TvRelease tvRelease;

    private static final Logger LOGGER = LoggerFactory.getLogger(TvReleaseControl.class);

    public TvReleaseControl(TvRelease tvRelease, Settings settings, Manager manager) {
        super(settings, manager);
        this.tvRelease = tvRelease;
        this.jtvdba = JTheTvdbAdapter.getAdapter(manager);
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
        setTvdbId();
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
        setTvdbId();
        if (tvRelease.getTvdbId() > 0 && getSettings().getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVDB) {
            jtvdba.getEpisode(tvRelease.getTvdbId(), tvRelease.getSeason(), tvRelease.getEpisodeNumbers().get(0))
                    .ifPresent(tvRelease::updateTvdbEpisodeInfo);
        }
    }

    private void setTvdbId() {
        jtvdba.getSerie(tvRelease.getName()).map(TheTvdbSerie::getId).mapToInt(Integer::parseInt).ifPresent(tvRelease::setTvdbId);
    }

    @Override
    public Release getVideoFile() {
        return tvRelease;
    }
}
