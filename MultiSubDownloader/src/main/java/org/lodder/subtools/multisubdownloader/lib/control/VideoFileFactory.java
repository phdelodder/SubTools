package org.lodder.subtools.multisubdownloader.lib.control;

import java.io.File;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.exception.VideoFileParseException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;

public class VideoFileFactory {

  public static Release get(final File file, final File basedir, final Settings settings,
      final String languagecode) throws ControlFactoryException, VideoFileParseException,
      VideoControlException {
    Logger.instance.trace("VideoFileFactory", "get", "");
    ReleaseControl vfc = VideoFileControlFactory.getController(file, basedir, settings);
    if (vfc instanceof EpisodeFileControl) {
      if (languagecode.isEmpty()) {
        vfc.process(settings.getMappingSettings().getMappingList());
      } else {
        Logger.instance.log("Treating As Episode: " + vfc.getVideoFile().getPath() + File.separator
            + vfc.getVideoFile().getFilename());
        vfc.processWithSubtitles(settings.getMappingSettings().getMappingList(), languagecode);
      }
    } else if (vfc instanceof MovieFileControl) {
      if (languagecode.isEmpty()) {
        vfc.process(settings.getMappingSettings().getMappingList());
      } else {
        Logger.instance.log("Treating As Movie: " + vfc.getVideoFile().getPath() + File.separator
            + vfc.getVideoFile().getFilename());

        vfc.processWithSubtitles(languagecode);
      }
    }
    return vfc.getVideoFile();
  }

}
