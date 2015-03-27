package org.lodder.subtools.subsort.lib.control;

import java.util.List;

import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpisodeFileControl extends VideoFileControl {

  private static final Logger LOGGER = LoggerFactory.getLogger(EpisodeFileControl.class);

  public EpisodeFileControl(TvRelease tvRelease) {
    super(tvRelease);
  }

  public TvRelease process(List<MappingTvdbScene> dict) throws ReleaseControlException {
    TvRelease tvRelease = (TvRelease) release;
    // return episodeFile;
    if (tvRelease.getShow().equals("")) {
      throw new ReleaseControlException("Unable to extract episode details, check file", release);
    } else {
      LOGGER.debug("process: showname [{}], season [{}], episode [{}]", tvRelease.getShow(),
          tvRelease.getSeason(), tvRelease.getEpisodeNumbers());

      return tvRelease;
    }
  }
}
