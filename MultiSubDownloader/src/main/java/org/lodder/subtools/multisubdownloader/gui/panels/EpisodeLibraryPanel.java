package org.lodder.subtools.multisubdownloader.gui.panels;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.VideoType;

public class EpisodeLibraryPanel extends VideoLibraryPanel {
  /**
     *
     */
  private static final long serialVersionUID = -9175813173306481849L;


  public EpisodeLibraryPanel(LibrarySettings libSettings, Manager manager, Boolean renameMode) {
    super(libSettings, VideoType.EPISODE, manager, renameMode);
    repaint();
  }

  protected void initializeEmptyValues() {
    /** Default values for new setup **/
    pnlStructureFile.setFileStructure("%SHOW NAME%.S%SS%E%EE%.%TITLE%");
    pnlStructureFolder.getStructure().setText("%SHOW NAME%%SEPARATOR%Season %S%");
  }

}
