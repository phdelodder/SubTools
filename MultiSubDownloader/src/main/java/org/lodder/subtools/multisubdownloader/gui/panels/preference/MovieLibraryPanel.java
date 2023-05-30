package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import java.io.Serial;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

public class MovieLibraryPanel extends VideoLibraryPanel {

    @Serial
    private static final long serialVersionUID = -9175813173306481849L;

    public MovieLibraryPanel(LibrarySettings libSettings, Manager manager, boolean renameMode,
            UserInteractionHandler userInteractionHandler) {
        super(libSettings, VideoType.MOVIE, manager, renameMode, userInteractionHandler);
        repaint();
    }
}
