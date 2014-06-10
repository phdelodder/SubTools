package org.lodder.subtools.multisubdownloader.settings.model;

import java.io.File;

import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;

public class LibrarySettings {

    private String libraryFilenameStructure, libraryFolderStructure;
    private File libraryFolder;
    private boolean libraryFilenameReplaceSpace, libraryFolderReplaceSpace;
    private boolean libraryIncludeLanguageCode, libraryReplaceChars;
    private boolean libraryRemoveEmptyFolders, libraryUseTVDBNaming;
    private String defaultNlText, defaultEnText;
    private LibraryActionType libraryAction;
    private LibraryOtherFileActionType libraryOtherFileAction;
    private String libraryFilenameReplacingSpaceSign, libraryFolderReplacingSpaceSign;
    private boolean libraryBackupSubtitle, libraryBackupUseWebsiteFileName;
    private File libraryBackupSubtitlePath;

    public LibrarySettings() {
        libraryFilenameStructure = "";
        libraryFolderStructure = "";
        libraryFilenameReplaceSpace = false;
        libraryFolderReplaceSpace = false;
        libraryFolder = null;
        libraryIncludeLanguageCode = false;
        libraryReplaceChars = false;
        libraryUseTVDBNaming = false;
        libraryAction = LibraryActionType.NOTHING;
        libraryOtherFileAction = LibraryOtherFileActionType.NOTHING;
        libraryFilenameReplacingSpaceSign = "";
        libraryFolderReplacingSpaceSign = "";
        libraryBackupSubtitle = false;
        libraryBackupUseWebsiteFileName = false;
        libraryBackupSubtitlePath = null;
    }

    public String getLibraryFilenameStructure() {
        return libraryFilenameStructure;
    }

    public void setLibraryFilenameStructure(String libraryFilenameStructure) {
        this.libraryFilenameStructure = libraryFilenameStructure;
    }

    public String getLibraryFolderStructure() {
        return libraryFolderStructure;
    }

    public void setLibraryFolderStructure(String libraryFolderStructure) {
        this.libraryFolderStructure = libraryFolderStructure;
    }

    public File getLibraryFolder() {
        return libraryFolder;
    }

    public void setLibraryFolder(File libraryFolder) {
        this.libraryFolder = libraryFolder;
    }

    public boolean isLibraryFilenameReplaceSpace() {
        return libraryFilenameReplaceSpace;
    }

    public void setLibraryFilenameReplaceSpace(boolean libraryFilenameReplaceSpace) {
        this.libraryFilenameReplaceSpace = libraryFilenameReplaceSpace;
    }
    
    public boolean isLibraryFolderReplaceSpace() {
        return libraryFolderReplaceSpace;
    }

    public void setLibraryFolderReplaceSpace(boolean libraryFolderReplaceSpace) {
        this.libraryFolderReplaceSpace = libraryFolderReplaceSpace;
    }

    public boolean isLibraryIncludeLanguageCode() {
        return libraryIncludeLanguageCode;
    }

    public void setLibraryIncludeLanguageCode(boolean libraryIncludeLanguageCode) {
        this.libraryIncludeLanguageCode = libraryIncludeLanguageCode;
    }

    public void setLibraryReplaceChars(boolean libraryReplaceChars) {
        this.libraryReplaceChars = libraryReplaceChars;
    }

    public boolean isLibraryReplaceChars() {
        return libraryReplaceChars;
    }

    public void setLibraryRemoveEmptyFolders(boolean libraryRemoveEmptyFolders) {
        this.libraryRemoveEmptyFolders = libraryRemoveEmptyFolders;
    }

    public boolean isLibraryRemoveEmptyFolders() {
        return libraryRemoveEmptyFolders;
    }

	public void setDefaultNlText(String defaultNlText) {
		this.defaultNlText = defaultNlText;
	}

	public String getDefaultNlText() {
		return defaultNlText;
	}

	public void setDefaultEnText(String defaultEnText) {
		this.defaultEnText = defaultEnText;
	}

	public String getDefaultEnText() {
		return defaultEnText;
	}

	public boolean isLibraryUseTVDBNaming() {
		return libraryUseTVDBNaming;
	}

	public void setLibraryUseTVDBNaming(boolean libraryUseTVDBNaming) {
		this.libraryUseTVDBNaming = libraryUseTVDBNaming;
	}

    /**
     * @return the libraryAction
     */
    public LibraryActionType getLibraryAction() {
        return libraryAction;
    }

    /**
     * @param libraryAction the libraryAction to set
     */
    public void setLibraryAction(LibraryActionType libraryAction) {
        this.libraryAction = libraryAction;
    }

    /**
     * @return the libraryOtherFileAction
     */
    public LibraryOtherFileActionType getLibraryOtherFileAction() {
        return libraryOtherFileAction;
    }

    /**
     * @param libraryOtherFileAction the libraryOtherFileAction to set
     */
    public void setLibraryOtherFileAction(LibraryOtherFileActionType libraryOtherFileAction) {
        this.libraryOtherFileAction = libraryOtherFileAction;
    }

    /**
     * @return the libraryFilenameReplacingSpaceSign
     */
    public String getLibraryFilenameReplacingSpaceSign() {
        return libraryFilenameReplacingSpaceSign;
    }

    /**
     * @param libraryFilenameReplacingSpaceSign the libraryFilenameReplacingSpaceSign to set
     */
    public void setLibraryFilenameReplacingSpaceSign(String libraryFilenameReplacingSpaceSign) {
        this.libraryFilenameReplacingSpaceSign = libraryFilenameReplacingSpaceSign;
    }
    
    /**
     * @return the libraryFolderReplacingSpaceSign
     */
    public String getLibraryFolderReplacingSpaceSign() {
        return libraryFolderReplacingSpaceSign;
    }

    /**
     * @param libraryFolderReplacingSpaceSign the libraryFolderReplacingSpaceSign to set
     */
    public void setLibraryFolderReplacingSpaceSign(String libraryFolderReplacingSpaceSign) {
        this.libraryFolderReplacingSpaceSign = libraryFolderReplacingSpaceSign;
    }

	/**
	 * @return the libraryBackupSubtitle
	 */
	public boolean isLibraryBackupSubtitle() {
		return libraryBackupSubtitle;
	}

	/**
	 * @param libraryBackupSubtitle the libraryBackupSubtitle to set
	 */
	public void setLibraryBackupSubtitle(boolean libraryBackupSubtitle) {
		this.libraryBackupSubtitle = libraryBackupSubtitle;
	}

	/**
	 * @return the libraryUseWebsiteFileName
	 */
	public boolean isLibraryBackupUseWebsiteFileName() {
		return libraryBackupUseWebsiteFileName;
	}

	/**
	 * @param libraryUseWebsiteFileName the libraryUseOriginalFileName to set
	 */
	public void setLibraryBackupUseWebsiteFileName(boolean libraryBackupUseWebsiteFileName) {
		this.libraryBackupUseWebsiteFileName = libraryBackupUseWebsiteFileName;
	}

	/**
	 * @return the libraryBackupSubtitlePath
	 */
	public File getLibraryBackupSubtitlePath() {
		return libraryBackupSubtitlePath;
	}

	/**
	 * @param libraryBackupSubtitlePath the libraryBackupSubtitlePath to set
	 */
	public void setLibraryBackupSubtitlePath(File libraryBackupSubtitlePath) {
		this.libraryBackupSubtitlePath = libraryBackupSubtitlePath;
	}
}
