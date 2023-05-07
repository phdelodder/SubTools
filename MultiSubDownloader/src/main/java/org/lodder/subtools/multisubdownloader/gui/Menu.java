package org.lodder.subtools.multisubdownloader.gui;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.lodder.subtools.multisubdownloader.Messages;

import java.awt.event.ActionListener;
import java.io.Serial;

public class Menu extends JMenuBar {

    @Serial
    private static final long serialVersionUID = -7384297314593169280L;
    private JMenu mnFile;
    private JMenuItem mntmQuit;
    private JMenu mnView;
    private ActionListener fileQuitAction;
    private ActionListener viewFilenameAction;
    private ActionListener viewTypeAction;
    private ActionListener viewTitleAction;
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
    protected ActionListener viewSeasonAction;
    protected ActionListener viewEpisodeAction;
    protected ActionListener viewShowOnlyFoundAction;
    protected ActionListener viewClearLogAction;
    protected ActionListener editRenameTVAction;
    protected ActionListener editRenameMovieAction;
    protected ActionListener editPreferencesAction;
    protected ActionListener translateShowNamesAction;
    protected ActionListener exportExclusionsAction;
    protected ActionListener importPreferencesAction;
    protected ActionListener importTranslationsAction;
    protected ActionListener exportPreferencesAction;
    protected ActionListener exportTranslationsAction;
    protected ActionListener aboutAction;
    protected ActionListener checkUpdateAction;
    protected ActionListener importExclusionsAction;

    public Menu() {
        createComponents();
        setupListeners();
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

    private void setupListeners() {
        mntmQuit.addActionListener(e -> {
            if (fileQuitAction != null) {
                fileQuitAction.actionPerformed(e);
            }
        });

        chckbxmntmFileName.addActionListener(actionEvent -> {
            if (viewFilenameAction != null) {
                viewFilenameAction.actionPerformed(actionEvent);
            }
        });

        chckbxmntmType.addActionListener(arg0 -> {
            if (viewTypeAction != null) {
                viewTypeAction.actionPerformed(arg0);
            }
        });

        chckbxmntmTitle.addActionListener(arg0 -> {
            if (viewTitleAction != null) {
                viewTitleAction.actionPerformed(arg0);
            }
        });

        chckbxmntmSeason.addActionListener(arg0 -> {
            if (viewSeasonAction != null) {
                viewSeasonAction.actionPerformed(arg0);
            }
        });

        chckbxmntmEpisode.addActionListener(arg0 -> {
            if (viewEpisodeAction != null) {
                viewEpisodeAction.actionPerformed(arg0);
            }
        });

        chckbxmntmShowOnlyFound.addActionListener(arg0 -> {
            if (viewShowOnlyFoundAction != null) {
                viewShowOnlyFoundAction.actionPerformed(arg0);
            }
        });

        mntmClearLog.addActionListener(arg0 -> {
            if (viewClearLogAction != null) {
                viewClearLogAction.actionPerformed(arg0);
            }
        });

        mntmRenameSerieFiles.addActionListener(arg0 -> {
            if (editRenameTVAction != null) {
                editRenameTVAction.actionPerformed(arg0);
            }
        });

        mntmRenameMovieFiles.addActionListener(arg0 -> {
            if (editRenameMovieAction != null) {
                editRenameMovieAction.actionPerformed(arg0);
            }
        });

        mntmPreferences.addActionListener(arg0 -> {
            if (editPreferencesAction != null) {
                editPreferencesAction.actionPerformed(arg0);
            }
        });

        mntmTranslateShowNames.addActionListener(arg0 -> {
            if (translateShowNamesAction != null) {
                translateShowNamesAction.actionPerformed(arg0);
            }
        });

        mntmExportExclusions.addActionListener(arg0 -> {
            if (exportExclusionsAction != null) {
                exportExclusionsAction.actionPerformed(arg0);
            }
        });

        mntmImportExclusions.addActionListener(arg0 -> {
            if (importExclusionsAction != null) {
                importExclusionsAction.actionPerformed(arg0);
            }
        });

        mntmExportPreferences.addActionListener(arg0 -> {
            if (exportPreferencesAction != null) {
                exportPreferencesAction.actionPerformed(arg0);
            }
        });

        mntmImportPreferences.addActionListener(arg0 -> {
            if (importPreferencesAction != null) {
                importPreferencesAction.actionPerformed(arg0);
            }
        });

        mntmImportTranslate.addActionListener(arg0 -> {
            if (importTranslationsAction != null) {
                importTranslationsAction.actionPerformed(arg0);
            }
        });

        mntmExportTranslate.addActionListener(arg0 -> {
            if (exportTranslationsAction != null) {
                exportTranslationsAction.actionPerformed(arg0);
            }
        });

        mntmAbout.addActionListener(arg0 -> {
            if (aboutAction != null) {
                aboutAction.actionPerformed(arg0);
            }
        });

        mntmCheckForUpdate.addActionListener(arg0 -> {
            if (checkUpdateAction != null) {
                checkUpdateAction.actionPerformed(arg0);
            }
        });
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

    public void setFileQuitAction(ActionListener fileQuitAction) {
        this.fileQuitAction = fileQuitAction;
    }

    public void setViewFilenameAction(ActionListener viewFilenameAction) {
        this.viewFilenameAction = viewFilenameAction;
    }

    public void setViewTypeAction(ActionListener viewTypeAction) {
        this.viewTypeAction = viewTypeAction;
    }

    public void setViewTitleAction(ActionListener viewTitleAction) {
        this.viewTitleAction = viewTitleAction;
    }

    public void setEditRenameTVAction(ActionListener editRenameTVAction) {
        this.editRenameTVAction = editRenameTVAction;
    }

    public void setEditRenameMovieAction(ActionListener editRenameMovieAction) {
        this.editRenameMovieAction = editRenameMovieAction;
    }

    public void setEditPreferencesAction(ActionListener editPreferencesAction) {
        this.editPreferencesAction = editPreferencesAction;
    }

    public void setExportExclusionsAction(ActionListener exportExclusionsAction) {
        this.exportExclusionsAction = exportExclusionsAction;
    }

    public void setExportPreferencesAction(ActionListener exportPreferencesAction) {
        this.exportPreferencesAction = exportPreferencesAction;
    }

    public void setExportTranslationsAction(ActionListener exportTranslationsAction) {
        this.exportTranslationsAction = exportTranslationsAction;
    }

    public void setAboutAction(ActionListener aboutAction) {
        this.aboutAction = aboutAction;
    }

    public void setCheckUpdateAction(ActionListener checkUpdateAction) {
        this.checkUpdateAction = checkUpdateAction;
    }

    public void setImportTranslationsAction(ActionListener importTranslationsAction) {
        this.importTranslationsAction = importTranslationsAction;
    }

    public void setImportPreferencesAction(ActionListener importPreferencesAction) {
        this.importPreferencesAction = importPreferencesAction;
    }

    public void setImportExclusionsAction(ActionListener importExclusionsAction) {
        this.importExclusionsAction = importExclusionsAction;
    }

    public void setTranslateShowNamesAction(ActionListener translateShowNamesAction) {
        this.translateShowNamesAction = translateShowNamesAction;
    }

    public void setViewClearLogAction(ActionListener viewClearLogAction) {
        this.viewClearLogAction = viewClearLogAction;
    }

    public void setViewShowOnlyFoundAction(ActionListener viewShowOnlyFoundAction) {
        this.viewShowOnlyFoundAction = viewShowOnlyFoundAction;
    }

    public void setViewEpisodeAction(ActionListener viewEpisodeAction) {
        this.viewEpisodeAction = viewEpisodeAction;
    }

    public void setViewSeasonAction(ActionListener viewSeasonAction) {
        this.viewSeasonAction = viewSeasonAction;
    }

}
