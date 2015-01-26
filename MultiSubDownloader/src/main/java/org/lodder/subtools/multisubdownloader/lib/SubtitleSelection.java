package org.lodder.subtools.multisubdownloader.lib;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;

public abstract class SubtitleSelection {

  private Settings settings;
  private Release release;

  public SubtitleSelection(Settings settings, Release release) {
    this.settings = settings;
    this.release = release;
  }
  
  protected abstract int getUserInput(Release release);
}
