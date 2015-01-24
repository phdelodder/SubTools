package org.lodder.subtools.multisubdownloader.gui.panels;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.model.VideoType;

public class MovieLibraryPanel extends VideoLibraryPanel {
  /**
     *
     */
  private static final long serialVersionUID = -9175813173306481849L;


  public MovieLibraryPanel(LibrarySettings libSettings) {
    super(libSettings, VideoType.MOVIE);
    repaint();
  }

  protected void initializeEmptyValues() {
    /** Default values for new setup **/
    pnlStructureFile.setFileStructure("");
    pnlStructureFolder.getStructure().setText("%MOVIE TITLE% (%YEAR%)");
  }

}
