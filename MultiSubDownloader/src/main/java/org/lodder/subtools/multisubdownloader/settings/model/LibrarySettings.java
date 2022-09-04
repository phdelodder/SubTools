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

    public LibraryActionType getLibraryAction() {
        return libraryAction;
    }

    public void setLibraryAction(LibraryActionType libraryAction) {
        this.libraryAction = libraryAction;
    }

    public LibraryOtherFileActionType getLibraryOtherFileAction() {
        return libraryOtherFileAction;
    }

    public void setLibraryOtherFileAction(LibraryOtherFileActionType libraryOtherFileAction) {
        this.libraryOtherFileAction = libraryOtherFileAction;
    }

    public String getLibraryFilenameReplacingSpaceSign() {
        return libraryFilenameReplacingSpaceSign;
    }

    public void setLibraryFilenameReplacingSpaceSign(String libraryFilenameReplacingSpaceSign) {
        this.libraryFilenameReplacingSpaceSign = libraryFilenameReplacingSpaceSign;
    }

    public String getLibraryFolderReplacingSpaceSign() {
        return libraryFolderReplacingSpaceSign;
    }

    public void setLibraryFolderReplacingSpaceSign(String libraryFolderReplacingSpaceSign) {
        this.libraryFolderReplacingSpaceSign = libraryFolderReplacingSpaceSign;
    }

    public boolean isLibraryBackupSubtitle() {
        return libraryBackupSubtitle;
    }

    public void setLibraryBackupSubtitle(boolean libraryBackupSubtitle) {
        this.libraryBackupSubtitle = libraryBackupSubtitle;
    }

    public boolean isLibraryBackupUseWebsiteFileName() {
        return libraryBackupUseWebsiteFileName;
    }

    public void setLibraryBackupUseWebsiteFileName(boolean libraryBackupUseWebsiteFileName) {
        this.libraryBackupUseWebsiteFileName = libraryBackupUseWebsiteFileName;
    }

    public File getLibraryBackupSubtitlePath() {
        return libraryBackupSubtitlePath;
    }

    public void setLibraryBackupSubtitlePath(File libraryBackupSubtitlePath) {
        this.libraryBackupSubtitlePath = libraryBackupSubtitlePath;
    }
}
