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
    private JMenuItem mntmExportExclusions;
    private JMenuItem mntmImportExclusions;
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
        mntmExportExclusions = new JMenuItem(Messages.getString("Menu.ExportExclusions"));
        mntmImportExclusions = new JMenuItem(Messages.getString("Menu.ImportExclusions"));
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
        mnImportExport.add(mntmExportExclusions);
        mnImportExport.add(mntmImportExclusions);
        mnImportExport.add(mntmExportPreferences);
        mnImportExport.add(mntmImportPreferences);
        add(mnImportExport);

        mnHelp.add(mntmCheckForUpdate);
        mnHelp.add(mntmAbout);

        add(mnHelp);

    }

    public void setShowOnlyFound(boolean show) {
        chckbxmntmShowOnlyFound.setSelected(show);
    }

    public boolean isShowOnlyFound() {
        return chckbxmntmShowOnlyFound.isSelected();
    }

    public boolean isViewFilenameSelected() {
        return chckbxmntmFileName.isSelected();
    }

    public void setViewFileNameSelected(boolean arg0) {
        this.chckbxmntmFileName.setSelected(arg0);
    }

    public boolean isViewTypeSelected() {
        return chckbxmntmType.isSelected();
    }

    public void setViewTypeSelected(boolean arg0) {
        this.chckbxmntmType.setSelected(arg0);
    }

    public boolean isViewTitleSelected() {
        return chckbxmntmTitle.isSelected();
    }

    public void setViewTitleSelected(boolean arg0) {
        this.chckbxmntmTitle.setSelected(arg0);
    }

    public boolean isViewSeasonSelected() {
        return chckbxmntmSeason.isSelected();
    }

    public void setViewSeasonSelected(boolean arg0) {
        this.chckbxmntmSeason.setSelected(arg0);
    }

    public boolean isViewEpisodeSelected() {
        return chckbxmntmEpisode.isSelected();
    }

    public void setViewEpisodeSelected(boolean arg0) {
        this.chckbxmntmEpisode.setSelected(arg0);
    }

    private void addActionListener(JMenuItem menuItem, ActionListener actionListener) {
        menuItem.addActionListener(actionEvent -> {
            if (actionListener != null) {
                actionListener.actionPerformed(actionEvent);
            }
        });
    }

    public void setFileQuitAction(ActionListener fileQuitAction) {
        addActionListener(mntmQuit, fileQuitAction);
    }

    public void setViewFilenameAction(ActionListener viewFilenameAction) {
        addActionListener(chckbxmntmFileName, viewFilenameAction);
    }

    public void setViewTypeAction(ActionListener viewTypeAction) {
        addActionListener(chckbxmntmType, viewTypeAction);
    }

    public void setViewTitleAction(ActionListener viewTitleAction) {
        addActionListener(chckbxmntmTitle, viewTitleAction);
    }

    public void setEditRenameTVAction(ActionListener editRenameTVAction) {
        addActionListener(mntmRenameSerieFiles, editRenameTVAction);
    }

    public void setEditRenameMovieAction(ActionListener editRenameMovieAction) {
        addActionListener(mntmRenameMovieFiles, editRenameMovieAction);
    }

    public void setEditPreferencesAction(ActionListener editPreferencesAction) {
        addActionListener(mntmPreferences, editPreferencesAction);
    }

    public void setExportExclusionsAction(ActionListener exportExclusionsAction) {
        addActionListener(mntmExportExclusions, exportExclusionsAction);
    }

    public void setExportPreferencesAction(ActionListener exportPreferencesAction) {
        addActionListener(mntmExportPreferences, exportPreferencesAction);
    }

    public void setExportTranslationsAction(ActionListener exportTranslationsAction) {
        addActionListener(mntmExportTranslate, exportTranslationsAction);
    }

    public void setAboutAction(ActionListener aboutAction) {
        addActionListener(mntmAbout, aboutAction);
    }

    public void setCheckUpdateAction(ActionListener checkUpdateAction) {
        addActionListener(mntmCheckForUpdate, checkUpdateAction);
    }

    public void setImportTranslationsAction(ActionListener importTranslationsAction) {
        addActionListener(mntmImportTranslate, importTranslationsAction);
    }

    public void setImportPreferencesAction(ActionListener importPreferencesAction) {
        addActionListener(mntmImportPreferences, importPreferencesAction);
    }

    public void setImportExclusionsAction(ActionListener importExclusionsAction) {
        addActionListener(mntmImportExclusions, importExclusionsAction);
    }

    public void setTranslateShowNamesAction(ActionListener translateShowNamesAction) {
        addActionListener(mntmTranslateShowNames, translateShowNamesAction);
    }

    public void setViewClearLogAction(ActionListener viewClearLogAction) {
        addActionListener(mntmClearLog, viewClearLogAction);
    }

    public void setViewShowOnlyFoundAction(ActionListener viewShowOnlyFoundAction) {
        addActionListener(chckbxmntmShowOnlyFound, viewShowOnlyFoundAction);
    }

    public void setViewEpisodeAction(ActionListener viewEpisodeAction) {
        addActionListener(chckbxmntmEpisode, viewEpisodeAction);
    }

    public void setViewSeasonAction(ActionListener viewSeasonAction) {
        addActionListener(chckbxmntmSeason, viewSeasonAction);
    }
}
