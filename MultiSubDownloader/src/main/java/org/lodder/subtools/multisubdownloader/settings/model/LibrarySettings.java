package org.lodder.subtools.multisubdownloader.settings.model;

import java.nio.file.Path;
import java.util.Arrays;

import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;

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
    private String defaultNlText, defaultEnText;
    private LibraryActionType libraryAction = LibraryActionType.NOTHING;
    private LibraryOtherFileActionType libraryOtherFileAction = LibraryOtherFileActionType.NOTHING;
    private String libraryFilenameReplacingSpaceSign = "";
    private String libraryFolderReplacingSpaceSign = "";
    private boolean libraryBackupSubtitle;
    private boolean libraryBackupUseWebsiteFileName;
    private Path libraryBackupSubtitlePath;

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
