package org.lodder.subtools.multisubdownloader.gui;

import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.lodder.subtools.multisubdownloader.Messages;

public class Menu extends JMenuBar {

    private static final long serialVersionUID = -7384297314593169280L;
    private JMenu mnFile;
    private JMenuItem mntmQuit;
    private JMenu mnBeeld;
    private ActionListener fileQuitAction;
    private ActionListener viewFilenameAction;
    private ActionListener viewTypeAction;
    private ActionListener viewTitleAction;
    private JMenu mnZoekResulaten;
    private JCheckBoxMenuItem chckbxmntmBestandsnaam;
    private JCheckBoxMenuItem chckbxmntmType;
    private JCheckBoxMenuItem chckbxmntmTitle;
    private JCheckBoxMenuItem chckbxmntmSeason;
    private JCheckBoxMenuItem chckbxmntmEpisode;
    private JCheckBoxMenuItem chckbxmntmAlleenGevondenTonen;
    private JMenuItem mntmLoggingWissen;
    private JMenu mnEdit;
    private JMenu mnHelp;
    private JMenuItem mntmRenameSerieFiles;
    private JMenuItem mntmRenameMovieFiles;
    private JMenuItem mntmPreferences;
    private JMenu mnImportexport;
    private JMenuItem mntmTranslateShowNames;
    private JMenu mnImporteerexporteer;
    private JMenuItem mntmExportTranslate;
    private JMenuItem mntmImportTranslate;
    private JMenuItem mntmExporteerUistluitingen;
    private JMenuItem mntmImporteerUitsluitingen;
    private JMenuItem mntmExporteerVoorkeuren;
    private JMenuItem mntmImporteerVoorkeuren;
    private JMenuItem mntmAbout;
    private JMenuItem mntmControlerenVoorUpdate;
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
        mnFile = new JMenu(Messages.getString("Menu.File"));
        mntmQuit = new JMenuItem(Messages.getString("Menu.Close"));
        mnBeeld = new JMenu(Messages.getString("Menu.View"));
        mnZoekResulaten = new JMenu(Messages.getString("Menu.SearchResults"));
        chckbxmntmBestandsnaam = new JCheckBoxMenuItem(Messages.getString("Menu.Filename"));
        chckbxmntmType = new JCheckBoxMenuItem(Messages.getString("Menu.Type"));
        chckbxmntmTitle = new JCheckBoxMenuItem(Messages.getString("Menu.Title"));
        chckbxmntmSeason = new JCheckBoxMenuItem(Messages.getString("Menu.Season"));
        chckbxmntmEpisode = new JCheckBoxMenuItem(Messages.getString("Menu.Episode"));
        chckbxmntmAlleenGevondenTonen = new JCheckBoxMenuItem(Messages.getString("Menu.OnlyShowFound"));
        mntmLoggingWissen = new JMenuItem(Messages.getString("Menu.EraseLogging"));
        mntmRenameSerieFiles = new JMenuItem(Messages.getString("Menu.RenameSerie"));
        mntmRenameMovieFiles = new JMenuItem(Messages.getString("Menu.RenameMovie"));
        mntmPreferences = new JMenuItem(Messages.getString("Menu.Preferences"));
        mnImportexport = new JMenu(Messages.getString("Menu.SerieNames"));
        mntmTranslateShowNames = new JMenuItem(Messages.getString("Menu.MappingTvdbScene"));
        mnImporteerexporteer = new JMenu(Messages.getString("Menu.ImportExport"));
        mnEdit = new JMenu(Messages.getString("Menu.Edit"));
        mnHelp = new JMenu(Messages.getString("Menu.Help"));
        mntmExportTranslate = new JMenuItem(Messages.getString("Menu.ExportMappingTvdbScene"));
        mntmImportTranslate = new JMenuItem(Messages.getString("Menu.ImportMappingTvdbScene"));
        mntmExporteerUistluitingen = new JMenuItem(Messages.getString("Menu.ExportExclusions"));
        mntmImporteerUitsluitingen = new JMenuItem(Messages.getString("Menu.ImportExclusions"));
        mntmExporteerVoorkeuren = new JMenuItem(Messages.getString("Menu.ExportPreferences"));
        mntmImporteerVoorkeuren = new JMenuItem(Messages.getString("Menu.ImportPreferences"));
        mntmAbout = new JMenuItem(Messages.getString("Menu.About"));
        mntmControlerenVoorUpdate = new JMenuItem(Messages.getString("Menu.CheckForUpdate"));
    }

    private void addComponentsToMenu() {
        mnFile.add(mntmQuit);
        add(mnFile);

        mnZoekResulaten.add(chckbxmntmType);
        mnZoekResulaten.add(chckbxmntmBestandsnaam);
        mnZoekResulaten.add(chckbxmntmTitle);
        mnZoekResulaten.add(chckbxmntmSeason);
        mnZoekResulaten.add(chckbxmntmEpisode);
        mnBeeld.add(mnZoekResulaten);
        mnBeeld.add(chckbxmntmAlleenGevondenTonen);
        mnBeeld.add(mntmLoggingWissen);
        add(mnBeeld);

        mnEdit.add(mntmRenameSerieFiles);
        mnEdit.add(mntmRenameMovieFiles);
        mnEdit.add(mntmPreferences);
        add(mnEdit);

        mnImportexport.add(mntmTranslateShowNames);
        add(mnImportexport);

        mnImporteerexporteer.add(mntmExportTranslate);
        mnImporteerexporteer.add(mntmImportTranslate);
        mnImporteerexporteer.add(mntmExporteerUistluitingen);
        mnImporteerexporteer.add(mntmImporteerUitsluitingen);
        mnImporteerexporteer.add(mntmExporteerVoorkeuren);
        mnImporteerexporteer.add(mntmImporteerVoorkeuren);
        add(mnImporteerexporteer);

        mnHelp.add(mntmControlerenVoorUpdate);
        mnHelp.add(mntmAbout);

        add(mnHelp);

    }

    private void setupListeners() {
        mntmQuit.addActionListener(e -> {
            if (fileQuitAction != null) {
                fileQuitAction.actionPerformed(e);
            }
        });

        chckbxmntmBestandsnaam.addActionListener(actionEvent -> {
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

        chckbxmntmAlleenGevondenTonen.addActionListener(arg0 -> {
            if (viewShowOnlyFoundAction != null) {
                viewShowOnlyFoundAction.actionPerformed(arg0);
            }
        });

        mntmLoggingWissen.addActionListener(arg0 -> {
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

        mntmExporteerUistluitingen.addActionListener(arg0 -> {
            if (exportExclusionsAction != null) {
                exportExclusionsAction.actionPerformed(arg0);
            }
        });

        mntmImporteerUitsluitingen.addActionListener(arg0 -> {
            if (importExclusionsAction != null) {
                importExclusionsAction.actionPerformed(arg0);
            }
        });

        mntmExporteerVoorkeuren.addActionListener(arg0 -> {
            if (exportPreferencesAction != null) {
                exportPreferencesAction.actionPerformed(arg0);
            }
        });

        mntmImporteerVoorkeuren.addActionListener(arg0 -> {
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

        mntmControlerenVoorUpdate.addActionListener(arg0 -> {
            if (checkUpdateAction != null) {
                checkUpdateAction.actionPerformed(arg0);
            }
        });
    }

    public void setShowOnlyFound(boolean show) {
        chckbxmntmAlleenGevondenTonen.setSelected(show);
    }

    public boolean isShowOnlyFound() {
        return chckbxmntmAlleenGevondenTonen.isSelected();
    }

    public boolean isViewFilenameSelected() {
        return chckbxmntmBestandsnaam.isSelected();
    }

    public void setViewFileNameSelected(boolean arg0) {
        this.chckbxmntmBestandsnaam.setSelected(arg0);
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
