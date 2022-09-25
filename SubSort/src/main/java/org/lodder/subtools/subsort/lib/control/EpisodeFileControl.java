package org.lodder.subtools.subsort.lib.control;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpisodeFileControl extends VideoFileControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(EpisodeFileControl.class);

    public EpisodeFileControl(TvRelease tvRelease) throws ReleaseControlException {
        super(tvRelease);
        process(tvRelease);
    }

    private TvRelease process(TvRelease tvRelease) throws ReleaseControlException {
        if (StringUtils.isBlank(tvRelease.getName())) {
            throw new ReleaseControlException("Unable to extract episode details, check file", release);
        } else {
            LOGGER.debug("process: showname [{}], season [{}], episode [{}]", tvRelease.getName(),
                    tvRelease.getSeason(), tvRelease.getEpisodeNumbers());

            return tvRelease;
        }
    }
}
