package org.lodder.subtools.multisubdownloader.gui.panels;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.model.VideoType;

public class EpisodeLibraryPanel extends VideoLibraryPanel {
  /**
     *
     */
  private static final long serialVersionUID = -9175813173306481849L;


  public EpisodeLibraryPanel(LibrarySettings libSettings) {
    super(libSettings, VideoType.EPISODE);
    repaint();
  }

  protected void initializeEmptyValues() {
    /** Default values for new setup **/
    txtFileStructure.setText("%SHOW NAME%.S%SS%E%EE%.%TITLE%");
    txtFolderStructure.setText("%SHOW NAME%%SEPARATOR%Season %S%");
  }

}
