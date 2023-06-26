package org.lodder.subtools.multisubdownloader.gui;

import java.io.Serial;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.lodder.subtools.multisubdownloader.Messages;

import java.awt.event.ActionListener;

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
        mnFile = new JMenu(Messages.getString("Menu.Path"));
        mntmQuit = new JMenuItem(Messages.getString("App.Close"));
        mnView = new JMenu(Messages.getString("Menu.View"));
        mnSearchResults = new JMenu(Messages.getString("Menu.SearchResults"));
        chckbxmntmFileName = new JCheckBoxMenuItem(Messages.getString("Menu.Filename"));
        chckbxmntmType = new JCheckBoxMenuItem(Messages.getString("Menu.Type"));
        chckbxmntmTitle = new JCheckBoxMenuItem(Messages.getString("Menu.Title"));
        chckbxmntmSeason = new JCheckBoxMenuItem(Messages.getString("App.Season"));
        chckbxmntmEpisode = new JCheckBoxMenuItem(Messages.getString("App.Episode"));
        chckbxmntmShowOnlyFound = new JCheckBoxMenuItem(Messages.getString("Menu.OnlyShowFound"));
        mntmClearLog = new JMenuItem(Messages.getString("Menu.EraseLogging"));
        mntmRenameSerieFiles = new JMenuItem(Messages.getString("Menu.RenameSerie"));
        mntmRenameMovieFiles = new JMenuItem(Messages.getString("Menu.RenameMovie"));
        mntmPreferences = new JMenuItem(Messages.getString("Menu.Preferences"));
        mnSerieNames = new JMenu(Messages.getString("Menu.SerieNames"));
        mntmTranslateShowNames = new JMenuItem(Messages.getString("Menu.MappingTvdbScene"));
        mnImportExport = new JMenu(Messages.getString("Menu.ImportExport"));
        mnEdit = new JMenu(Messages.getString("App.Edit"));
        mnHelp = new JMenu(Messages.getString("Menu.Help"));
        mntmExportTranslate = new JMenuItem(Messages.getString("Menu.ExportMappingTvdbScene"));
        mntmImportTranslate = new JMenuItem(Messages.getString("Menu.ImportMappingTvdbScene"));
        mntmExportPreferences = new JMenuItem(Messages.getString("Menu.ExportPreferences"));
        mntmImportPreferences = new JMenuItem(Messages.getString("Menu.ImportPreferences"));
        mntmAbout = new JMenuItem(Messages.getString("Menu.About"));
        mntmCheckForUpdate = new JMenuItem(Messages.getString("Menu.CheckForUpdate"));
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
        addActionListener(mntmAbout, arg -> aboutAction.run());
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
        addActionListener(menuItem, arg -> actionListener.run());
    }

    private void addActionListener(JMenuItem menuItem, ActionListener actionListener) {
        if (actionListener != null) {
            menuItem.addActionListener(actionListener::actionPerformed);
        }
    }

}
