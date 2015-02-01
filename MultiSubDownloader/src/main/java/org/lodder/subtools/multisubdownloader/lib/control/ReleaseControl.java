package org.lodder.subtools.multisubdownloader.lib.control;

import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.exception.ReleaseControlException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public abstract class ReleaseControl {

  protected Release release;
  protected SubtitleControl sc;
  protected Settings settings;

  public ReleaseControl(Release release, Settings settings) {
    this.settings = settings;
    this.sc = new SubtitleControl(settings);
    this.release = release;
  }

  public abstract void process(List<MappingTvdbScene> dict) throws ReleaseControlException;

  public void process() throws ReleaseControlException {
    this.process(new ArrayList<MappingTvdbScene>());
  }

  public void setVideoFile(Release release) {
    this.release = release;
  }

  public Release getVideoFile() {
    return release;
  }
}
