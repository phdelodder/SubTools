package org.lodder.subtools.subsort.lib.control;

import java.util.List;

import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public class EpisodeFileControl extends VideoFileControl {

  public EpisodeFileControl(TvRelease tvRelease) {
    super(tvRelease);
  }

  public TvRelease process(List<MappingTvdbScene> dict) throws VideoControlException {
    Logger.instance.trace("EpisodeFileControl", "process", "");

    TvRelease tvRelease = (TvRelease) release;
    // return episodeFile;
    if (tvRelease.getShow().equals("")) {
      throw new VideoControlException("Unable to extract episode details, check file", release);
    } else {
      Logger.instance.debug("Showname: " + tvRelease.getShow());
      Logger.instance.debug("Season: " + tvRelease.getSeason());
      Logger.instance.debug("Episode: " + tvRelease.getEpisodeNumbers());

      return tvRelease;
    }
  }
}
