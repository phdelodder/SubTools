package org.lodder.subtools.multisubdownloader.lib.control;

import org.lodder.subtools.multisubdownloader.lib.JTVRageAdapter;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.sublibrary.JTheTVDBAdapter;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.data.thetvdb.model.TheTVDBSerie;
import org.lodder.subtools.sublibrary.data.tvrage.model.TVRageShowInfo;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.TvdbMappings;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ OptionalExtension.class })
public class TvReleaseControl extends ReleaseControl<TvRelease> {

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
        if (getRelease().getTvdbId() > 0) {
            jtvdba.getEpisode(getRelease())
                    .ifPresentOrThrow(getRelease()::updateTvdbEpisodeInfo, () -> new ReleaseControlException(
                            "Season %s Episode %s not found, check file".formatted(getRelease().getSeason(),
                                    getRelease().getEpisodeNumbers().toString()),
                            getRelease()));
        } else {
            throw new ReleaseControlException("Show not found, check file", getRelease());
        }
    }

    public void processTVRage() throws ReleaseControlException {
        setTvrageId();
        tvra.getEpisodeInfo(getRelease().getTvrageId(), getRelease().getSeason(), getRelease().getEpisodeNumbers().get(0))
                .ifPresentDo(getRelease()::updateTVRageEpisodeInfo)
                .orElseThrow(() -> new ReleaseControlException("Season " + getRelease().getSeason() + " Episode "
                        + getRelease().getEpisodeNumbers().toString() + "not found, check file", getRelease()));
    }

    @Override
    public void process(TvdbMappings tvdbMappings) throws ReleaseControlException {
        // return episodeFile;
        if ("".equals(getRelease().getName())) {
            throw new ReleaseControlException("Unable to extract episode details, check file", getRelease());
        } else {
            LOGGER.debug("process: showname [{}], season [{}], episode [{}]", getRelease().getName(),
                    getRelease().getSeason(), getRelease().getEpisodeNumbers());

            if (getRelease().isSpecial()) {
                processSpecial(tvdbMappings);
            } else {
                if (SettingsProcessEpisodeSource.TVRAGE.equals(getSettings().getProcessEpisodeSource())) {
                    processTVRage();
                }
                processTvdb(tvdbMappings);
            }
        }
    }

    private void processSpecial(TvdbMappings tvdbMappings) throws ReleaseControlException {
        setTvrageId();
        if (getRelease().getTvrageId() > 0 && getSettings().getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVRAGE) {
            tvra.getEpisodeInfo(getRelease().getTvrageId(), getRelease().getSeason(), getRelease().getEpisodeNumbers().get(0))
                    .ifPresent(getRelease()::updateTVRageEpisodeInfo);
        }
        setTvdbID(tvdbMappings);
        if (getRelease().getTvdbId() > 0 && getSettings().getProcessEpisodeSource() == SettingsProcessEpisodeSource.TVDB) {
            jtvdba.getEpisode(getRelease()).ifPresent(getRelease()::updateTvdbEpisodeInfo);
        }
    }

    private void setTvdbID(TvdbMappings tvdbMappings) throws ReleaseControlException {
        tvdbMappings.setInfo(getRelease(),
                () -> JTheTVDBAdapter.getAdapter(getManager()).getSerie(getRelease()).map(TheTVDBSerie::getId).mapToInt(Integer::parseInt));
    }

    private void setTvrageId() {
        tvra.searchShow(getRelease()).map(TVRageShowInfo::getShowId).ifPresent(getRelease()::setTvrageId);
    }
}
