package org.lodder.subtools.multisubdownloader.lib;

import java.io.File;
import java.io.IOException;

import org.lodder.subtools.multisubdownloader.actions.CleanAction;
import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.lib.library.PathLibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.DetectLanguage;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.util.Files;

public class Actions {

  private final Settings settings;
  private final boolean usingCMD;

  public Actions(Settings settings, final boolean usingCMD) {
    this.settings = settings;
    this.usingCMD = usingCMD;
  }

  


}
