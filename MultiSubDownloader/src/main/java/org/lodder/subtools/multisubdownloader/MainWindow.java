package org.lodder.subtools.multisubdownloader;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.event.Emitter;
import org.lodder.subtools.multisubdownloader.gui.Menu;
import org.lodder.subtools.multisubdownloader.gui.actions.search.FileSearchAction;
import org.lodder.subtools.multisubdownloader.gui.actions.search.TextSearchAction;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.dialog.MappingEpisodeNameDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.PreferenceDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.ProgressDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.RenameDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.MyPopupMenu;
import org.lodder.subtools.multisubdownloader.gui.extra.PopupListener;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusLabel;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SearchColumnName;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.LoggingPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.ResultPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchFileInputPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchTextInputPanel;
import org.lodder.subtools.multisubdownloader.gui.workers.DownloadWorker;
import org.lodder.subtools.multisubdownloader.gui.workers.RenameWorker;
import org.lodder.subtools.multisubdownloader.lib.Actions;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.SubtitleSelectionGUI;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.Filtering;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.multisubdownloader.util.Export;
import org.lodder.subtools.multisubdownloader.util.Import;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.OsCheck;
import org.lodder.subtools.sublibrary.OsCheck.OSType;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.StringUtils;
import org.lodder.subtools.sublibrary.util.XmlFileFilter;
import org.lodder.subtools.sublibrary.util.http.HttpClient;

public class MainWindow extends JFrame implements PropertyChangeListener {

  /**
     *
     */
  private static final long serialVersionUID = 1L;
  private final Container app;
  private StatusLabel lblStatus;
  private final SettingsControl settingsControl;
  private ProgressDialog progressDialog;
  private MyPopupMenu popupMenu;
  private SearchPanel pnlSearchFile;
  private SearchPanel pnlSearchText;
  private JPanel pnlLogging;
  private SearchTextInputPanel pnlSearchTextInput;
  private SearchFileInputPanel pnlSearchFileInput;
  private Menu menuBar;

  /**
   * Create the application.
   */
  public MainWindow(final SettingsControl settingsControl, Container app) {
    this.app = app;
    setTitle(ConfigProperties.getInstance().getProperty("name"));
    /*
     * setIconImage(Toolkit.getDefaultToolkit().getImage(
     * getClass().getResource("/resources/Bierdopje_bigger.png")));
     */
    this.settingsControl = settingsControl;
    initialize();
    restoreScreenSettings();
    pnlSearchFile.getResultPanel().disableButtons();
    pnlSearchText.getResultPanel().disableButtons();
    checkUpdate(false);
    initPopupMenu();

    try {
      if (this.settingsControl.getSettings().isAutoUpdateMapping()) {
        this.settingsControl.updateMappingFromOnline();
      }
    } catch (Throwable e) {
      Logger.instance.error(Logger.stack2String(e));
    }
  }

  private void checkUpdate(final boolean showNoUpdate) {
    UpdateAvailableDropbox u = new UpdateAvailableDropbox();
    if (u.checkProgram()) {
      final JEditorPane editorPane = new JEditorPane();
      editorPane.setPreferredSize(new Dimension(800, 50));
      editorPane.setEditable(false);
      editorPane.setContentType("text/html");

      editorPane.setText(
        "<html>" + Messages.getString("UpdateAppAvailable") + "!: </br><A HREF=" + u.getUpdateUrl() + ">"
          + u.getUpdateUrl() + "</a></html>");

      editorPane.addHyperlinkListener(new HyperlinkListener() {
        @Override
        public void hyperlinkUpdate(final HyperlinkEvent hyperlinkEvent) {
          if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED
              && Desktop.isDesktopSupported()) {
            try {
              Desktop.getDesktop().browse(hyperlinkEvent.getURL().toURI());
            } catch (Exception e) {
              Logger.instance.error(Logger.stack2String(e));
            }
          }
        }
      });
      JOptionPane.showMessageDialog(this, editorPane,
          ConfigProperties.getInstance().getProperty("name"), JOptionPane.INFORMATION_MESSAGE);
    } else if (showNoUpdate) {
      JOptionPane.showMessageDialog(this, Messages.getString("MainWindow.NoUpdateAvailable")
          + ConfigProperties.getInstance().getProperty("version")
          + ConfigProperties.getInstance().getProperty(Messages.getString("MainWindow.Version")),
          ConfigProperties.getInstance().getProperty("name"), JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    MemoryFolderChooser.getInstance().setMemory(settingsControl.getSettings().getLastOutputDir());
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        close();
      }
    });
    setBounds(100, 100, 925, 680);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    final GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {448, 0};
    gridBagLayout.rowHeights = new int[] {0, 125, 15, 0};
    gridBagLayout.columnWeights = new double[] {1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[] {1.0, 1.0, 0.0, Double.MIN_VALUE};
    getContentPane().setLayout(gridBagLayout);
    final GridBagConstraints gbc_panel_buttons = new GridBagConstraints();
    gbc_panel_buttons.insets = new Insets(0, 0, 5, 0);
    gbc_panel_buttons.fill = GridBagConstraints.BOTH;
    gbc_panel_buttons.gridx = 0;
    gbc_panel_buttons.gridy = 1;

    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
    gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
    gbc_tabbedPane.fill = GridBagConstraints.BOTH;
    gbc_tabbedPane.gridx = 0;
    gbc_tabbedPane.gridy = 0;
    getContentPane().add(tabbedPane, gbc_tabbedPane);

    createFileSearchPanel();
    tabbedPane.addTab(Messages.getString("MainWindow.SearchOnFile"), null, pnlSearchFile, null);

    createTextSearchPanel();
    tabbedPane.addTab(Messages.getString("MainWindow.SearchOnName"), null, pnlSearchText, null);

    pnlLogging = new LoggingPanel();
    final GridBagConstraints gbc_pnlLogging = new GridBagConstraints();
    gbc_pnlLogging.fill = GridBagConstraints.BOTH;
    gbc_pnlLogging.insets = new Insets(0, 0, 5, 0);
    gbc_pnlLogging.gridx = 0;
    gbc_pnlLogging.gridy = 1;
    getContentPane().add(pnlLogging, gbc_pnlLogging);

    lblStatus = new StatusLabel("");
    StatusMessenger.instance.addListener(lblStatus);
    final GridBagConstraints gbc_lblStatus = new GridBagConstraints();
    gbc_lblStatus.anchor = GridBagConstraints.SOUTHWEST;
    gbc_lblStatus.gridx = 0;
    gbc_lblStatus.gridy = 2;
    getContentPane().add(lblStatus, gbc_lblStatus);

    createMenu();
    setJMenuBar(menuBar);
  }

  private void createMenu() {
    menuBar = new Menu();

    menuBar.setShowOnlyFound(settingsControl.getSettings().isOptionsShowOnlyFound());

    menuBar.setFileQuitAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        close();
      }
    });

    menuBar.setViewFilenameAction(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        VideoTable videoTable = pnlSearchFile.getResultPanel().getTable();
        if (menuBar.isViewFilenameSelected()) {
          videoTable.unhideColumn(SearchColumnName.FILENAME);
        } else {
          videoTable.hideColumn(SearchColumnName.FILENAME);
        }
      }
    });

    menuBar.setViewTypeAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        VideoTable videoTable = pnlSearchFile.getResultPanel().getTable();
        if (menuBar.isViewTitleSelected()) {
          videoTable.unhideColumn(SearchColumnName.TYPE);
        } else {
          videoTable.hideColumn(SearchColumnName.TYPE);
        }
      }
    });

    menuBar.setViewTitleAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        VideoTable videoTable = pnlSearchFile.getResultPanel().getTable();
        if (menuBar.isViewTitleSelected()) {
          videoTable.unhideColumn(SearchColumnName.TITLE);
        } else {
          videoTable.hideColumn(SearchColumnName.TITLE);
        }
      }
    });

    menuBar.setViewSeasonAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        VideoTable videoTable = pnlSearchFile.getResultPanel().getTable();
        if (menuBar.isViewSeasonSelected()) {
          videoTable.unhideColumn(SearchColumnName.SEASON);
        } else {
          videoTable.hideColumn(SearchColumnName.SEASON);
        }
      }
    });

    menuBar.setViewEpisodeAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        VideoTable videoTable = pnlSearchFile.getResultPanel().getTable();
        if (menuBar.isViewEpisodeSelected()) {
          videoTable.unhideColumn(SearchColumnName.EPISODE);
        } else {
          videoTable.hideColumn(SearchColumnName.EPISODE);
        }
      }
    });

    menuBar.setViewShowOnlyFoundAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        VideoTable videoTable = pnlSearchFile.getResultPanel().getTable();
        settingsControl.getSettings().setOptionsShowOnlyFound(menuBar.isShowOnlyFound());
        ((VideoTableModel) videoTable.getModel()).setShowOnlyFound(settingsControl.getSettings()
            .isOptionsShowOnlyFound());
      }
    });

    menuBar.setViewClearLogAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        ((LoggingPanel) pnlLogging).setLogText("");
      }
    });

    menuBar.setEditRenameTVAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        final RenameDialog rDialog =
            new RenameDialog(getThis(), settingsControl.getSettings(), VideoType.EPISODE);
        rDialog.setVisible(true);
      }
    });

    menuBar.setEditRenameMovieAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        final RenameDialog rDialog =
            new RenameDialog(getThis(), settingsControl.getSettings(), VideoType.MOVIE);
        rDialog.setVisible(true);
      }
    });

    menuBar.setEditPreferencesAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        final PreferenceDialog pDialog =
          new PreferenceDialog(getThis(), settingsControl, (Emitter) app.make("EventEmitter"));
        pDialog.setVisible(true);
      }
    });

    menuBar.setTranslateShowNamesAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        showTranslateShowNames();
      }
    });

    menuBar.setExportExclusionsAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        exportList(Export.ExportListType.EXCLUDE);
      }
    });

    menuBar.setImportExclusionsAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        importList(Import.ImportListType.EXCLUDE);
      }
    });

    menuBar.setExportPreferencesAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        exportList(Export.ExportListType.PREFERENCES);
      }
    });

    menuBar.setImportPreferencesAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        importList(Import.ImportListType.PREFERENCES);
      }
    });

    menuBar.setImportTranslationsAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        importList(Import.ImportListType.TRANSLATE);
      }
    });

    menuBar.setExportTranslationsAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        exportList(Export.ExportListType.TRANSLATE);
      }
    });

    menuBar.setCheckUpdateAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        checkUpdate(true);
      }
    });

    menuBar.setAboutAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        showAbout();
      }
    });
  }

  private void createTextSearchPanel() {
    Settings settings = this.settingsControl.getSettings();

    /* resolve the SubtitleProviderStore from the Container */
    SubtitleProviderStore subtitleProviderStore =
        (SubtitleProviderStore) this.app.make("SubtitleProviderStore");

    TextSearchAction searchAction = new TextSearchAction(this, settings, subtitleProviderStore);
    ResultPanel resultPanel = new ResultPanel();
    pnlSearchTextInput = new SearchTextInputPanel();

    pnlSearchText = new SearchPanel();
    pnlSearchText.setResultPanel(resultPanel);
    pnlSearchText.setInputPanel(pnlSearchTextInput);

    resultPanel.showSelectFoundSubtitlesButton();
    resultPanel.setTable(createSubtitleTable());

    searchAction.setSearchPanel(pnlSearchText);
    searchAction.setReleaseFactory(new ReleaseFactory(settings));
    searchAction.setFiltering(new Filtering(settings));

    pnlSearchTextInput.setSearchAction(searchAction);
    resultPanel.setDownloadAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        downloadText();
      }
    });
  }

  private VideoTable createSubtitleTable() {
    VideoTable subtitleTable = new VideoTable();
    subtitleTable.setModel(VideoTableModel.getDefaultSubtitleTableModel());
    final RowSorter<TableModel> sorterSubtitle =
        new TableRowSorter<TableModel>(subtitleTable.getModel());
    subtitleTable.setRowSorter(sorterSubtitle);
    subtitleTable.hideColumn(SearchColumnName.OBJECT);
    return subtitleTable;
  }

  private void createFileSearchPanel() {
    Settings settings = this.settingsControl.getSettings();

    /* resolve the SubtitleProviderStore from the Container */
    SubtitleProviderStore subtitleProviderStore =
        (SubtitleProviderStore) this.app.make("SubtitleProviderStore");

    FileSearchAction searchAction = new FileSearchAction(this, settings, subtitleProviderStore);
    ResultPanel resultPanel = new ResultPanel();
    pnlSearchFileInput = new SearchFileInputPanel();
    pnlSearchFile = new SearchPanel();

    pnlSearchFile.setResultPanel(resultPanel);
    pnlSearchFile.setInputPanel(pnlSearchFileInput);

    resultPanel.setTable(createVideoTable());

    searchAction.setActions(new Actions(this.settingsControl.getSettings(), false));
    searchAction.setReleaseFactory(new ReleaseFactory(this.settingsControl.getSettings()));
    searchAction.setFiltering(new Filtering(this.settingsControl.getSettings()));
    searchAction.setSearchPanel(pnlSearchFile);

    pnlSearchFileInput.setSelectFolderAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        selectIncomingFolder();
      }
    });
    pnlSearchFileInput.setSearchAction(searchAction);

    resultPanel.setDownloadAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        download();
      }
    });
    resultPanel.setMoveAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        final int response =
            JOptionPane.showConfirmDialog(
                getThis(),
                Messages.getString("MainWindow.OnlyMoveToLibraryStructure"), Messages.getString("MainWindow.Confirm"), //$NON-NLS-2$
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
          rename();
        }
      }
    });
  }

  private VideoTable createVideoTable() {
    VideoTable videoTable = new VideoTable();
    videoTable.setModel(VideoTableModel.getDefaultVideoTableModel());
    ((VideoTableModel) videoTable.getModel()).setShowOnlyFound(settingsControl.getSettings()
        .isOptionsShowOnlyFound());
    ((VideoTableModel) videoTable.getModel()).setSubtitleSelection(new SubtitleSelectionGUI(settingsControl.getSettings()));
    final RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(videoTable.getModel());
    videoTable.setRowSorter(sorter);
    videoTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    int columnId = videoTable.getColumnIdByName(SearchColumnName.FOUND);
    videoTable.getColumnModel().getColumn(columnId).setResizable(false);
    videoTable.getColumnModel().getColumn(columnId).setPreferredWidth(100);
    videoTable.getColumnModel().getColumn(columnId).setMaxWidth(100);
    columnId = videoTable.getColumnIdByName(SearchColumnName.SELECT);
    videoTable.getColumnModel().getColumn(columnId).setResizable(false);
    videoTable.getColumnModel().getColumn(columnId).setPreferredWidth(85);
    videoTable.getColumnModel().getColumn(columnId).setMaxWidth(85);
    videoTable.hideColumn(SearchColumnName.OBJECT);
    videoTable.hideColumn(SearchColumnName.SEASON);
    videoTable.hideColumn(SearchColumnName.EPISODE);
    videoTable.hideColumn(SearchColumnName.TYPE);
    videoTable.hideColumn(SearchColumnName.TITLE);
    return videoTable;
  }

  private void restoreScreenSettings() {
    VideoTable videoTable = pnlSearchFile.getResultPanel().getTable();
    if (settingsControl.getSettings().getScreenSettings().isHideEpisode()) {
      videoTable.hideColumn(SearchColumnName.EPISODE);
    } else {
      menuBar.setViewEpisodeSelected(true);
      videoTable.unhideColumn(SearchColumnName.EPISODE);
    }
    if (settingsControl.getSettings().getScreenSettings().isHideFilename()) {
      videoTable.hideColumn(SearchColumnName.FILENAME);
    } else {
      menuBar.setViewFileNameSelected(true);
      videoTable.unhideColumn(SearchColumnName.FILENAME);
    }
    if (settingsControl.getSettings().getScreenSettings().isHideSeason()) {
      videoTable.hideColumn(SearchColumnName.SEASON);
    } else {
      menuBar.setViewSeasonSelected(true);
      videoTable.unhideColumn(SearchColumnName.SEASON);
    }
    if (settingsControl.getSettings().getScreenSettings().isHideType()) {
      videoTable.hideColumn(SearchColumnName.TYPE);
    } else {
      menuBar.setViewTitleSelected(true);
      videoTable.unhideColumn(SearchColumnName.TYPE);
    }
    if (settingsControl.getSettings().getScreenSettings().isHideTitle()) {
      videoTable.hideColumn(SearchColumnName.TITLE);
    } else {
      menuBar.setViewTitleSelected(true);
      videoTable.unhideColumn(SearchColumnName.TITLE);
    }
  }

  private void initPopupMenu() {
    popupMenu = new MyPopupMenu();
    JMenuItem menuItem = new JMenuItem(Messages.getString("MainWindow.Copy"));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        final VideoTable t = (VideoTable) popupMenu.getInvoker();
        final DefaultTableModel model = (DefaultTableModel) t.getModel();

        int col = t.columnAtPoint(popupMenu.getClickLocation());
        int row = t.rowAtPoint(popupMenu.getClickLocation());

        try {
          StringSelection selection = new StringSelection((String) model.getValueAt(row, col));
          Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        } catch (HeadlessException e) {
          Logger.instance.error("initPopupMenu : " + Logger.stack2String(e));
        }
      }
    });
    popupMenu.add(menuItem);
    // add the listener to the jtable
    MouseListener popupListener = new PopupListener(popupMenu);
    // add the listener specifically to the header
    VideoTable videoTable = pnlSearchFile.getResultPanel().getTable();
    VideoTable subtitleTable = pnlSearchText.getResultPanel().getTable();
    videoTable.addMouseListener(popupListener);
    videoTable.getTableHeader().addMouseListener(popupListener);
    subtitleTable.addMouseListener(popupListener);
    subtitleTable.getTableHeader().addMouseListener(popupListener);
  }

  protected void showTranslateShowNames() {
    final MappingEpisodeNameDialog tDialog = new MappingEpisodeNameDialog(this, settingsControl);
    tDialog.setVisible(true);
  }

  private void showAbout() {
    JOptionPane.showConfirmDialog(this, Messages.getString("MainWindow.AboutTitle"),
        ConfigProperties.getInstance().getProperty("name"), JOptionPane.CLOSED_OPTION);
  }

  protected void rename() {
    VideoTable videoTable = pnlSearchFile.getResultPanel().getTable();
    RenameWorker renameWorker = new RenameWorker(videoTable, settingsControl.getSettings());
    renameWorker.addPropertyChangeListener(this);
    pnlSearchFile.getResultPanel().enableButtons();
    progressDialog = new ProgressDialog(this, renameWorker);
    progressDialog.setVisible(true);
    renameWorker.execute();
  }

  private void download() {
    VideoTable videoTable = pnlSearchFile.getResultPanel().getTable();
    Logger.instance.trace(MainWindow.class.toString(), "download", "");
    DownloadWorker downloadWorker = new DownloadWorker(videoTable, settingsControl.getSettings());
    downloadWorker.addPropertyChangeListener(this);
    pnlSearchFile.getResultPanel().disableButtons();
    progressDialog = new ProgressDialog(this, downloadWorker);
    progressDialog.setVisible(true);
    downloadWorker.execute();
  }

  private void downloadText() {
    VideoTable subtitleTable = pnlSearchText.getResultPanel().getTable();
    final VideoTableModel model = (VideoTableModel) subtitleTable.getModel();
    File path =
        MemoryFolderChooser.getInstance().selectDirectory(getContentPane(),
            Messages.getString("MainWindow.SelectFolder"));

    for (int i = 0; i < model.getRowCount(); i++) {
      if ((Boolean) model.getValueAt(i, subtitleTable.getColumnIdByName(SearchColumnName.SELECT))) {
        final Subtitle subtitle =
            (Subtitle) model
                .getValueAt(i, subtitleTable.getColumnIdByName(SearchColumnName.OBJECT));
        String filename = "";
        if (!subtitle.getFilename().endsWith(".srt")) filename = subtitle.getFilename() + ".srt";
        if (OsCheck.getOperatingSystemType() == OSType.Windows)
          filename = StringUtils.removeIllegalWindowsChars(filename);

        try {

          if (HttpClient.isUrl(subtitle.getDownloadlink())) {
            HttpClient.getHttpClient().doDownloadFile(new URL(subtitle.getDownloadlink()),
                new File(path, filename));
          } else {
            Files
                .copy(new File(subtitle.getDownloadlink()), new File(path, subtitle.getFilename()));
          }

        } catch (MalformedURLException e) {
          Logger.instance.error("downloadText : " + Logger.stack2String(e));
        } catch (IOException e) {
          Logger.instance.error("downloadText : " + Logger.stack2String(e));
        }
      }
    }
  }

  protected JFrame getThis() {
    return this;
  }

  public void showErrorMessage(String message) {
    JOptionPane.showConfirmDialog(this, message,
        ConfigProperties.getInstance().getProperty("name"), JOptionPane.CLOSED_OPTION,
        JOptionPane.ERROR_MESSAGE);
  }

  private void importList(Import.ImportListType listType) {
    // Create a file chooser
    final JFileChooser fc = new JFileChooser();
    XmlFileFilter filter = new XmlFileFilter();
    fc.setAcceptAllFileFilterUsed(false);
    fc.setFileFilter(filter);
    final int returnVal = fc.showOpenDialog(getThis());
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      Import i = new Import(getThis(), settingsControl);
      i.doImport(listType, fc.getSelectedFile());
    }
  }

  private void exportList(Export.ExportListType listType) {
    // Create a file chooser
    final JFileChooser fc = new JFileChooser();
    XmlFileFilter filter = new XmlFileFilter();
    fc.setAcceptAllFileFilterUsed(false);
    fc.setFileFilter(filter);
    final int returnVal = fc.showSaveDialog(getThis());
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      Export e = new Export(settingsControl);
      File f = fc.getSelectedFile();
      if (!XmlFileFilter.getExtension(f).equalsIgnoreCase("xml")) {
        f = new File(f.toString() + ".xml");
      }
      e.doExport(listType, f);
    }

  }

  private void selectIncomingFolder() {
    File path =
        MemoryFolderChooser.getInstance().selectDirectory(getThis(),
            Messages.getString("MainWindow.SelectFolder"));
    pnlSearchFileInput.setIncomingPath(path.getAbsolutePath());
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if (event.getSource() instanceof DownloadWorker) {
      final DownloadWorker downloadWorker = (DownloadWorker) event.getSource();
      if (downloadWorker.isDone()) {
        pnlSearchFile.getResultPanel().enableButtons();
        progressDialog.setVisible(false);
      } else {
        final int progress = downloadWorker.getProgress();
        progressDialog.updateProgress(progress);
        StatusMessenger.instance.message(Messages.getString("MainWindow.StatusDownload"));
      }
    } else if (event.getSource() instanceof RenameWorker) {
      final RenameWorker renameWorker = (RenameWorker) event.getSource();
      if (renameWorker.isDone()) {
        pnlSearchFile.getResultPanel().enableButtons();
        progressDialog.setVisible(false);
      } else {
        final int progress = renameWorker.getProgress();
        progressDialog.updateProgress(progress);
        StatusMessenger.instance.message(Messages.getString("MainWindow.StatusRename"));
      }
    }
  }

  private void close() {
    Logger.instance.log("close", Level.TRACE);
    settingsControl.getSettings().setOptionRecursive(pnlSearchFileInput.isRecursiveSelected());
    storeScreenSettings();
    settingsControl.store();
  }

  private void storeScreenSettings() {
    VideoTable videoTable = pnlSearchFile.getResultPanel().getTable();
    settingsControl.getSettings().getScreenSettings()
        .setHideEpisode(videoTable.isHideColumn(SearchColumnName.EPISODE));
    settingsControl.getSettings().getScreenSettings()
        .setHideFilename(videoTable.isHideColumn(SearchColumnName.FILENAME));
    settingsControl.getSettings().getScreenSettings()
        .setHideSeason(videoTable.isHideColumn(SearchColumnName.SEASON));
    settingsControl.getSettings().getScreenSettings()
        .setHideTitle(videoTable.isHideColumn(SearchColumnName.TITLE));
    settingsControl.getSettings().getScreenSettings()
        .setHideType(videoTable.isHideColumn(SearchColumnName.TYPE));

  }

  public ProgressDialog setProgressDialog(Cancelable worker) {
    progressDialog = new ProgressDialog(this, worker);
    return progressDialog;
  }

  public void showProgressDialog() {
    this.progressDialog.setVisible(true);
  }

  public void hideProgressDialog() {
    this.progressDialog.setVisible(false);
  }

  public void setStatusMessage(String message) {
    StatusMessenger.instance.message(message);
  }

  public void updateProgressDialog(int progress) {
    progressDialog.updateProgress(progress);
  }
}
