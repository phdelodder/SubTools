package org.lodder.subtools.multisubdownloader.lib;

import java.io.File;

import org.lodder.subtools.multisubdownloader.lib.control.MovieReleaseControl;
import org.lodder.subtools.multisubdownloader.lib.control.ReleaseControl;
import org.lodder.subtools.multisubdownloader.lib.control.TvReleaseControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;

public class ReleaseFactory {

  private ReleaseParser releaseParser;
  private ReleaseControl releaseControl;
  private Settings settings;
  private Manager manager;
  
  public ReleaseFactory(Settings settings, Manager manager){
    releaseParser = new ReleaseParser();
    this.settings = settings;
    this.manager = manager;
  }
  
  public Release createRelease(final File file){
    Release r = null;
    
    try {
      r = releaseParser.parse(file);
      
      switch (r.getVideoType()){
        case EPISODE:
          releaseControl = new TvReleaseControl((TvRelease) r, settings, manager);
          break;
        case MOVIE:
          releaseControl = new MovieReleaseControl((MovieRelease) r, settings, manager);
          break;
        default:
          break;
      }     
      
      releaseControl.process(settings.getMappingSettings().getMappingList());
      r = releaseControl.getVideoFile();
      
    } catch (ReleaseParseException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (ReleaseControlException e) {
      Logger.instance.error(Logger.stack2String(e));
      return null;
    }
    
    return r;
  }
}
