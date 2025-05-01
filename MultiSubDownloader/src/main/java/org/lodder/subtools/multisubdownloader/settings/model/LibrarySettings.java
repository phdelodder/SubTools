package org.lodder.subtools.multisubdownloader.settings.model;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import manifold.ext.props.rt.api.var;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.sublibrary.Language;

public class LibrarySettings {

    @var String filenameStructure = "";
    @var String folderStructure = "";
    @var Path folder;
    @var boolean filenameReplaceSpace;
    @var boolean folderReplaceSpace;
    @var boolean includeLanguageCode;
    @var boolean removeEmptyFolders;
    @var boolean useTVDBNaming;
    @var LibraryActionType action = LibraryActionType.NOTHING;
    @var LibraryOtherFileActionType otherFileAction = LibraryOtherFileActionType.NOTHING;
    @var Character filenameReplacingSpaceChar;
    @var Character folderReplacingSpaceChar;
    @var boolean backupSubtitle;
    @var boolean backupUseWebsiteFileName;
    @var Path backupSubtitlePath;
    @var Map<Language, String> langCodeMap = new LinkedHashMap<>();

    public boolean hasLibraryAction(LibraryActionType libraryAction) {
        return this.action == libraryAction;
    }

    public boolean hasAnyLibraryAction(LibraryActionType... libraryActions) {
        return libraryActions.stream().anyMatch(this::hasLibraryAction);
    }

    public boolean hasLibraryOtherFileAction(LibraryOtherFileActionType libraryOtherFileAction) {
        return this.otherFileAction == libraryOtherFileAction;
    }
}
