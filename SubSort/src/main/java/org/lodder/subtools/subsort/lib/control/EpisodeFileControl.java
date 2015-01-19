package org.lodder.subtools.subsort.lib.control;

import java.util.List;

import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public class EpisodeFileControl extends VideoFileControl {

  public EpisodeFileControl(EpisodeFile episodeFile) {
    super(episodeFile);
  }

  public EpisodeFile process(List<MappingTvdbScene> dict) throws VideoControlException {
    Logger.instance.trace("EpisodeFileControl", "process", "");

    EpisodeFile episodeFile = (EpisodeFile) release;
    // return episodeFile;
    if (episodeFile.getShow().equals("")) {
      throw new VideoControlException("Unable to extract episode details, check file", release);
    } else {
      Logger.instance.debug("Showname: " + episodeFile.getShow());
      Logger.instance.debug("Season: " + episodeFile.getSeason());
      Logger.instance.debug("Episode: " + episodeFile.getEpisodeNumbers());

      return episodeFile;
    }
  }
}
