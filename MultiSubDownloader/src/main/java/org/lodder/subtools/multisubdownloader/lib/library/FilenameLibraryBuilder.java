package org.lodder.subtools.multisubdownloader.lib.library;

import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

public class FilenameLibraryBuilder extends FilenameLibraryCommonBuilder {

    private final LibrarySettings librarySettings;

    public FilenameLibraryBuilder(LibrarySettings librarySettings, Manager manager, UserInteractionHandler userInteractionHandler) {
        super(manager, userInteractionHandler);
        this.librarySettings = librarySettings;
    }

    @Override
    protected boolean hasAnyLibraryAction(LibraryActionType... libraryActions) {
        return librarySettings.hasAnyLibraryAction(libraryActions);
    }

    @Override
    protected String getFilenameStructure() {
        return librarySettings.getLibraryFilenameStructure();
    }

    @Override
    protected boolean isReplaceChars() {
        return librarySettings.isLibraryReplaceChars();
    }

    @Override
    protected boolean isFilenameReplaceSpace() {
        return librarySettings.isLibraryFilenameReplaceSpace();
    }

    @Override
    protected String getFilenameReplacingSpaceSign() {
        return librarySettings.getLibraryFilenameReplacingSpaceSign();
    }

    @Override
    protected boolean isIncludeLanguageCode() {
        return librarySettings.isLibraryIncludeLanguageCode();
    }

    @Override
    protected String getDefaultNlText() {
        return librarySettings.getDefaultNlText();
    }

    @Override
    protected String getDefaultEnText() {
        return librarySettings.getDefaultEnText();
    }

    @Override
    protected boolean isUseTVDBNaming() {
        return librarySettings.isLibraryUseTVDBNaming();
    }
}
