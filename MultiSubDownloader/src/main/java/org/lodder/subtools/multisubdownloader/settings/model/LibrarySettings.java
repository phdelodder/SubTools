package org.lodder.subtools.multisubdownloader.settings.model;

import java.io.File;

import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LibrarySettings {

    private String libraryFilenameStructure = "";
    private String libraryFolderStructure = "";
    private File libraryFolder;
    private boolean libraryFilenameReplaceSpace;
    private boolean libraryFolderReplaceSpace;
    private boolean libraryIncludeLanguageCode;
    private boolean libraryReplaceChars;
    private boolean libraryRemoveEmptyFolders;
    private boolean libraryUseTVDBNaming;
    private String defaultNlText, defaultEnText;
    private LibraryActionType libraryAction = LibraryActionType.NOTHING;
    private LibraryOtherFileActionType libraryOtherFileAction = LibraryOtherFileActionType.NOTHING;
    private String libraryFilenameReplacingSpaceSign = "";
    private String libraryFolderReplacingSpaceSign = "";
    private boolean libraryBackupSubtitle;
    private boolean libraryBackupUseWebsiteFileName;
    private File libraryBackupSubtitlePath;
}
