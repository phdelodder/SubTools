package org.lodder.subtools.multisubdownloader.gui.dialog;

import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.multisubdownloader.gui.extra.*;
import org.lodder.subtools.multisubdownloader.gui.panels.EpisodeLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.MovieLibraryPanel;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.*;
import org.lodder.subtools.sublibrary.control.VideoPatterns;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PreferenceDialog extends MutliSubDialog {

  /**
     *
     */
  private static final long serialVersionUID = -5730220264781738564L;
  private final JPanel contentPanel = new JPanel();
  private JCheckBox chkOnlyFound, chkAlwaysConfirm, chkSubtitleExactMethod,
      chkSubtitleKeywordMethod;
  private SettingsControl settingsCtrl;
  private EpisodeLibraryPanel pnlEpisodeLibrary;
  private JListWithImages excludeList;
  private JCheckBox chkStopOnSearchError;
  private JCheckBox chkAutomaticDownloadSelection;
  private JTable table;
  private JCheckBox chkNoRuleMatchTakeFirst;
  private MovieLibraryPanel pnlMovieLibrary;
  private JTextField txtProxyHost, txtAddic7edUsername;
  private JTextField txtProxyPort, txtAddic7edPassword;
  private JCheckBox chkProxyserverGebruiken, chkUserAddic7edLogin, chkExcludeHearingImpaired;
  private JListWithImages defaultIncomingFoldersList, localSourcesFoldersList;
  private JCheckBox chkSerieSourceAddic7ed, chkSerieSourceTvSubtitles, chkSerieSourcePodnapisi,
      chkSerieSourceOpensubtitles, chkSerieSourceLocal, chkSerieSourcePrivateRepo,
      chkSerieSourceSubsMax;
  private JComboBox<SettingsProcessEpisodeSource> cbxEpisodeProcessSource;
  private JComboBox<Integer> cbxPriorityLocal, cbxPriorityPodnapisi, cbxPriorityAddic7ed,
      cbxPriorityTvSubtitles, cbxPriorityOpensubtitles, cbxPriorityPrivateRepo, cbxPrioritySubsMax;
  private JCheckBox chkAutomaticSelectionQuality;
  private JCheckBox chkAutomaticSelectionTeam;
  private JButton btnAddExtraRule;
  private JButton btnRemoveSelected;
  private JScrollPane scrollPane_1;
  private ArrowButton btnNaarBovenPlaatsen;
  private ArrowButton btnNaarBenedenPlaatsen;

  /**
   * Create the dialog.
   */
  public PreferenceDialog(JFrame frame, final SettingsControl settingsCtrl) {
    super(frame, "Voorkeuren", true);
    this.settingsCtrl = settingsCtrl;
    initialize();
    initializeTable();
    setPreferenceSettings();
    repaint();
  }

  private void initializeTable() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();

    // Add some columns
    model.addColumn("Regel Nummer");
    model.addColumn("Kwaliteit");
  }

  private void addRuleRow(String q) {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.addRow(new Object[] {model.getRowCount() + 1, q});
    // These are the combobox values
    List<String> both =
        new ArrayList<String>(1 + VideoPatterns.QUALITYKEYWORDS.length
            + VideoPatterns.GROUPEDQUALITYKEYWORDS.length);
    Collections.addAll(both, "Selecteer...");
    // additional grouped keywords
    Collections.addAll(both, VideoPatterns.GROUPEDQUALITYKEYWORDS);
    // keywords
    Collections.addAll(both, VideoPatterns.QUALITYKEYWORDS);
    String[] values = both.toArray(new String[both.size()]);

    // Set the combobox editor on the 1st visible column
    int vColIndex = 1;
    TableColumn col = table.getColumnModel().getColumn(vColIndex);
    col.setCellEditor(new MyComboBoxEditor(values));
    // If the cell should appear like a combobox in its
    // non-editing state, also set the combobox renderer
    col.setCellRenderer(new MyComboBoxRenderer(values));
  }

  private void removeRuleRow() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    if (table.getSelectedRow() >= 0) {
      model.removeRow(table.getSelectedRow());
    }

  }

  private void initialize() {
    setResizable(false);
    setModalityType(ModalityType.APPLICATION_MODAL);
    setBounds(100, 100, 650, 700);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
      tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      contentPanel.add(tabbedPane);
      {
        JPanel pnlGeneral = new JPanel();
        tabbedPane.addTab("Algmeen", null, pnlGeneral, null);
        pnlGeneral.setLayout(new MigLayout("", "[127px,grow][grow][grow]",
            "[23px][grow][][][grow,center][grow]"));
        {
          JLabel lblDefaultIncomingFolder = new JLabel("Standaard inkomende map");
          pnlGeneral.add(lblDefaultIncomingFolder, "cell 0 0,alignx left,aligny center");
        }
        {
          JButton btnBrowse = new JButton("map toevoegen");
          btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              File path =
                  MemoryFolderChooser.getInstance().selectDirectory(getContentPane(),
                      "Selecteer map");
              if (defaultIncomingFoldersList.getModel().getSize() == 0) {
                defaultIncomingFoldersList.addItem(SettingsExcludeType.FOLDER,
                    path.getAbsolutePath());
              } else {
                boolean exists = false;
                for (int i = 0; i < defaultIncomingFoldersList.getModel().getSize(); i++) {
                  if (defaultIncomingFoldersList.getDescription(i).equals(path.getAbsolutePath())) {
                    exists = true;
                  }
                }
                if (!exists) {
                  defaultIncomingFoldersList.addItem(SettingsExcludeType.FOLDER,
                      path.getAbsolutePath());
                }
              }
            }
          });
          pnlGeneral.add(btnBrowse, "cell 1 0,alignx left,aligny top");
        }
        {
          JButton button = new JButton("map verwijderen");
          button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              DefaultListModel<JPanel> model =
                  (DefaultListModel<JPanel>) defaultIncomingFoldersList.getModel();
              int selected = defaultIncomingFoldersList.getSelectedIndex();
              if (model.size() > 0 && selected >= 0) {
                model.removeElementAt(selected);
              }
            }
          });
          pnlGeneral.add(button, "cell 2 0");
        }
        {
          JScrollPane scrollPane = new JScrollPane();
          pnlGeneral.add(scrollPane, "cell 1 1 2 1,grow");
          {
            defaultIncomingFoldersList = new JListWithImages();
            scrollPane.setViewportView(defaultIncomingFoldersList);
          }
        }
        {
          JLabel lblUitsluitLijst = new JLabel("Uitsluit lijst");
          pnlGeneral.add(lblUitsluitLijst, "cell 0 2,alignx right,gaptop 10");
        }
        {
          JButton btnAddUitsluitMap = new JButton("map toevoegen");
          btnAddUitsluitMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              addExcludeItem(SettingsExcludeType.FOLDER);
            }
          });
          pnlGeneral.add(btnAddUitsluitMap, "cell 1 2,alignx center,gaptop 10");
        }
        {
          JButton btnVerwijderUitsluitMap = new JButton("item verwijderen");
          btnVerwijderUitsluitMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              removeExcludeItem();
            }
          });
          pnlGeneral.add(btnVerwijderUitsluitMap, "cell 2 2,alignx center,gaptop 10");
        }
        {
          JButton btnAddUitsluitRegex = new JButton("regex toevoegen");
          btnAddUitsluitRegex.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              addExcludeItem(SettingsExcludeType.REGEX);
            }
          });
          pnlGeneral.add(btnAddUitsluitRegex, "cell 1 3,alignx center");
        }
        {
          JScrollPane scrollPane = new JScrollPane();
          pnlGeneral.add(scrollPane, "cell 1 4 2 1,grow");
          {
            excludeList = new JListWithImages();

            scrollPane.setViewportView(excludeList);
          }
        }
        {
          JPanel pnlProxySettings = new JPanel();
          pnlGeneral.add(pnlProxySettings, "cell 0 5 3 1,grow");
          pnlProxySettings.setLayout(new MigLayout("", "[50px:n][][grow][grow]", "[][][][]"));
          pnlProxySettings.add(new JLabel("Proxy Instellingen"), "cell 0 0 4 1,gapy 5");
          pnlProxySettings.add(new JSeparator(), "cell 0 0 4 1,growx,gapy 5");
          {
            chkProxyserverGebruiken = new JCheckBox("Proxyserver gebruiken");
            pnlProxySettings.add(chkProxyserverGebruiken, "cell 0 1 3 1");
          }
          {
            JLabel lblProxyHost = new JLabel("Adres");
            pnlProxySettings.add(lblProxyHost, "cell 1 2,alignx trailing");
          }
          {
            txtProxyHost = new JTextField();
            pnlProxySettings.add(txtProxyHost, "cell 2 2,growx");
            txtProxyHost.setColumns(10);
          }
          {
            JLabel lblProxyPoort = new JLabel("Poort");
            pnlProxySettings.add(lblProxyPoort, "cell 1 3,alignx trailing");
          }
          {
            txtProxyPort = new JTextField();
            pnlProxySettings.add(txtProxyPort, "cell 2 3,growx");
            txtProxyPort.setColumns(10);
          }

        }
      }

      {
        pnlEpisodeLibrary =
            new EpisodeLibraryPanel(settingsCtrl.getSettings().getEpisodeLibrarySettings());
        tabbedPane.addTab("Serie Bibliotheek", null, pnlEpisodeLibrary, null);
      }
      {
        pnlMovieLibrary =
            new MovieLibraryPanel(settingsCtrl.getSettings().getMovieLibrarySettings());
        tabbedPane.addTab("Film Bibliotheek", null, pnlMovieLibrary, null);
      }
      {
        JPanel pnlOptions = new JPanel();
        tabbedPane.addTab("Opties", null, pnlOptions, null);
        pnlOptions.setLayout(new MigLayout("", "[][433px,grow][433px][100px,grow][]",
            "[][][][][][grow][grow][][][][][25px][][][][][23px][][23px][][]"));
        pnlOptions.add(new JLabel("Download opties"), "cell 0 0 5 1");
        pnlOptions.add(new JSeparator(), "cell 0 0 5 1,growx");
        chkAlwaysConfirm = new JCheckBox("Controleer altijd voor het downloaden");
        pnlOptions.add(chkAlwaysConfirm, "cell 1 1 3 1,grow");
        chkAutomaticDownloadSelection = new JCheckBox("Automatische selectie regels");
        chkAutomaticDownloadSelection.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {}
        });
        pnlOptions.add(chkAutomaticDownloadSelection, "cell 1 2 3 1");
        {
          btnAddExtraRule = new JButton("Extra regel toevoegen");
          btnAddExtraRule.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              addRuleRow("");
            }
          });
          {
            chkAutomaticSelectionQuality = new JCheckBox("Op basis van kwaliteit");
            pnlOptions.add(chkAutomaticSelectionQuality, "cell 2 3 2 1");
          }
          pnlOptions.add(btnAddExtraRule, "cell 2 4,alignx right");
        }
        btnRemoveSelected = new JButton("Geselecteerde regel verwijderen");
        btnRemoveSelected.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            removeRuleRow();
          }
        });
        pnlOptions.add(btnRemoveSelected, "cell 3 4");
        {
          scrollPane_1 = new JScrollPane();
          pnlOptions.add(scrollPane_1, "cell 2 5 2 2,grow");
          table = new JTable();
          scrollPane_1.setViewportView(table);
        }
        {
          btnNaarBovenPlaatsen = new ArrowButton(SwingConstants.NORTH, 1, 10);
          btnNaarBovenPlaatsen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              moveRuleRowUp();
            }
          });
          pnlOptions.add(btnNaarBovenPlaatsen, "cell 4 5");
        }
        {
          btnNaarBenedenPlaatsen = new ArrowButton(SwingConstants.SOUTH, 1, 10);
          btnNaarBenedenPlaatsen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              moveRuleRowDown();
            }
          });
          pnlOptions.add(btnNaarBenedenPlaatsen, "cell 4 6");
        }
        {
          chkAutomaticSelectionTeam = new JCheckBox("Op basis van release group");
          pnlOptions.add(chkAutomaticSelectionTeam, "cell 2 7 2 1");
        }
        {
          chkNoRuleMatchTakeFirst =
              new JCheckBox("Neem de eerste ondertitel als geen enkel regel resultaat heeft.");
          pnlOptions.add(chkNoRuleMatchTakeFirst, "cell 2 8 3 1");
        }
        pnlOptions.add(new JLabel("Zoek filter"), "cell 0 10 5 1");
        pnlOptions.add(new JSeparator(), "cell 0 10 5 1,growx");
        chkSubtitleExactMethod =
            new JCheckBox(
                "Exacte (werkt enkel als naamgeving identiek is) [werkte niet met Addic7ed]");
        pnlOptions.add(chkSubtitleExactMethod, "cell 1 11 3 1,grow");
        {
          chkSubtitleKeywordMethod =
              new JCheckBox(
                  "Keywords (op basis van woorden zoals 720p, xvid, ...) [werkt met iedere source]");
          pnlOptions.add(chkSubtitleKeywordMethod, "cell 1 12 3 1");
        }
        {
          chkExcludeHearingImpaired = new JCheckBox("Hearing Impaired uitsluiten");
          pnlOptions.add(chkExcludeHearingImpaired, "cell 1 14 3 1");
        }
        pnlOptions.add(new JLabel("Tabel opties"), "cell 0 15 5 1");
        pnlOptions.add(new JSeparator(), "cell 0 15 5 1,growx");
        chkOnlyFound = new JCheckBox("Alleen gevonden tonen");
        pnlOptions.add(chkOnlyFound, "cell 1 16 3 1,growx,aligny center");
        pnlOptions.add(new JLabel("Fout afhandeling opties"), "cell 0 17 5 1");
        pnlOptions.add(new JSeparator(), "cell 0 17 5 1,growx");
        chkStopOnSearchError = new JCheckBox("Stop zoeken na fout");
        pnlOptions.add(chkStopOnSearchError, "cell 1 18 3 1,alignx left,aligny center");
        {
          JLabel label = new JLabel("Serie database source");
          pnlOptions.add(label, "cell 0 19 5 1");
          pnlOptions.add(new JSeparator(), "cell 0 19 5 1,growx");
        }
        {
          cbxEpisodeProcessSource =
              new JComboBox<SettingsProcessEpisodeSource>(SettingsProcessEpisodeSource.values());
          // cbxEpisodeProcessSource = new JComboBox<SettingsProcessEpisodeSource>();
          cbxEpisodeProcessSource.setEnabled(false);
          pnlOptions.add(cbxEpisodeProcessSource, "cell 1 20,growx");
        }
      }
      {
        JPanel pnlSerieSources = new JPanel();
        Integer[] prio = new Integer[] {1, 2, 3, 4, 5, 6, 7};
        tabbedPane.addTab("Serie Sources", null, pnlSerieSources, null);
        pnlSerieSources.setLayout(new MigLayout("", "[grow]", "[][top][]"));
        JPanel pnlSerieSourcesSelectionSettings = new JPanel();
        pnlSerieSources.add(pnlSerieSourcesSelectionSettings, "cell 0 0 3 1,grow");
        pnlSerieSourcesSelectionSettings.setLayout(new MigLayout("",
            "[50px:n][][100.00,grow][grow][grow]", "[][][][][][][][]"));
        pnlSerieSourcesSelectionSettings.add(new JLabel(
            "Selecteer de gewenste sources en bepaalde volgorde"), "cell 0 0 5 1,gapy 5");
        pnlSerieSourcesSelectionSettings.add(new JSeparator(), "cell 0 0 5 1,growx,gapy 5");
        chkSerieSourceAddic7ed = new JCheckBox("Addic7ed");
        pnlSerieSourcesSelectionSettings.add(chkSerieSourceAddic7ed, "cell 0 1 2 1");
        {
          cbxPriorityAddic7ed = new JComboBox<Integer>();
          cbxPriorityAddic7ed.setModel(new DefaultComboBoxModel<Integer>(prio));
          pnlSerieSourcesSelectionSettings.add(cbxPriorityAddic7ed, "cell 2 1,growx");
        }
        chkSerieSourceTvSubtitles = new JCheckBox("Tv Subtitles");
        pnlSerieSourcesSelectionSettings.add(chkSerieSourceTvSubtitles, "cell 0 2 2 1");
        {
          cbxPriorityTvSubtitles = new JComboBox<Integer>();
          cbxPriorityTvSubtitles.setModel(new DefaultComboBoxModel<Integer>(prio));
          pnlSerieSourcesSelectionSettings.add(cbxPriorityTvSubtitles, "cell 2 2,growx");
        }
        chkSerieSourcePodnapisi = new JCheckBox("Podnapisi");
        pnlSerieSourcesSelectionSettings.add(chkSerieSourcePodnapisi, "cell 0 3 2 1");
        {
          cbxPriorityPodnapisi = new JComboBox<Integer>();
          cbxPriorityPodnapisi.setModel(new DefaultComboBoxModel<Integer>(prio));
          pnlSerieSourcesSelectionSettings.add(cbxPriorityPodnapisi, "cell 2 3,growx");
        }
        chkSerieSourceOpensubtitles = new JCheckBox("Opensubtitles");
        pnlSerieSourcesSelectionSettings.add(chkSerieSourceOpensubtitles, "cell 0 4 2 1");
        {
          cbxPriorityOpensubtitles = new JComboBox<Integer>();
          cbxPriorityOpensubtitles.setModel(new DefaultComboBoxModel<Integer>(prio));
          pnlSerieSourcesSelectionSettings.add(cbxPriorityOpensubtitles, "cell 2 4,growx");
        }
        chkSerieSourcePrivateRepo = new JCheckBox("Private Repo");
        pnlSerieSourcesSelectionSettings.add(chkSerieSourcePrivateRepo, "cell 0 5");
        {
          cbxPriorityPrivateRepo = new JComboBox<Integer>();
          cbxPriorityPrivateRepo.setModel(new DefaultComboBoxModel<Integer>(prio));
          pnlSerieSourcesSelectionSettings.add(cbxPriorityPrivateRepo, "cell 2 5,growx");
        }
        chkSerieSourceLocal = new JCheckBox("Lokaal");
        pnlSerieSourcesSelectionSettings.add(chkSerieSourceLocal, "cell 0 6 2 1");
        {
          cbxPriorityLocal = new JComboBox<Integer>();
          cbxPriorityLocal.setModel(new DefaultComboBoxModel<Integer>(prio));
          pnlSerieSourcesSelectionSettings.add(cbxPriorityLocal, "cell 2 6,growx");
        }
        {
          chkSerieSourceSubsMax = new JCheckBox("SubsMax");
          pnlSerieSourcesSelectionSettings.add(chkSerieSourceSubsMax, "cell 0 7");
        }
        {
          cbxPrioritySubsMax = new JComboBox<Integer>();
          cbxPrioritySubsMax.setModel(new DefaultComboBoxModel<Integer>(prio));
          pnlSerieSourcesSelectionSettings.add(cbxPrioritySubsMax, "cell 2 7,growx");
        }
        JPanel pnlAddic7edLoginSettings = new JPanel();
        pnlSerieSources.add(pnlAddic7edLoginSettings, "cell 0 1 3 1,grow");
        pnlAddic7edLoginSettings.setLayout(new MigLayout("", "[50px:n][][grow][grow]", "[][][][]"));
        pnlAddic7edLoginSettings.add(new JLabel("Addic7ed Login"), "cell 0 0 4 1,gapy 5");
        pnlAddic7edLoginSettings.add(new JSeparator(), "cell 0 0 4 1,growx,gapy 5");
        {
          chkUserAddic7edLogin = new JCheckBox("Addic7ed login gebruiken");
          pnlAddic7edLoginSettings.add(chkUserAddic7edLogin, "cell 0 1 3 1");
        }
        {
          JLabel lblUsername = new JLabel("Gebruikersnaam");
          pnlAddic7edLoginSettings.add(lblUsername, "cell 1 2,alignx trailing");
        }
        {
          txtAddic7edUsername = new JTextField();
          pnlAddic7edLoginSettings.add(txtAddic7edUsername, "cell 2 2,growx");
          txtAddic7edUsername.setColumns(10);
        }
        {
          JLabel lblAddic7edPassword = new JLabel("Paswoord");
          pnlAddic7edLoginSettings.add(lblAddic7edPassword, "cell 1 3,alignx trailing");
        }
        {
          txtAddic7edPassword = new JTextField();
          pnlAddic7edLoginSettings.add(txtAddic7edPassword, "cell 2 3,growx");
          txtAddic7edPassword.setColumns(10);
        }
        JPanel pnlLocalSourcesSettings = new JPanel();
        pnlSerieSources.add(pnlLocalSourcesSettings, "cell 0 2 3 1,grow");
        pnlLocalSourcesSettings.setLayout(new MigLayout("", "[][][][grow]", "[][][]"));
        pnlLocalSourcesSettings.add(new JLabel("Lokale folders"), "cell 0 0 5 1,gapy 5");
        pnlLocalSourcesSettings.add(new JSeparator(), "cell 0 0 5 1,growx,gapy 5");

        {
          JLabel lblLocalSources = new JLabel("Lokale mappen met ondertitels");
          pnlLocalSourcesSettings.add(lblLocalSources, "cell 0 1,alignx left,aligny center");
        }
        {
          JButton btnBrowseLocalSources = new JButton("map toevoegen");
          btnBrowseLocalSources.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              File path =
                  MemoryFolderChooser.getInstance().selectDirectory(getContentPane(),
                      "Selecteer map");
              if (localSourcesFoldersList.getModel().getSize() == 0) {
                localSourcesFoldersList.addItem(SettingsExcludeType.FOLDER, path.getAbsolutePath());
              } else {
                boolean exists = false;
                for (int i = 0; i < localSourcesFoldersList.getModel().getSize(); i++) {
                  if (localSourcesFoldersList.getDescription(i).equals(path.getAbsolutePath())) {
                    exists = true;
                  }
                }
                if (!exists) {
                  localSourcesFoldersList.addItem(SettingsExcludeType.FOLDER,
                      path.getAbsolutePath());
                }
              }
            }
          });
          pnlLocalSourcesSettings.add(btnBrowseLocalSources, "cell 1 1,alignx left,aligny top");
        }
        {
          JButton btnRemoveLocalSources = new JButton("map verwijderen");
          btnRemoveLocalSources.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              DefaultListModel<JPanel> model =
                  (DefaultListModel<JPanel>) localSourcesFoldersList.getModel();
              int selected = localSourcesFoldersList.getSelectedIndex();
              if (model.size() > 0 && selected >= 0) {
                model.removeElementAt(selected);
              }
            }
          });
          pnlLocalSourcesSettings.add(btnRemoveLocalSources, "cell 2 1");
        }
        {
          JScrollPane scrlPlocalSources = new JScrollPane();
          pnlLocalSourcesSettings.add(scrlPlocalSources, "cell 1 2 2 1,grow");
          {
            localSourcesFoldersList = new JListWithImages();
            scrlPlocalSources.setViewportView(localSourcesFoldersList);
          }
        }
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton(" OK ");
        okButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            testAndSaveValues();
          }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton("Annuleren");
        cancelButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            setVisible(false);
          }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }
  }

  protected void moveRuleRowDown() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    if (table.getSelectedRow() >= 0 && table.getSelectedRow() < model.getRowCount()) {
      Object oSelected = model.getValueAt(table.getSelectedRow(), 1);
      Object oDown = model.getValueAt(table.getSelectedRow() + 1, 1);
      model.setValueAt(oSelected, table.getSelectedRow() + 1, 1);
      model.setValueAt(oDown, table.getSelectedRow(), 1);
    }
  }

  protected void moveRuleRowUp() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    if (table.getSelectedRow() >= 0) {
      Object oSelected = model.getValueAt(table.getSelectedRow(), 1);
      Object oUp = model.getValueAt(table.getSelectedRow() - 1, 1);
      model.setValueAt(oSelected, table.getSelectedRow() - 1, 1);
      model.setValueAt(oUp, table.getSelectedRow(), 1);
    }
  }

  private void setPreferenceSettings() {
    for (int i = 0; i < settingsCtrl.getSettings().getExcludeList().size(); i++) {
      excludeList.addItem(settingsCtrl.getSettings().getExcludeList().get(i).getType(),
          settingsCtrl.getSettings().getExcludeList().get(i).getDescription());
    }
    for (int i = 0; i < settingsCtrl.getSettings().getDefaultIncomingFolders().size(); i++) {
      defaultIncomingFoldersList.addItem(SettingsExcludeType.FOLDER, settingsCtrl.getSettings()
          .getDefaultIncomingFolders().get(i).getAbsolutePath());
    }
    for (int i = 0; i < settingsCtrl.getSettings().getLocalSourcesFolders().size(); i++) {
      localSourcesFoldersList.addItem(SettingsExcludeType.FOLDER, settingsCtrl.getSettings()
          .getLocalSourcesFolders().get(i).getAbsolutePath());
    }
    chkProxyserverGebruiken.setSelected(settingsCtrl.getSettings().isGeneralProxyEnabled());
    txtProxyHost.setText(settingsCtrl.getSettings().getGeneralProxyHost());
    txtProxyPort.setText(String.valueOf(settingsCtrl.getSettings().getGeneralProxyPort()));
    chkAlwaysConfirm.setSelected(settingsCtrl.getSettings().isOptionsAlwaysConfirm());
    chkSubtitleExactMethod.setSelected(settingsCtrl.getSettings().isOptionSubtitleExactMatch());
    chkSubtitleKeywordMethod.setSelected(settingsCtrl.getSettings().isOptionSubtitleKeywordMatch());
    chkExcludeHearingImpaired.setSelected(settingsCtrl.getSettings()
        .isOptionSubtitleExcludeHearingImpaired());
    chkOnlyFound.setSelected(settingsCtrl.getSettings().isOptionsShowOnlyFound());
    chkStopOnSearchError.setSelected(settingsCtrl.getSettings().isOptionsStopOnSearchError());
    cbxEpisodeProcessSource.setSelectedItem(settingsCtrl.getSettings().getProcessEpisodeSource());
    pnlEpisodeLibrary.setLibrarySettings(settingsCtrl.getSettings().getEpisodeLibrarySettings());
    pnlMovieLibrary.setLibrarySettings(settingsCtrl.getSettings().getMovieLibrarySettings());
    chkAutomaticDownloadSelection.setSelected(settingsCtrl.getSettings()
        .isOptionsAutomaticDownloadSelection());
    chkAutomaticSelectionQuality.setSelected(settingsCtrl.getSettings()
        .isOptionsAutomaticDownloadSelectionQuality());
    chkAutomaticSelectionTeam.setSelected(settingsCtrl.getSettings()
        .isOptionsAutomaticDownloadSelectionReleaseGroup());
    chkNoRuleMatchTakeFirst.setSelected(settingsCtrl.getSettings().isOptionsNoRuleMatchTakeFirst());
    for (String q : settingsCtrl.getSettings().getQualityRuleList()) {
      addRuleRow(q);
    }
    chkUserAddic7edLogin.setSelected(settingsCtrl.getSettings().isLoginAddic7edEnabled());
    txtAddic7edUsername.setText(settingsCtrl.getSettings().getLoginAddic7edUsername());
    txtAddic7edPassword.setText(settingsCtrl.getSettings().getLoginAddic7edPassword());
    chkSerieSourceAddic7ed.setSelected(settingsCtrl.getSettings().isSerieSourceAddic7ed());
    chkSerieSourceTvSubtitles.setSelected(settingsCtrl.getSettings().isSerieSourceTvSubtitles());
    chkSerieSourcePodnapisi.setSelected(settingsCtrl.getSettings().isSerieSourcePodnapisi());
    chkSerieSourceOpensubtitles
        .setSelected(settingsCtrl.getSettings().isSerieSourceOpensubtitles());
    chkSerieSourceLocal.setSelected(settingsCtrl.getSettings().isSerieSourceLocal());
    chkSerieSourcePrivateRepo.setSelected(settingsCtrl.getSettings().isSerieSourcePrivateRepo());
    chkSerieSourceSubsMax.setSelected(settingsCtrl.getSettings().isSerieSourceSubsMax());

    for (SearchSubtitlePriority prio : settingsCtrl.getSettings().getListSearchSubtitlePriority()) {
      switch (prio.getSubtitleSource()) {
        case ADDIC7ED:
          cbxPriorityAddic7ed.setSelectedItem(prio.getPriority());
          break;
        case LOCAL:
          cbxPriorityLocal.setSelectedItem(prio.getPriority());
          break;
        case OPENSUBTITLES:
          cbxPriorityOpensubtitles.setSelectedItem(prio.getPriority());
          break;
        case PODNAPISI:
          cbxPriorityPodnapisi.setSelectedItem(prio.getPriority());
          break;
        case PRIVATEREPO:
          cbxPriorityPrivateRepo.setSelectedItem(prio.getPriority());
          break;
        case TVSUBTITLES:
          cbxPriorityTvSubtitles.setSelectedItem(prio.getPriority());
          break;
        case SUBSMAX:
          cbxPrioritySubsMax.setSelectedItem(prio.getPriority());
          break;
        default:
          break;
      }

    }

  }

  protected boolean testOptionsTab() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    for (int i = 0; i < model.getRowCount(); i++) {
      if (model.getValueAt(i, 1).equals("") || model.getValueAt(i, 1).equals("Selecteer...")) {
        String message = "Opties: op regel " + i + 1 + " is er geen waarde geselecteerd";
        JOptionPane.showConfirmDialog(this, message, "MultiSubDownloader",
            JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
        Logger.instance.debug("testOptionsTab: " + "Opties: op regel " + i + 1
            + " is er geen waarde geselecteerd");
        return false;
      }
    }
    return true;
  }

  protected boolean testGeneralTab() {
    try {
      Integer.parseInt(txtProxyPort.getText());
    } catch (Exception e) {
      String message = "De proxy poort moet een numerische waarde zijn!";
      JOptionPane.showConfirmDialog(this, message, "MultiSubDownloader", JOptionPane.CLOSED_OPTION,
          JOptionPane.ERROR_MESSAGE);
      Logger.instance.debug("testGeneralTab: De proxy poort moet een numerische waarde zijn!");
      return false;
    }
    return true;
  }

  protected boolean testSerieSourcesTab() {
    if (chkUserAddic7edLogin.isSelected()) {
      if (txtAddic7edUsername.getText().isEmpty() | txtAddic7edPassword.getText().isEmpty()) {
        String message =
            "Addic7ed login geselecteerd! Gelieve een username en pasword in te vullen.";
        JOptionPane.showConfirmDialog(this, message, "MultiSubDownloader",
            JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
        Logger.instance
            .debug("testSerieSourcesTab: Addic7ed login geselecteerd! Gelieve een username en pasword in te vullen.");
        return false;
      }
    }
    return true;
  }

  private void addExcludeItem(SettingsExcludeType seType) {
    if (seType == SettingsExcludeType.FOLDER) {
      File path =
          MemoryFolderChooser.getInstance().selectDirectory(getContentPane(),
              "Selecteer uitsluit map");
      excludeList.addItem(seType, path.getAbsolutePath());
    } else if (seType == SettingsExcludeType.REGEX) {
      String regex = JOptionPane.showInputDialog("Geef een REGEX op: (VB: S0*)");
      excludeList.addItem(seType, regex);
    }
  }

  private void removeExcludeItem() {
    DefaultListModel<JPanel> model = (DefaultListModel<JPanel>) excludeList.getModel();
    int selected = excludeList.getSelectedIndex();
    if (model.size() > 0 && selected >= 0) {
      model.removeElementAt(selected);
    }
  }

  private void testAndSaveValues() {
    boolean status = true;
    if (testGeneralTab()) {
      ArrayList<File> folList = new ArrayList<File>();
      for (int i = 0; i < defaultIncomingFoldersList.getModel().getSize(); i++) {
        folList.add(new File(defaultIncomingFoldersList.getDescription(i)));
      }
      settingsCtrl.getSettings().setDefaultIncomingFolders(folList);

      ArrayList<SettingsExcludeItem> list = new ArrayList<SettingsExcludeItem>();
      for (int i = 0; i < excludeList.getModel().getSize(); i++) {
        SettingsExcludeItem sei =
            new SettingsExcludeItem(excludeList.getDescription(i), excludeList.getType(i));
        list.add(sei);
      }
      settingsCtrl.getSettings().setExcludeList(list);
      settingsCtrl.getSettings().setGeneralProxyEnabled(chkProxyserverGebruiken.isSelected());
      settingsCtrl.getSettings().setGeneralProxyHost(txtProxyHost.getText());
      settingsCtrl.getSettings().setGeneralProxyPort(Integer.parseInt(txtProxyPort.getText()));
    } else {
      status = false;
    }
    if (pnlEpisodeLibrary.isValidPanelValues()) {
      LibrarySettings libs = pnlEpisodeLibrary.getLibrarySettings();
      settingsCtrl.getSettings().setEpisodeLibrarySettings(libs);
    } else {
      status = false;
    }
    if (pnlMovieLibrary.isValidPanelValues()) {
      LibrarySettings libs = pnlMovieLibrary.getLibrarySettings();
      settingsCtrl.getSettings().setMovieLibrarySettings(libs);
    } else {
      status = false;
    }
    if (testOptionsTab()) {
      settingsCtrl.getSettings().setOptionsAlwaysConfirm(chkAlwaysConfirm.isSelected());
      settingsCtrl.getSettings().setOptionSubtitleExactMatch(chkSubtitleExactMethod.isSelected());
      settingsCtrl.getSettings().setOptionSubtitleKeywordMatch(
          chkSubtitleKeywordMethod.isSelected());
      settingsCtrl.getSettings().setOptionSubtitleExcludeHearingImpaired(
          chkExcludeHearingImpaired.isSelected());
      settingsCtrl.getSettings().setOptionsShowOnlyFound(chkOnlyFound.isSelected());
      settingsCtrl.getSettings().setOptionsStopOnSearchError(chkStopOnSearchError.isSelected());
      settingsCtrl.getSettings().setProcessEpisodeSource(
          (SettingsProcessEpisodeSource) cbxEpisodeProcessSource.getSelectedItem());
      settingsCtrl.getSettings().setOptionsAutomaticDownloadSelection(
          chkAutomaticDownloadSelection.isSelected());
      settingsCtrl.getSettings().setOptionsAutomaticDownloadSelectionQuality(
          chkAutomaticSelectionQuality.isSelected());
      settingsCtrl.getSettings().setOptionsAutomaticDownloadSelectionReleaseGroup(
          chkAutomaticSelectionTeam.isSelected());
      settingsCtrl.getSettings().setOptionsNoRuleMatchMatchTakeFirst(
          chkNoRuleMatchTakeFirst.isSelected());
      DefaultTableModel model = (DefaultTableModel) table.getModel();
      ArrayList<String> list = new ArrayList<String>();
      for (int i = 0; i < model.getRowCount(); i++) {
        list.add((String) model.getValueAt(i, 1));
      }
      settingsCtrl.getSettings().setQualityRuleList(list);
    } else {
      status = false;
    }
    if (testSerieSourcesTab()) {
      settingsCtrl.getSettings().setLoginAddic7edEnabled(chkUserAddic7edLogin.isSelected());
      settingsCtrl.getSettings().setLoginAddic7edUsername(txtAddic7edUsername.getText());
      settingsCtrl.getSettings().setLoginAddic7edPassword(txtAddic7edPassword.getText());
      ArrayList<File> folList = new ArrayList<File>();
      for (int i = 0; i < localSourcesFoldersList.getModel().getSize(); i++) {
        folList.add(new File(localSourcesFoldersList.getDescription(i)));
      }
      settingsCtrl.getSettings().setLocalSourcesFolders(folList);
      settingsCtrl.getSettings().setSerieSourceAddic7ed(chkSerieSourceAddic7ed.isSelected());
      settingsCtrl.getSettings().setSerieSourceTvSubtitles(chkSerieSourceTvSubtitles.isSelected());
      settingsCtrl.getSettings().setSerieSourcePodnapisi(chkSerieSourcePodnapisi.isSelected());
      settingsCtrl.getSettings().setSerieSourceOpensubtitles(
          chkSerieSourceOpensubtitles.isSelected());
      settingsCtrl.getSettings().setSerieSourceLocal(chkSerieSourceLocal.isSelected());
      settingsCtrl.getSettings().setSerieSourcePrivateRepo(chkSerieSourcePrivateRepo.isSelected());
      settingsCtrl.getSettings().setSerieSourceSubsMax(chkSerieSourceSubsMax.isSelected());

      // Save priority
      SearchSubtitlePriority prioAddic7ed =
          new SearchSubtitlePriority(SubtitleSource.ADDIC7ED,
              (Integer) cbxPriorityAddic7ed.getSelectedItem());
      SearchSubtitlePriority prioLocal =
          new SearchSubtitlePriority(SubtitleSource.LOCAL,
              (Integer) cbxPriorityLocal.getSelectedItem());
      SearchSubtitlePriority prioOpensubtitles =
          new SearchSubtitlePriority(SubtitleSource.OPENSUBTITLES,
              (Integer) cbxPriorityOpensubtitles.getSelectedItem());
      SearchSubtitlePriority prioPodnapisi =
          new SearchSubtitlePriority(SubtitleSource.PODNAPISI,
              (Integer) cbxPriorityPodnapisi.getSelectedItem());
      SearchSubtitlePriority prioPrivateRepo =
          new SearchSubtitlePriority(SubtitleSource.PRIVATEREPO,
              (Integer) cbxPriorityPrivateRepo.getSelectedItem());
      SearchSubtitlePriority prioTvSubtitles =
          new SearchSubtitlePriority(SubtitleSource.TVSUBTITLES,
              (Integer) cbxPriorityTvSubtitles.getSelectedItem());
      SearchSubtitlePriority prioSubsMax =
          new SearchSubtitlePriority(SubtitleSource.SUBSMAX,
              (Integer) cbxPrioritySubsMax.getSelectedItem());
      List<SearchSubtitlePriority> lPrio = new ArrayList<SearchSubtitlePriority>();

      lPrio.add(prioAddic7ed);
      lPrio.add(prioLocal);
      lPrio.add(prioOpensubtitles);
      lPrio.add(prioPodnapisi);
      lPrio.add(prioPrivateRepo);
      lPrio.add(prioTvSubtitles);
      lPrio.add(prioSubsMax);

      java.util.Collections.sort(lPrio, new Comparator<SearchSubtitlePriority>() {
        public int compare(SearchSubtitlePriority t1, SearchSubtitlePriority t2) {
          return t1.getPriority() - t2.getPriority();
        }
      });

      settingsCtrl.getSettings().setListSearchSubtitlePriority(lPrio);
    } else {
      status = false;
    }

    if (status) {
      setVisible(false);
      settingsCtrl.store();
    }
  }
}
