package org.lodder.subtools.multisubdownloader.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu extends JMenuBar {

  /**
   * 
   */
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
    mnFile = new JMenu("Bestand");
    mntmQuit = new JMenuItem("Afsluiten");
    mnBeeld = new JMenu("Beeld");
    mnZoekResulaten = new JMenu("Zoek Resulaten ");
    chckbxmntmBestandsnaam = new JCheckBoxMenuItem("Bestandsnaam");
    chckbxmntmType = new JCheckBoxMenuItem("Type");
    chckbxmntmTitle = new JCheckBoxMenuItem("Titel");
    chckbxmntmSeason = new JCheckBoxMenuItem("Season");
    chckbxmntmEpisode = new JCheckBoxMenuItem("Episode");
    chckbxmntmAlleenGevondenTonen = new JCheckBoxMenuItem("Alleen gevonden tonen");
    mntmLoggingWissen = new JMenuItem("Logging wissen");
    mntmRenameSerieFiles = new JMenuItem("Series Hernoemen...");
    mntmRenameMovieFiles = new JMenuItem("Films Hernoemen...");
    mntmPreferences = new JMenuItem("Voorkeuren");
    mnImportexport = new JMenu("Serie Namen");
    mntmTranslateShowNames = new JMenuItem("Mapping Tvdb/Scene");
    mnImporteerexporteer = new JMenu("Importeer/Exporteer");
    mnEdit = new JMenu("Bewerken");
    mnHelp = new JMenu("Help");
    mntmExportTranslate = new JMenuItem("Exporteer Mapping Tvdb/Scene");
    mntmImportTranslate = new JMenuItem("Importeer Mapping Tvdb/Scene");
    mntmExporteerUistluitingen = new JMenuItem("Exporteer Uitsluitingen");
    mntmImporteerUitsluitingen = new JMenuItem("Importeer Uitsluitingen");
    mntmExporteerVoorkeuren = new JMenuItem("Exporteer Voorkeuren");
    mntmImporteerVoorkeuren = new JMenuItem("Importeer Voorkeuren");
    mntmAbout = new JMenuItem("About");
    mntmControlerenVoorUpdate = new JMenuItem("Controleer voor update");
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
    mntmQuit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fileQuitAction.actionPerformed(e);
        System.exit(0);
      }
    });

    chckbxmntmBestandsnaam.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        viewFilenameAction.actionPerformed(actionEvent);
      }
    });

    chckbxmntmType.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        viewTypeAction.actionPerformed(arg0);
      }
    });

    chckbxmntmTitle.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        viewTitleAction.actionPerformed(arg0);
      }
    });

    chckbxmntmSeason.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        viewSeasonAction.actionPerformed(arg0);
      }
    });

    chckbxmntmEpisode.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        viewEpisodeAction.actionPerformed(arg0);
      }
    });

    chckbxmntmAlleenGevondenTonen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        viewShowOnlyFoundAction.actionPerformed(arg0);
      }
    });

    mntmLoggingWissen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        viewClearLogAction.actionPerformed(arg0);
      }
    });

    mntmRenameSerieFiles.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        editRenameTVAction.actionPerformed(arg0);
      }
    });

    mntmRenameMovieFiles.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        editRenameMovieAction.actionPerformed(arg0);
      }
    });

    mntmPreferences.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        editPreferencesAction.actionPerformed(arg0);
      }
    });

    mntmTranslateShowNames.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        translateShowNamesAction.actionPerformed(arg0);
      }
    });

    mntmExporteerUistluitingen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        exportExclusionsAction.actionPerformed(arg0);
      }
    });

    mntmImporteerUitsluitingen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        importExclusionsAction.actionPerformed(arg0);
      }
    });

    mntmExporteerVoorkeuren.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        exportPreferencesAction.actionPerformed(arg0);
      }
    });

    mntmImporteerVoorkeuren.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        importPreferencesAction.actionPerformed(arg0);
      }
    });

    mntmImportTranslate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        importTranslationsAction.actionPerformed(arg0);
      }
    });

    mntmExportTranslate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        exportTranslationsAction.actionPerformed(arg0);
      }
    });

    mntmAbout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        aboutAction.actionPerformed(arg0);
      }
    });

    mntmControlerenVoorUpdate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        checkUpdateAction.actionPerformed(arg0);
      }
    });
  }

  public void setShowOnlyFound(boolean show) {
    chckbxmntmAlleenGevondenTonen.setSelected(show);
  }
  
  public boolean isShowOnlyFound(){
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
