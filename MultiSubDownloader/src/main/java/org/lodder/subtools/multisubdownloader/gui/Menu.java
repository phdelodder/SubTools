package org.lodder.subtools.multisubdownloader.gui;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.Serial;

public class Menu extends JMenuBar {

    @Serial
    private static final long serialVersionUID = -7384297314593169280L;

    private JMenu mnFile;
    private JMenuItem mntmQuit;
    private JMenu mnView;
    private JMenu mnSearchResults;
    private JCheckBoxMenuItem chckbxmntmFileName;
    private JCheckBoxMenuItem chckbxmntmType;
    private JCheckBoxMenuItem chckbxmntmTitle;
    private JCheckBoxMenuItem chckbxmntmSeason;
    private JCheckBoxMenuItem chckbxmntmEpisode;
    private JCheckBoxMenuItem chckbxmntmShowOnlyFound;
    private JMenuItem mntmClearLog;
    private JMenu mnEdit;
    private JMenu mnHelp;
    private JMenuItem mntmRenameSerieFiles;
    private JMenuItem mntmRenameMovieFiles;
    private JMenuItem mntmPreferences;
    private JMenu mnSerieNames;
    private JMenuItem mntmTranslateShowNames;
    private JMenu mnImportExport;
    private JMenuItem mntmExportTranslate;
    private JMenuItem mntmImportTranslate;
    private JMenuItem mntmExportPreferences;
    private JMenuItem mntmImportPreferences;
    private JMenuItem mntmAbout;
    private JMenuItem mntmCheckForUpdate;

    public Menu() {
        createComponents();
        addComponentsToMenu();
    }

    private void createComponents() {
        mnFile = new JMenu(getText("Menu.Path"));
        mntmQuit = new JMenuItem(getText("App.Close"));
        mnView = new JMenu(getText("Menu.View"));
        mnSearchResults = new JMenu(getText("Menu.SearchResults"));
        chckbxmntmFileName = new JCheckBoxMenuItem(getText("Menu.Filename"));
        chckbxmntmType = new JCheckBoxMenuItem(getText("Menu.Type"));
        chckbxmntmTitle = new JCheckBoxMenuItem(getText("Menu.Title"));
        chckbxmntmSeason = new JCheckBoxMenuItem(getText("App.Season"));
        chckbxmntmEpisode = new JCheckBoxMenuItem(getText("App.Episode"));
        chckbxmntmShowOnlyFound = new JCheckBoxMenuItem(getText("Menu.OnlyShowFound"));
        mntmClearLog = new JMenuItem(getText("Menu.EraseLogging"));
        mntmRenameSerieFiles = new JMenuItem(getText("Menu.RenameSerie"));
        mntmRenameMovieFiles = new JMenuItem(getText("Menu.RenameMovie"));
        mntmPreferences = new JMenuItem(getText("Menu.Preferences"));
        mnSerieNames = new JMenu(getText("Menu.SerieNames"));
        mntmTranslateShowNames = new JMenuItem(getText("Menu.MappingTvdbScene"));
        mnImportExport = new JMenu(getText("Menu.ImportExport"));
        mnEdit = new JMenu(getText("App.Edit"));
        mnHelp = new JMenu(getText("Menu.Help"));
        mntmExportTranslate = new JMenuItem(getText("Menu.ExportMappingTvdbScene"));
        mntmImportTranslate = new JMenuItem(getText("Menu.ImportMappingTvdbScene"));
        mntmExportPreferences = new JMenuItem(getText("Menu.ExportPreferences"));
        mntmImportPreferences = new JMenuItem(getText("Menu.ImportPreferences"));
        mntmAbout = new JMenuItem(getText("Menu.About"));
        mntmCheckForUpdate = new JMenuItem(getText("Menu.CheckForUpdate"));
    }

    private void addComponentsToMenu() {
        mnFile.add(mntmQuit);
        add(mnFile);

        mnSearchResults.add(chckbxmntmType);
        mnSearchResults.add(chckbxmntmFileName);
        mnSearchResults.add(chckbxmntmTitle);
        mnSearchResults.add(chckbxmntmSeason);
        mnSearchResults.add(chckbxmntmEpisode);
        mnView.add(mnSearchResults);
        mnView.add(chckbxmntmShowOnlyFound);
        mnView.add(mntmClearLog);
        add(mnView);

        mnEdit.add(mntmRenameSerieFiles);
        mnEdit.add(mntmRenameMovieFiles);
        mnEdit.add(mntmPreferences);
        add(mnEdit);

        mnSerieNames.add(mntmTranslateShowNames);
        add(mnSerieNames);

        mnImportExport.add(mntmExportTranslate);
        mnImportExport.add(mntmImportTranslate);
        mnImportExport.add(mntmExportPreferences);
        mnImportExport.add(mntmImportPreferences);
        add(mnImportExport);

        mnHelp.add(mntmCheckForUpdate);
        mnHelp.add(mntmAbout);

        add(mnHelp);

    }

    public Menu withShowOnlyFound(boolean show) {
        chckbxmntmShowOnlyFound.setSelected(show);
        return this;
    }

    public boolean isShowOnlyFound() {
        return chckbxmntmShowOnlyFound.isSelected();
    }

    public boolean isViewFilenameSelected() {
        return chckbxmntmFileName.isSelected();
    }

    public Menu withViewFileNameSelected(boolean arg0) {
        this.chckbxmntmFileName.setSelected(arg0);
        return this;
    }

    public boolean isViewTypeSelected() {
        return chckbxmntmType.isSelected();
    }

    public Menu withViewTypeSelected(boolean arg0) {
        this.chckbxmntmType.setSelected(arg0);
        return this;
    }

    public boolean isViewTitleSelected() {
        return chckbxmntmTitle.isSelected();
    }

    public Menu withViewTitleSelected(boolean arg0) {
        this.chckbxmntmTitle.setSelected(arg0);
        return this;
    }

    public boolean isViewSeasonSelected() {
        return chckbxmntmSeason.isSelected();
    }

    public Menu withViewSeasonSelected(boolean arg0) {
        this.chckbxmntmSeason.setSelected(arg0);
        return this;
    }

    public boolean isViewEpisodeSelected() {
        return chckbxmntmEpisode.isSelected();
    }

    public Menu withViewEpisodeSelected(boolean arg0) {
        this.chckbxmntmEpisode.setSelected(arg0);
        return this;
    }

    public Menu withFileQuitAction(Runnable fileQuitAction) {
        addActionListener(mntmQuit, fileQuitAction);
        return this;
    }

    public Menu withViewFilenameAction(Runnable viewFilenameAction) {
        addActionListener(chckbxmntmFileName, viewFilenameAction);
        return this;
    }

    public Menu withViewTypeAction(Runnable viewTypeAction) {
        addActionListener(chckbxmntmType, viewTypeAction);
        return this;
    }

    public Menu withViewTitleAction(Runnable viewTitleAction) {
        addActionListener(chckbxmntmTitle, viewTitleAction);
        return this;
    }

    public Menu withEditRenameTVAction(Runnable editRenameTVAction) {
        addActionListener(mntmRenameSerieFiles, editRenameTVAction);
        return this;
    }

    public Menu withEditRenameMovieAction(Runnable editRenameMovieAction) {
        addActionListener(mntmRenameMovieFiles, editRenameMovieAction);
        return this;
    }

    public Menu withEditPreferencesAction(Runnable editPreferencesAction) {
        addActionListener(mntmPreferences, editPreferencesAction);
        return this;
    }

    public Menu withExportPreferencesAction(Runnable exportPreferencesAction) {
        addActionListener(mntmExportPreferences, exportPreferencesAction);
        return this;
    }

    public Menu withExportTranslationsAction(Runnable exportTranslationsAction) {
        addActionListener(mntmExportTranslate, exportTranslationsAction);
        return this;
    }

    public Menu withAboutAction(Runnable aboutAction) {
        addActionListener(mntmAbout, _ -> aboutAction.run());
        return this;
    }

    public Menu withCheckUpdateAction(Runnable checkUpdateAction) {
        addActionListener(mntmCheckForUpdate, checkUpdateAction);
        return this;
    }

    public Menu withImportTranslationsAction(Runnable importTranslationsAction) {
        addActionListener(mntmImportTranslate, importTranslationsAction);
        return this;
    }

    public Menu withImportPreferencesAction(Runnable importPreferencesAction) {
        addActionListener(mntmImportPreferences, importPreferencesAction);
        return this;
    }

    public Menu withTranslateShowNamesAction(Runnable translateShowNamesAction) {
        addActionListener(mntmTranslateShowNames, translateShowNamesAction);
        return this;
    }

    public Menu withViewClearLogAction(Runnable viewClearLogAction) {
        addActionListener(mntmClearLog, viewClearLogAction);
        return this;
    }

    public Menu withViewShowOnlyFoundAction(Runnable viewShowOnlyFoundAction) {
        addActionListener(chckbxmntmShowOnlyFound, viewShowOnlyFoundAction);
        return this;
    }

    public Menu withViewEpisodeAction(Runnable viewEpisodeAction) {
        addActionListener(chckbxmntmEpisode, viewEpisodeAction);
        return this;
    }

    public Menu withViewSeasonAction(Runnable viewSeasonAction) {
        addActionListener(chckbxmntmSeason, viewSeasonAction);
        return this;
    }

    private void addActionListener(JMenuItem menuItem, Runnable actionListener) {
        addActionListener(menuItem, _ -> actionListener.run());
    }

    private void addActionListener(JMenuItem menuItem, ActionListener actionListener) {
        if (actionListener != null) {
            menuItem.addActionListener(actionListener);
        }
    }

}
