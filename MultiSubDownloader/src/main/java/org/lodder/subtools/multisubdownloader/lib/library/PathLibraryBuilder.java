package org.lodder.subtools.multisubdownloader.lib.library;

import java.nio.file.Path;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

public class PathLibraryBuilder extends PathLibraryCommonBuilder {

    private final LibrarySettings librarySettings;

    public PathLibraryBuilder(LibrarySettings librarySettings, Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        this.librarySettings = librarySettings;
    }

    @Override
    protected boolean hasAnyLibraryAction(LibraryActionType... libraryActions) {
        return librarySettings.hasAnyLibraryAction(libraryActions);
    }

    @Override
    protected Path getLibraryFolder() {
        return librarySettings.getLibraryFolder();
    }

    @Override
    protected String getFolderStructure() {
        return librarySettings.getLibraryFolderStructure();
    }

    @Override
    protected boolean isFolderReplaceSpace() {
        return librarySettings.isLibraryFolderReplaceSpace();
    }

    @Override
    protected String getFolderReplacingSpaceSign() {
        return librarySettings.getLibraryFolderReplacingSpaceSign();
    }

    @Override
    protected boolean isUseTVDBNaming() {
        return librarySettings.isLibraryUseTVDBNaming();
    }

}
