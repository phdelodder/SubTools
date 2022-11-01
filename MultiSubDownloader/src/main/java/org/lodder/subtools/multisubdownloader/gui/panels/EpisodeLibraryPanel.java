package org.lodder.subtools.multisubdownloader.gui.panels;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

public class EpisodeLibraryPanel extends VideoLibraryPanel {

    private static final long serialVersionUID = -9175813173306481849L;

    public EpisodeLibraryPanel(LibrarySettings libSettings, Manager manager, Boolean renameMode, UserInteractionHandler userInteractionHandler) {
        super(libSettings, VideoType.EPISODE, manager, renameMode, userInteractionHandler);
        repaint();
    }
}
