package org.lodder.subtools.multisubdownloader.lib.control;

import java.io.File;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.exception.VideoFileParseException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;

public class ReleaseFactory {

  public static Release get(final File file, final File basedir, final Settings settings,
      final String languagecode) throws ControlFactoryException, VideoFileParseException,
      VideoControlException {
    Logger.instance.trace("VideoFileFactory", "get", "");
    ReleaseControl releaseCtrl = ReleaseControlFactory.getController(file, basedir, settings);
    if (releaseCtrl instanceof EpisodeFileControl) {
      if (languagecode.isEmpty()) {
        releaseCtrl.process(settings.getMappingSettings().getMappingList());
      } else {
        Logger.instance.log("Treating As Episode: " + releaseCtrl.getVideoFile().getPath() + File.separator
            + releaseCtrl.getVideoFile().getFilename());
        releaseCtrl.processWithSubtitles(settings.getMappingSettings().getMappingList(), languagecode);
      }
    } else if (releaseCtrl instanceof MovieFileControl) {
      if (languagecode.isEmpty()) {
        releaseCtrl.process(settings.getMappingSettings().getMappingList());
      } else {
        Logger.instance.log("Treating As Movie: " + releaseCtrl.getVideoFile().getPath() + File.separator
            + releaseCtrl.getVideoFile().getFilename());

        releaseCtrl.processWithSubtitles(languagecode);
      }
    }
    return releaseCtrl.getVideoFile();
  }

}
