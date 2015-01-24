package org.lodder.subtools.multisubdownloader.lib;

import java.io.File;

import org.lodder.subtools.multisubdownloader.lib.control.MovieReleaseControl;
import org.lodder.subtools.multisubdownloader.lib.control.ReleaseControl;
import org.lodder.subtools.multisubdownloader.lib.control.TvReleaseControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.ControlFactoryException;
import org.lodder.subtools.sublibrary.exception.VideoControlException;
import org.lodder.subtools.sublibrary.exception.VideoFileParseException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoType;

public class ReleaseFactory {

  static final ReleaseParser releaseParser = new ReleaseParser();
  
  public static Release get(final File file, final File basedir, final Settings settings,
      final String languagecode) throws ControlFactoryException, VideoFileParseException,
      VideoControlException {
    Logger.instance.trace("VideoFileFactory", "get", "");
    ReleaseControl releaseCtrl = getController(file, basedir, settings);
    if (releaseCtrl instanceof TvReleaseControl) {
      if (languagecode.isEmpty()) {
        releaseCtrl.process(settings.getMappingSettings().getMappingList());
      } else {
        Logger.instance.log("Treating As Episode: " + releaseCtrl.getVideoFile().getPath() + File.separator
            + releaseCtrl.getVideoFile().getFilename());
        releaseCtrl.processWithSubtitles(settings.getMappingSettings().getMappingList(), languagecode);
      }
    } else if (releaseCtrl instanceof MovieReleaseControl) {
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

  public static ReleaseControl getController(File file, File basedir, Settings settings)
      throws VideoFileParseException, ControlFactoryException {
    Release release = releaseParser.parse(file, basedir);
    if (release.getVideoType() == VideoType.EPISODE) {
      return new TvReleaseControl((TvRelease) release, settings);
    } else if (release.getVideoType() == VideoType.MOVIE) {
      return new MovieReleaseControl((MovieRelease) release, settings);
    }
    throw new ControlFactoryException("Can't find controller");
  }

}
