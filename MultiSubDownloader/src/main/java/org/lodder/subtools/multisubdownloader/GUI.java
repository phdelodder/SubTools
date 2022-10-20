package org.lodder.subtools.multisubdownloader;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.lodder.subtools.multisubdownloader.exceptions.SearchSetupException;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.event.Emitter;
import org.lodder.subtools.multisubdownloader.gui.Menu;
import org.lodder.subtools.multisubdownloader.gui.actions.search.FileGuiSearchAction;
import org.lodder.subtools.multisubdownloader.gui.actions.search.TextGuiSearchAction;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.dialog.MappingEpisodeNameDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.PreferenceDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.ProgressDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.RenameDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.progress.fileindexer.IndexingProgressDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.progress.search.SearchProgressDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.MyPopupMenu;
import org.lodder.subtools.multisubdownloader.gui.extra.PopupListener;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusLabel;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.extra.table.CustomTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SearchColumnName;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.LoggingPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.ResultPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchFileInputPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchTextInputPanel;
import org.lodder.subtools.multisubdownloader.gui.workers.DownloadWorker;
import org.lodder.subtools.multisubdownloader.gui.workers.RenameWorker;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.multisubdownloader.util.Export;
import org.lodder.subtools.multisubdownloader.util.Import;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.OsCheck;
import org.lodder.subtools.sublibrary.OsCheck.OSType;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.StringUtil;
import org.lodder.subtools.sublibrary.util.XmlFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI extends JFrame implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private final Container app;
    private final Manager manager;
    private final UserInteractionHandlerGUI userInteractionHandler;
    private StatusLabel lblStatus;
    private final SettingsControl settingsControl;
    private ProgressDialog progressDialog;
    private MyPopupMenu popupMenu;
    private SearchPanel<SearchFileInputPanel> pnlSearchFile;
    private SearchPanel<SearchTextInputPanel> pnlSearchText;
    private JPanel pnlLogging;
    private SearchTextInputPanel pnlSearchTextInput;
    private SearchFileInputPanel pnlSearchFileInput;
    private Menu menuBar;
    private SearchProgressDialog searchProgressDialog;
    private IndexingProgressDialog fileIndexerProgressDialog;

    private static final Logger LOGGER = LoggerFactory.getLogger(GUI.class);

    /**
     * Create the application.
     */
    public GUI(final SettingsControl settingsControl, Container app) {
        this.app = app;
        this.manager = (Manager) this.app.make("Manager");
        this.userInteractionHandler = new UserInteractionHandlerGUI(settingsControl.getSettings(), this);
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
        new Thread(() -> checkUpdate(false)).start();
        initPopupMenu();
    }

    private void checkUpdate(final boolean forceUpdateCheck) {
        UpdateAvailableGithub u = new UpdateAvailableGithub(manager, settingsControl);
        Optional<String> updateUrl = (forceUpdateCheck && u.isNewVersionAvailable())
                || (!forceUpdateCheck && u.shouldCheckForNewUpdate(settingsControl.getSettings().getUpdateCheckPeriod())
                        && u.isNewVersionAvailable()) ? u.getLatestDownloadUrl() : Optional.empty();
        if (updateUrl.isPresent()) {
            final JEditorPane editorPane = new JEditorPane();
            editorPane.setPreferredSize(new Dimension(800, 50));
            editorPane.setEditable(false);
            editorPane.setContentType("text/html");

            editorPane.setText("<html>" + Messages.getString("UpdateAppAvailable") + "!: </br><A HREF="
                    + updateUrl.get() + ">" + updateUrl.get() + "</a></html>");

            editorPane.addHyperlinkListener(hyperlinkEvent -> {
                if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED && Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(hyperlinkEvent.getURL().toURI());
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }
                }
            });
            JOptionPane.showMessageDialog(this, editorPane, ConfigProperties.getInstance().getProperty("name"), JOptionPane.INFORMATION_MESSAGE);
        } else if (forceUpdateCheck) {
            JOptionPane.showMessageDialog(this, Messages.getString("MainWindow.NoUpdateAvailable"),
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
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 448, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 125, 15, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 1.0, 1.0, 0.0, Double.MIN_VALUE };
        getContentPane().setLayout(gridBagLayout);

        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
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

        menuBar.setFileQuitAction(arg0 -> close());

        menuBar.setViewFilenameAction(actionEvent -> {
            CustomTable customTable = pnlSearchFile.getResultPanel().getTable();
            if (menuBar.isViewFilenameSelected()) {
                customTable.unhideColumn(SearchColumnName.FILENAME);
            } else {
                customTable.hideColumn(SearchColumnName.FILENAME);
            }
        });

        menuBar.setViewTypeAction(arg0 -> {
            CustomTable customTable = pnlSearchFile.getResultPanel().getTable();
            if (menuBar.isViewTitleSelected()) {
                customTable.unhideColumn(SearchColumnName.TYPE);
            } else {
                customTable.hideColumn(SearchColumnName.TYPE);
            }
        });

        menuBar.setViewTitleAction(arg0 -> {
            CustomTable customTable = pnlSearchFile.getResultPanel().getTable();
            if (menuBar.isViewTitleSelected()) {
                customTable.unhideColumn(SearchColumnName.TITLE);
            } else {
                customTable.hideColumn(SearchColumnName.TITLE);
            }
        });

        menuBar.setViewSeasonAction(arg0 -> {
            CustomTable customTable = pnlSearchFile.getResultPanel().getTable();
            if (menuBar.isViewSeasonSelected()) {
                customTable.unhideColumn(SearchColumnName.SEASON);
            } else {
                customTable.hideColumn(SearchColumnName.SEASON);
            }
        });

        menuBar.setViewEpisodeAction(arg0 -> {
            CustomTable customTable = pnlSearchFile.getResultPanel().getTable();
            if (menuBar.isViewEpisodeSelected()) {
                customTable.unhideColumn(SearchColumnName.EPISODE);
            } else {
                customTable.hideColumn(SearchColumnName.EPISODE);
            }
        });

        menuBar.setViewShowOnlyFoundAction(arg0 -> {
            CustomTable customTable = pnlSearchFile.getResultPanel().getTable();
            settingsControl.getSettings().setOptionsShowOnlyFound(menuBar.isShowOnlyFound());
            ((VideoTableModel) customTable.getModel()).setShowOnlyFound(settingsControl.getSettings()
                    .isOptionsShowOnlyFound());
        });

        menuBar.setViewClearLogAction(arg0 -> ((LoggingPanel) pnlLogging).setLogText(""));

        menuBar.setEditRenameTVAction(arg0 -> {
            final RenameDialog rDialog =
                    new RenameDialog(getThis(), settingsControl.getSettings(), VideoType.EPISODE, manager, userInteractionHandler);
            rDialog.setVisible(true);
        });

        menuBar.setEditRenameMovieAction(arg0 -> {
            final RenameDialog rDialog =
                    new RenameDialog(getThis(), settingsControl.getSettings(), VideoType.MOVIE, manager, userInteractionHandler);
            rDialog.setVisible(true);
        });

        menuBar.setEditPreferencesAction(arg0 -> {
            final PreferenceDialog pDialog =
                    new PreferenceDialog(getThis(), settingsControl, (Emitter) app.make("EventEmitter"), manager, userInteractionHandler);
            pDialog.setVisible(true);
        });

        menuBar.setTranslateShowNamesAction(arg0 -> showTranslateShowNames());

        menuBar.setExportExclusionsAction(arg0 -> exportList(manager, Export.ExportListType.EXCLUDE));

        menuBar.setImportExclusionsAction(arg0 -> importList(manager, Import.ImportListType.EXCLUDE));

        menuBar.setExportPreferencesAction(arg0 -> exportList(manager, Export.ExportListType.PREFERENCES));

        menuBar.setImportPreferencesAction(arg0 -> importList(manager, Import.ImportListType.PREFERENCES));

        menuBar.setCheckUpdateAction(arg0 -> checkUpdate(true));

        menuBar.setAboutAction(arg0 -> showAbout());
    }

    private void createTextSearchPanel() {
        Settings settings = this.settingsControl.getSettings();

        /* resolve the SubtitleProviderStore from the Container */
        SubtitleProviderStore subtitleProviderStore = (SubtitleProviderStore) this.app.make("SubtitleProviderStore");
        ResultPanel resultPanel = new ResultPanel();
        pnlSearchTextInput = new SearchTextInputPanel();
        pnlSearchText = new SearchPanel<>(pnlSearchTextInput, resultPanel);
        pnlSearchTextInput.setSelectedlanguage(settings.getSubtitleLanguage() == null ? Language.DUTCH : settings.getSubtitleLanguage());
        resultPanel.showSelectFoundSubtitlesButton();
        resultPanel.setTable(createSubtitleTable());
        resultPanel.setDownloadAction(arg0 -> downloadText());

        TextGuiSearchAction searchAction;
        try {
            searchAction = TextGuiSearchAction.createWithSettings(settings)
                    .subtitleProviderStore(subtitleProviderStore)
                    .mainwindow(this)
                    .searchPanel(pnlSearchText)
                    .releaseFactory(new ReleaseFactory(settings, (Manager) app.make("Manager")))
                    .build();
            pnlSearchTextInput.setSearchAction(searchAction);
        } catch (SearchSetupException e) {
            throw new RuntimeException(e);
        }
    }

    private CustomTable createSubtitleTable() {
        CustomTable subtitleTable = new CustomTable();
        subtitleTable.setModel(VideoTableModel.getDefaultSubtitleTableModel());
        final RowSorter<TableModel> sorterSubtitle = new TableRowSorter<>(subtitleTable.getModel());
        subtitleTable.setRowSorter(sorterSubtitle);
        subtitleTable.hideColumn(SearchColumnName.OBJECT);
        return subtitleTable;
    }

    private void createFileSearchPanel() {
        Settings settings = this.settingsControl.getSettings();

        ResultPanel resultPanel = new ResultPanel();
        pnlSearchFileInput = new SearchFileInputPanel();
        pnlSearchFileInput.setRecursiveSelected(settings.isOptionRecursive());
        pnlSearchFileInput.setSelectedlanguage(settings.getSubtitleLanguage() == null ? Language.DUTCH : settings.getSubtitleLanguage());
        pnlSearchFile = new SearchPanel<>(pnlSearchFileInput, resultPanel);

        resultPanel.setTable(createVideoTable());

        try {
            FileGuiSearchAction searchAction = FileGuiSearchAction
                    .createWithSettings(settings)
                    .subtitleProviderStore((SubtitleProviderStore) this.app.make("SubtitleProviderStore"))
                    .mainwindow(this)
                    .searchPanel(pnlSearchFile)
                    .releaseFactory(new ReleaseFactory(settings, (Manager) app.make("Manager")))
                    .build();

            pnlSearchFileInput.setSelectFolderAction(arg0 -> selectIncomingFolder());
            pnlSearchFileInput.setSearchAction(searchAction);

            resultPanel.setDownloadAction(arg0 -> download());
            resultPanel.setMoveAction(arg0 -> {
                final int response =
                        JOptionPane.showConfirmDialog(
                                getThis(),
                                Messages.getString("MainWindow.OnlyMoveToLibraryStructure"), Messages.getString("MainWindow.Confirm"), //$NON-NLS-2$
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    rename();
                }
            });
        } catch (SearchSetupException e) {
            throw new RuntimeException(e);
        }
    }

    private CustomTable createVideoTable() {
        CustomTable customTable = new CustomTable();
        customTable.setModel(VideoTableModel.getDefaultVideoTableModel());
        ((VideoTableModel) customTable.getModel()).setShowOnlyFound(settingsControl.getSettings().isOptionsShowOnlyFound());
        ((VideoTableModel) customTable.getModel()).setUserInteractionHandler(userInteractionHandler);
        final RowSorter<TableModel> sorter = new TableRowSorter<>(customTable.getModel());
        customTable.setRowSorter(sorter);
        customTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        int columnId = customTable.getColumnIdByName(SearchColumnName.FOUND);
        customTable.getColumnModel().getColumn(columnId).setResizable(false);
        customTable.getColumnModel().getColumn(columnId).setPreferredWidth(100);
        customTable.getColumnModel().getColumn(columnId).setMaxWidth(100);
        columnId = customTable.getColumnIdByName(SearchColumnName.SELECT);
        customTable.getColumnModel().getColumn(columnId).setResizable(false);
        customTable.getColumnModel().getColumn(columnId).setPreferredWidth(85);
        customTable.getColumnModel().getColumn(columnId).setMaxWidth(85);
        customTable.hideColumn(SearchColumnName.OBJECT);
        customTable.hideColumn(SearchColumnName.SEASON);
        customTable.hideColumn(SearchColumnName.EPISODE);
        customTable.hideColumn(SearchColumnName.TYPE);
        customTable.hideColumn(SearchColumnName.TITLE);
        return customTable;
    }

    private void restoreScreenSettings() {
        CustomTable customTable = pnlSearchFile.getResultPanel().getTable();
        if (settingsControl.getSettings().getScreenSettings().isHideEpisode()) {
            customTable.hideColumn(SearchColumnName.EPISODE);
        } else {
            menuBar.setViewEpisodeSelected(true);
            customTable.unhideColumn(SearchColumnName.EPISODE);
        }
        if (settingsControl.getSettings().getScreenSettings().isHideFilename()) {
            customTable.hideColumn(SearchColumnName.FILENAME);
        } else {
            menuBar.setViewFileNameSelected(true);
            customTable.unhideColumn(SearchColumnName.FILENAME);
        }
        if (settingsControl.getSettings().getScreenSettings().isHideSeason()) {
            customTable.hideColumn(SearchColumnName.SEASON);
        } else {
            menuBar.setViewSeasonSelected(true);
            customTable.unhideColumn(SearchColumnName.SEASON);
        }
        if (settingsControl.getSettings().getScreenSettings().isHideType()) {
            customTable.hideColumn(SearchColumnName.TYPE);
        } else {
            menuBar.setViewTitleSelected(true);
            customTable.unhideColumn(SearchColumnName.TYPE);
        }
        if (settingsControl.getSettings().getScreenSettings().isHideTitle()) {
            customTable.hideColumn(SearchColumnName.TITLE);
        } else {
            menuBar.setViewTitleSelected(true);
            customTable.unhideColumn(SearchColumnName.TITLE);
        }
    }

    private void initPopupMenu() {
        popupMenu = new MyPopupMenu();
        JMenuItem menuItem = new JMenuItem(Messages.getString("MainWindow.Copy"));
        menuItem.addActionListener(arg0 -> {
            final CustomTable t = (CustomTable) popupMenu.getInvoker();
            final DefaultTableModel model = (DefaultTableModel) t.getModel();

            int col = t.columnAtPoint(popupMenu.getClickLocation());
            int row = t.rowAtPoint(popupMenu.getClickLocation());

            try {
                StringSelection selection = new StringSelection((String) model.getValueAt(row, col));
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            } catch (HeadlessException e) {
                LOGGER.error("initPopupMenu", e);
            }
        });
        popupMenu.add(menuItem);
        // add the listener to the jtable
        MouseListener popupListener = new PopupListener(popupMenu);
        // add the listener specifically to the header
        CustomTable customTable = pnlSearchFile.getResultPanel().getTable();
        CustomTable subtitleTable = pnlSearchText.getResultPanel().getTable();
        customTable.addMouseListener(popupListener);
        customTable.getTableHeader().addMouseListener(popupListener);
        subtitleTable.addMouseListener(popupListener);
        subtitleTable.getTableHeader().addMouseListener(popupListener);
    }

    protected void showTranslateShowNames() {
        final MappingEpisodeNameDialog tDialog = new MappingEpisodeNameDialog(this, settingsControl, (Manager) this.app.make("Manager"));
        tDialog.setVisible(true);
    }

    private void showAbout() {
        JOptionPane.showConfirmDialog(this,
                Messages.getString("MainWindow.CurrentVersion") + ": "
                        + ConfigProperties.getInstance().getProperty(Messages.getString("MainWindow.Version")),
                ConfigProperties.getInstance().getProperty("name"), JOptionPane.CLOSED_OPTION);
    }

    protected void rename() {
        CustomTable customTable = pnlSearchFile.getResultPanel().getTable();
        RenameWorker renameWorker =
                new RenameWorker(customTable, settingsControl.getSettings(), (Manager) this.app.make("Manager"), userInteractionHandler);
        renameWorker.addPropertyChangeListener(this);
        pnlSearchFile.getResultPanel().enableButtons();
        progressDialog = new ProgressDialog(this, renameWorker);
        progressDialog.setVisible(true);
        renameWorker.execute();
    }

    private void download() {
        CustomTable customTable = pnlSearchFile.getResultPanel().getTable();
        DownloadWorker downloadWorker = new DownloadWorker(customTable, settingsControl.getSettings(), (Manager) this.app.make("Manager"), this);
        downloadWorker.addPropertyChangeListener(this);
        pnlSearchFile.getResultPanel().disableButtons();
        progressDialog = new ProgressDialog(this, downloadWorker);
        progressDialog.setVisible(true);
        downloadWorker.execute();
    }

    private void downloadText() {
        CustomTable subtitleTable = pnlSearchText.getResultPanel().getTable();
        final VideoTableModel model = (VideoTableModel) subtitleTable.getModel();
        File path = MemoryFolderChooser.getInstance().selectDirectory(getContentPane(), Messages.getString("MainWindow.SelectFolder"));

        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Boolean) model.getValueAt(i, subtitleTable.getColumnIdByName(SearchColumnName.SELECT))) {
                final Subtitle subtitle = (Subtitle) model.getValueAt(i, subtitleTable.getColumnIdByName(SearchColumnName.OBJECT));
                String filename = "";
                if (!subtitle.getFileName().endsWith(".srt")) {
                    filename = subtitle.getFileName() + ".srt";
                }
                if (OsCheck.getOperatingSystemType() == OSType.Windows) {
                    filename = StringUtil.removeIllegalWindowsChars(filename);
                }

                try {
                    if (subtitle.getSourceLocation() == Subtitle.SourceLocation.FILE) {
                        Files.copy(subtitle.getFile(), new File(path, subtitle.getFileName()));
                    } else {
                        Manager manager = (Manager) this.app.make("Manager");
                        String url =
                                subtitle.getSourceLocation() == Subtitle.SourceLocation.URL ? subtitle.getUrl() : subtitle.getUrlSupplier().get();
                        manager.store(url, new File(path, filename));
                    }
                } catch (IOException | ManagerException e) {
                    LOGGER.error("downloadText", e);
                } catch (SubtitlesProviderException e) {
                    LOGGER.error("Error while getting url for [%s] for subtitle provider [%s] (%s)".formatted(filename, e.getSubtitleProvider(),
                            e.getMessage()), e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected JFrame getThis() {
        return this;
    }

    public void showErrorMessage(String message) {
        JOptionPane.showConfirmDialog(this, message, ConfigProperties.getInstance().getProperty("name"), JOptionPane.CLOSED_OPTION,
                JOptionPane.ERROR_MESSAGE);
    }

    private void importList(Manager manager, Import.ImportListType listType) {
        // Create a file chooser
        final JFileChooser fc = new JFileChooser();
        XmlFileFilter filter = new XmlFileFilter();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(filter);
        final int returnVal = fc.showOpenDialog(getThis());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            Import i = new Import(getThis(), settingsControl);
            i.doImport(manager, listType, fc.getSelectedFile());
        }
    }

    private void exportList(Manager manager, Export.ExportListType listType) {
        // Create a file chooser
        final JFileChooser fc = new JFileChooser();
        XmlFileFilter filter = new XmlFileFilter();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(filter);
        final int returnVal = fc.showSaveDialog(getThis());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            Export e = new Export(settingsControl);
            File f = fc.getSelectedFile();
            if (!"xml".equalsIgnoreCase(XmlFileFilter.getExtension(f))) {
                f = new File(f + ".xml");
            }
            e.doExport(manager, listType, f);
        }

    }

    private void selectIncomingFolder() {
        File path = MemoryFolderChooser.getInstance().selectDirectory(getThis(), Messages.getString("MainWindow.SelectFolder"));
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
        settingsControl.getSettings().setOptionRecursive(pnlSearchFileInput.isRecursiveSelected());
        settingsControl.getSettings().setSubtitleLanguage(pnlSearchFileInput.getSelectedLanguage());
        storeScreenSettings();
        settingsControl.store();
    }

    private void storeScreenSettings() {
        CustomTable customTable = pnlSearchFile.getResultPanel().getTable();
        settingsControl.getSettings().getScreenSettings().setHideEpisode(customTable.isHideColumn(SearchColumnName.EPISODE));
        settingsControl.getSettings().getScreenSettings().setHideFilename(customTable.isHideColumn(SearchColumnName.FILENAME));
        settingsControl.getSettings().getScreenSettings().setHideSeason(customTable.isHideColumn(SearchColumnName.SEASON));
        settingsControl.getSettings().getScreenSettings().setHideTitle(customTable.isHideColumn(SearchColumnName.TITLE));
        settingsControl.getSettings().getScreenSettings().setHideType(customTable.isHideColumn(SearchColumnName.TYPE));

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

    public SearchProgressDialog createSearchProgressDialog(Cancelable searchAction) {
        searchProgressDialog = new SearchProgressDialog(this, searchAction);
        return searchProgressDialog;
    }

    // public void hideSearchProgressDialog() {
    // if (searchProgressDialog == null) {
    // return;
    // }
    // searchProgressDialog.setVisible(false);
    // }

    public IndexingProgressDialog createFileIndexerProgressDialog(Cancelable searchAction) {
        fileIndexerProgressDialog = new IndexingProgressDialog(this, searchAction);
        return fileIndexerProgressDialog;
    }

    public void hideFileIndexerProgressDialog() {
        if (fileIndexerProgressDialog == null) {
            return;
        }
        fileIndexerProgressDialog.setVisible(false);
    }
}
