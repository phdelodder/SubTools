package org.lodder.subtools.multisubdownloader.lib;

import java.io.File;

import org.lodder.subtools.multisubdownloader.lib.control.MovieReleaseControl;
import org.lodder.subtools.multisubdownloader.lib.control.ReleaseControl;
import org.lodder.subtools.multisubdownloader.lib.control.TvReleaseControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.exception.ReleaseParseException;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.TvRelease;

public class ReleaseFactory {

  static final ReleaseParser releaseParser = new ReleaseParser();
  static ReleaseControl releaseControl;
  
  public static Release createRelease(final File file, final Settings settings){
    Release r = null;
    
    try {
      r = releaseParser.parse(file);
      
      switch (r.getVideoType()){
        case EPISODE:
          releaseControl = new TvReleaseControl((TvRelease) r, settings);
          break;
        case MOVIE:
          releaseControl = new MovieReleaseControl((MovieRelease) r, settings);
          break;
        default:
          break;
      }     
      
      releaseControl.process(settings.getMappingSettings().getMappingList());
      r = releaseControl.getVideoFile();
      
    } catch (ReleaseParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ReleaseControlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return r;
  }
}
