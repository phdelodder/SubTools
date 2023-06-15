package org.lodder.subtools.multisubdownloader.settings.model;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.sublibrary.Language;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LibrarySettings {

    private String libraryFilenameStructure = "";
    private String libraryFolderStructure = "";
    private Path libraryFolder;
    private boolean libraryFilenameReplaceSpace;
    private boolean libraryFolderReplaceSpace;
    private boolean libraryIncludeLanguageCode;
    private boolean libraryRemoveEmptyFolders;
    private boolean libraryUseTVDBNaming;
    private LibraryActionType libraryAction = LibraryActionType.NOTHING;
    private LibraryOtherFileActionType libraryOtherFileAction = LibraryOtherFileActionType.NOTHING;
    private Character libraryFilenameReplacingSpaceChar;
    private Character libraryFolderReplacingSpaceChar;
    private boolean libraryBackupSubtitle;
    private boolean libraryBackupUseWebsiteFileName;
    private Path libraryBackupSubtitlePath;
    private Map<Language, String> langCodeMap = new LinkedHashMap<>();

    public boolean hasLibraryAction(LibraryActionType libraryAction) {
        return this.libraryAction == libraryAction;
    }

    public boolean hasAnyLibraryAction(LibraryActionType... libraryActions) {
        return Arrays.stream(libraryActions).anyMatch(this::hasLibraryAction);
    }

    public boolean hasLibraryOtherFileAction(LibraryOtherFileActionType libraryOtherFileAction) {
        return this.libraryOtherFileAction == libraryOtherFileAction;
    }
}
