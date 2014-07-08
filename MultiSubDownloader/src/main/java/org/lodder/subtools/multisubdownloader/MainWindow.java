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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.lodder.subtools.multisubdownloader.gui.dialog.MappingEpisodeNameDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.PreferenceDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.ProgressDialog;
import org.lodder.subtools.multisubdownloader.gui.dialog.RenameDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.LogTextArea;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.MyPopupMenu;
import org.lodder.subtools.multisubdownloader.gui.extra.PopupListener;
import org.lodder.subtools.multisubdownloader.gui.extra.SearchPanel;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusLabel;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SearchColumnName;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.workers.DownloadWorker;
import org.lodder.subtools.multisubdownloader.gui.workers.RenameWorker;
import org.lodder.subtools.multisubdownloader.gui.workers.SearchFileWorker;
import org.lodder.subtools.multisubdownloader.gui.workers.SearchNameWorker;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.util.Export;
import org.lodder.subtools.multisubdownloader.util.Import;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.OsCheck;
import org.lodder.subtools.sublibrary.OsCheck.OSType;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.VideoSearchType;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.model.Subtitle.SubtitleSource;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.StringUtils;
import org.lodder.subtools.sublibrary.util.XmlFileFilter;
import org.lodder.subtools.sublibrary.util.http.DropBoxClient;
import org.lodder.subtools.sublibrary.util.http.HttpClient;

import net.miginfocom.swing.MigLayout;

public class MainWindow extends JFrame implements PropertyChangeListener {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private VideoTable videoTable;
	private JTextField txtIncomingPath;
	private JCheckBox chkRecursive;
	private StatusLabel lblStatus;
	private JComboBox<String> cbxLanguageText;
	private JComboBox<String> cbxLanguageFile;
	private final SettingsControl settingsControl;
	private ProgressDialog progressDialog;
	private JButton btnSearch;
	private JCheckBox chkforceSubtitleOverwrite;
	private JTextField txtInputEpisode;
	private JTextField txtInputSeason;
	private JTextField txtInputVideoName;
	private JComboBox<VideoSearchType> cbxVideoType;
	private VideoTable subtitleTable;
	private JButton btnSearchText;
	private JTextField txtQualityVersion;
	private MyPopupMenu popupMenu;
	private SearchPanel pnlSearchFile;
	private SearchPanel pnlSearchText;
	private final String[] languageSelection = new String[] { "Nederlands",
			"Engels" };
	private JCheckBoxMenuItem chckbxmntmTitle;
	private JCheckBoxMenuItem chckbxmntmType;
	private JCheckBoxMenuItem chckbxmntmBestandsnaam;
	private JCheckBoxMenuItem chckbxmntmSeason;
	private JCheckBoxMenuItem chckbxmntmEpisode;

	/**
	 * Create the application.
	 */
	public MainWindow(final SettingsControl settingsControl) {
		setTitle("Multi Sub Downloader");
		/*
		 * setIconImage(Toolkit.getDefaultToolkit().getImage(
		 * getClass().getResource("/resources/Bierdopje_bigger.png")));
		 */
		this.settingsControl = settingsControl;
		initialize();
		restoreScreenSettings();
		pnlSearchFile.setEnableDownloadButtons(false);
		pnlSearchText.setEnableDownloadButtons(false);
		checkUpdate(false);
		initPopupMenu();
	}

	private void checkUpdate(final boolean showNoUpdate) {
		UpdateAvailableDropbox u = new UpdateAvailableDropbox();
		if (u.checkProgram()) {
			final JEditorPane editorPane = new JEditorPane();
			editorPane.setPreferredSize(new Dimension(800, 50));
			editorPane.setEditable(false);
			editorPane.setContentType("text/html");
			editorPane.setText("<html>Update available!: </br><A HREF="
					+ u.getUpdateUrl() + ">" + u.getUpdateUrl() + "</a</html>");
			editorPane.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(final HyperlinkEvent hyperlinkEvent) {
					if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED
							&& Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(
									hyperlinkEvent.getURL().toURI());
						} catch (Exception e) {
							Logger.instance.error(Logger.stack2String(e));
						}
					}
				}
			});
			JOptionPane.showMessageDialog(this, editorPane,
					"MultiSubDownloader", JOptionPane.INFORMATION_MESSAGE);
		} else if (showNoUpdate) {
			JOptionPane.showMessageDialog(
					this,
					"Geen nieuwe update beschikbaar"
							+ ", huidige versie: "
							+ ConfigProperties.getInstance().getProperty(
									"version"), "MultiSubDownloader",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		MemoryFolderChooser.getInstance().setMemory(
				settingsControl.getSettings().getLastOutputDir());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		setBounds(100, 100, 925, 680);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 448, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 125, 15, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0, 0.0,
				Double.MIN_VALUE };
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

		pnlSearchFile = new SearchPanel();
		tabbedPane.addTab("Zoeken op bestanden", null, pnlSearchFile, null);

		final JPanel pnlSearchFileInput = new JPanel();
		pnlSearchFile.setInputPanel(pnlSearchFileInput);
		pnlSearchFileInput.setLayout(new MigLayout("", "[][][][][][]",
				"[][][][][][]"));

		final JLabel lblLocatieNieuweAfleveringen = new JLabel(
				"Locatie nieuwe afleveringen");
		pnlSearchFileInput.add(lblLocatieNieuweAfleveringen,
				"cell 1 0,alignx trailing");

		txtIncomingPath = new JTextField();
		pnlSearchFileInput.add(txtIncomingPath, "cell 2 0,alignx leading");
		txtIncomingPath.setColumns(20);

		final JButton btnBrowse = new JButton("Bladeren");
		pnlSearchFileInput.add(btnBrowse, "cell 3 0");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectIncomingFolder();
			}
		});

		chkRecursive = new JCheckBox("Mappen in map doorzoeken");
		chkRecursive.setSelected(this.settingsControl.getSettings()
				.isOptionRecursive());
		pnlSearchFileInput.add(chkRecursive, "cell 2 1 2 1");

		chkforceSubtitleOverwrite = new JCheckBox(
				"Negeer bestaande ondertitel bestanden");
		pnlSearchFileInput.add(chkforceSubtitleOverwrite, "cell 2 3 2 1");

		btnSearch = new JButton("Zoeken naar ondertitels");
		pnlSearchFileInput.add(btnSearch, "cell 0 5 3 1,alignx center");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					searchFile();
				} catch (final Exception e) {
					showErrorMessage(e.getMessage());
					lblStatus.setText(e.getMessage());
				}
			}
		});

		final JLabel lblSelecteerDeGewenste = new JLabel(
				"Selecteer de gewenste ondertitel taal");
		pnlSearchFileInput.add(lblSelecteerDeGewenste, "cell 2 2");

		cbxLanguageFile = new JComboBox<String>();
		pnlSearchFileInput.add(cbxLanguageFile, "cell 3 2");
		cbxLanguageFile.setModel(new DefaultComboBoxModel<String>(
				languageSelection));
		cbxLanguageFile.setSelectedIndex(0);

		pnlSearchFile.setActionDownload(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				download();
			}
		});

		pnlSearchFile.setActionMove(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final int response = JOptionPane
						.showConfirmDialog(
								getThis(),
								"Dit is enkel verplaatsen naar de bibliotheek structuur!",
								"Bevestigen", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION) {
					rename();
				}
			}
		});

		videoTable = new VideoTable();
		pnlSearchFile.setTable(videoTable);
		videoTable.setModel(VideoTableModel.getDefaultVideoTableModel());
		((VideoTableModel) videoTable.getModel())
				.setShowOnlyFound(settingsControl.getSettings()
						.isOptionsShowOnlyFound());
		final RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				videoTable.getModel());
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

		pnlSearchText = new SearchPanel();
		tabbedPane.addTab("Zoeken op naam", null, pnlSearchText, null);
		pnlSearchText.setSelectFoundVisible(false);

		JPanel pnlSearchTextInput = new JPanel();
		pnlSearchText.setInputPanel(pnlSearchTextInput);
		pnlSearchTextInput.setLayout(new MigLayout("",
				"[][][][][][][][grow][]", "[][][][][]"));

		cbxVideoType = new JComboBox<VideoSearchType>();
		cbxVideoType.setModel(new DefaultComboBoxModel<VideoSearchType>(
				VideoSearchType.values()));
		cbxVideoType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				VideoSearchType videoTypeChoice = (VideoSearchType) cbxVideoType
						.getSelectedItem();
				if (videoTypeChoice.equals(VideoSearchType.EPISODE)) {
					txtInputSeason.setEditable(true);
					txtInputSeason.setEnabled(true);
					txtInputEpisode.setEditable(true);
					txtInputEpisode.setEnabled(true);
				} else {
					txtInputSeason.setEditable(false);
					txtInputSeason.setEnabled(false);
					txtInputEpisode.setEditable(false);
					txtInputEpisode.setEnabled(false);
				}
				if (videoTypeChoice.equals(VideoSearchType.RELEASE)) {
					txtQualityVersion.setEditable(false);
					txtQualityVersion.setEnabled(false);
				} else {
					txtQualityVersion.setEditable(true);
					txtQualityVersion.setEnabled(true);
				}

			}
		});
		pnlSearchTextInput.add(cbxVideoType, "cell 1 0,growx");

		txtInputVideoName = new JTextField();
		pnlSearchTextInput.add(txtInputVideoName, "cell 2 0 5 1,growx");
		txtInputVideoName.setColumns(10);

		JLabel lblKwaliteitversie = new JLabel("Kwaliteit\\Versie");
		pnlSearchTextInput.add(lblKwaliteitversie, "cell 1 1,alignx trailing");

		txtQualityVersion = new JTextField();
		pnlSearchTextInput.add(txtQualityVersion, "cell 2 1,growx");
		txtQualityVersion.setColumns(10);

		JLabel lblSeizoen = new JLabel("Seizoen");
		pnlSearchTextInput.add(lblSeizoen, "cell 3 1,alignx trailing");

		txtInputSeason = new JTextField();
		pnlSearchTextInput.add(txtInputSeason, "cell 4 1,alignx left");
		txtInputSeason.setColumns(5);

		JLabel lblAflevering = new JLabel("Aflevering");
		pnlSearchTextInput.add(lblAflevering, "cell 5 1,alignx trailing");

		txtInputEpisode = new JTextField();
		pnlSearchTextInput.add(txtInputEpisode, "cell 6 1,growx");
		txtInputEpisode.setColumns(5);

		JLabel lblSelecteerDeGewenste1 = new JLabel(
				"Selecteer de gewenste ondertitel taal");
		pnlSearchTextInput.add(lblSelecteerDeGewenste1,
				"cell 1 2 3 1,alignx trailing");

		cbxLanguageText = new JComboBox<String>();
		cbxLanguageText.setModel(new DefaultComboBoxModel<String>(
				languageSelection));
		pnlSearchTextInput.add(cbxLanguageText, "cell 4 2 2 1,growx");

		btnSearchText = new JButton("Zoeken naar ondertitels");
		btnSearchText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				searchName();
			}
		});
		pnlSearchTextInput.add(btnSearchText, "cell 2 4 2 1");

		subtitleTable = new VideoTable();
		pnlSearchText.setTable(subtitleTable);
		subtitleTable.setModel(VideoTableModel.getDefaultSubtitleTableModel());
		final RowSorter<TableModel> sorterSubtitle = new TableRowSorter<TableModel>(
				subtitleTable.getModel());
		subtitleTable.setRowSorter(sorterSubtitle);
		subtitleTable.hideColumn(SearchColumnName.OBJECT);

		pnlSearchText.setActionDownload(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				downloadText();
			}
		});

		final JPanel pnlLogging = new JPanel();
		final GridBagConstraints gbc_pnlLogging = new GridBagConstraints();
		gbc_pnlLogging.fill = GridBagConstraints.BOTH;
		gbc_pnlLogging.insets = new Insets(0, 0, 5, 0);
		gbc_pnlLogging.gridx = 0;
		gbc_pnlLogging.gridy = 1;
		getContentPane().add(pnlLogging, gbc_pnlLogging);
		pnlLogging.setLayout(new MigLayout("", "[698px,grow][]",
				"[][70px,grow]"));

		final JScrollPane scrollPane_1 = new JScrollPane();
		pnlLogging.add(new JLabel("Logging"), "cell 0 0,alignx right,gaptop 5");
		pnlLogging.add(new JSeparator(), "cell 0 0,growx,gaptop 5");

		final JComboBox<Level> cbxLogLevel = new JComboBox<Level>();
		cbxLogLevel.setModel(new DefaultComboBoxModel<Level>(Level.values()));
		cbxLogLevel.setSelectedItem(Logger.instance.getLogLevel());
		cbxLogLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Logger.instance.setLogLevel((Level) cbxLogLevel
						.getSelectedItem());
			}
		});
		pnlLogging.add(cbxLogLevel, "cell 1 0,alignx right");
		pnlLogging.add(scrollPane_1, "cell 0 1 2 1,grow");

		final LogTextArea txtLogging = new LogTextArea();
		Logger.instance.addListener(txtLogging);
		scrollPane_1.setViewportView(txtLogging);
		txtLogging.setEditable(false);
		txtLogging.setAutoScroll(true);

		lblStatus = new StatusLabel("");
		StatusMessenger.instance.addListener(lblStatus);
		final GridBagConstraints gbc_lblStatus = new GridBagConstraints();
		gbc_lblStatus.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblStatus.gridx = 0;
		gbc_lblStatus.gridy = 2;
		getContentPane().add(lblStatus, gbc_lblStatus);

		final JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		final JMenu mnFile = new JMenu("Bestand");
		menuBar.add(mnFile);

		final JMenuItem mntmQuit = new JMenuItem("Afsluiten");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
				System.exit(-1);
			}
		});

		mnFile.add(mntmQuit);

		JMenu mnBeeld = new JMenu("Beeld");
		menuBar.add(mnBeeld);

		JMenu mnZoekResulaten = new JMenu("Zoek Resulaten ");
		mnBeeld.add(mnZoekResulaten);

		chckbxmntmBestandsnaam = new JCheckBoxMenuItem("Bestandsnaam");
		chckbxmntmBestandsnaam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (chckbxmntmBestandsnaam.isSelected()) {
					videoTable.unhideColumn(SearchColumnName.FILENAME);
				} else {
					videoTable.hideColumn(SearchColumnName.FILENAME);
				}
			}
		});

		chckbxmntmType = new JCheckBoxMenuItem("Type");
		chckbxmntmType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxmntmType.isSelected()) {
					videoTable.unhideColumn(SearchColumnName.TYPE);
				} else {
					videoTable.hideColumn(SearchColumnName.TYPE);
				}
			}
		});
		mnZoekResulaten.add(chckbxmntmType);
		mnZoekResulaten.add(chckbxmntmBestandsnaam);

		chckbxmntmTitle = new JCheckBoxMenuItem("Titel");
		chckbxmntmTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxmntmTitle.isSelected()) {
					videoTable.unhideColumn(SearchColumnName.TITLE);
				} else {
					videoTable.hideColumn(SearchColumnName.TITLE);
				}
			}
		});
		mnZoekResulaten.add(chckbxmntmTitle);

		chckbxmntmSeason = new JCheckBoxMenuItem("Season");
		chckbxmntmSeason.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxmntmSeason.isSelected()) {
					videoTable.unhideColumn(SearchColumnName.SEASON);
				} else {
					videoTable.hideColumn(SearchColumnName.SEASON);
				}
			}
		});
		mnZoekResulaten.add(chckbxmntmSeason);

		chckbxmntmEpisode = new JCheckBoxMenuItem("Episode");
		chckbxmntmEpisode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxmntmEpisode.isSelected()) {
					videoTable.unhideColumn(SearchColumnName.EPISODE);
				} else {
					videoTable.hideColumn(SearchColumnName.EPISODE);
				}
			}
		});
		mnZoekResulaten.add(chckbxmntmEpisode);

		final JCheckBoxMenuItem chckbxmntmAlleenGevondenTonen = new JCheckBoxMenuItem(
				"Alleen gevonden tonen");
		chckbxmntmAlleenGevondenTonen.setSelected(settingsControl.getSettings()
				.isOptionsShowOnlyFound());
		chckbxmntmAlleenGevondenTonen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsControl.getSettings().setOptionsShowOnlyFound(
						chckbxmntmAlleenGevondenTonen.isSelected());
				((VideoTableModel) videoTable.getModel())
						.setShowOnlyFound(settingsControl.getSettings()
								.isOptionsShowOnlyFound());
			}
		});
		mnBeeld.add(chckbxmntmAlleenGevondenTonen);

		JMenuItem mntmLoggingWissen = new JMenuItem("Logging wissen");
		mntmLoggingWissen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtLogging.setText("");
			}
		});
		mnBeeld.add(mntmLoggingWissen);

		final JMenu mnEdit = new JMenu("Bewerken");
		menuBar.add(mnEdit);

		final JMenuItem mntmRenameSerieFiles = new JMenuItem(
				"Series Hernoemen...");
		mntmRenameSerieFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final RenameDialog rDialog = new RenameDialog(getThis(),
						settingsControl.getSettings(), VideoType.EPISODE);
				rDialog.setVisible(true);
			}
		});
		mnEdit.add(mntmRenameSerieFiles);

		JMenuItem mntmRenameMovieFiles = new JMenuItem("Films Hernoemen...");
		mntmRenameMovieFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final RenameDialog rDialog = new RenameDialog(getThis(),
						settingsControl.getSettings(), VideoType.MOVIE);
				rDialog.setVisible(true);
			}
		});
		mnEdit.add(mntmRenameMovieFiles);

		final JMenuItem mntmPreferences = new JMenuItem("Voorkeuren");
		mnEdit.add(mntmPreferences);
		mntmPreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final PreferenceDialog pDialog = new PreferenceDialog(
						getThis(), settingsControl);
				pDialog.setVisible(true);
			}
		});

		final JMenu mnImportexport = new JMenu("Serie Namen");
		menuBar.add(mnImportexport);

		final JMenuItem mntmTranslateShowNames = new JMenuItem(
				"Mapping Tvdb/Scene");
		mnImportexport.add(mntmTranslateShowNames);
		mntmTranslateShowNames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showTranslateShowNames();
			}
		});

		final JMenu mnImporteerexporteer = new JMenu("Importeer/Exporteer");
		menuBar.add(mnImporteerexporteer);

		final JMenuItem mntmExportTranslate = new JMenuItem(
				"Exporteer Mapping Tvdb/Scene");
		mnImporteerexporteer.add(mntmExportTranslate);

		final JMenuItem mntmImportTranslate = new JMenuItem(
				"Importeer Mapping Tvdb/Scene");
		mnImporteerexporteer.add(mntmImportTranslate);

		final JMenuItem mntmExporteerUistluitingen = new JMenuItem(
				"Exporteer Uitsluitingen");
		mntmExporteerUistluitingen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exportList(Export.ExportListType.EXCLUDE);
			}
		});
		mnImporteerexporteer.add(mntmExporteerUistluitingen);

		final JMenuItem mntmImporteerUitsluitingen = new JMenuItem(
				"Importeer Uitsluitingen");
		mntmImporteerUitsluitingen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				importList(Import.ImportListType.EXCLUDE);
			}
		});
		mnImporteerexporteer.add(mntmImporteerUitsluitingen);

		JMenuItem mntmExporteerVoorkeuren = new JMenuItem(
				"Exporteer Voorkeuren");
		mntmExporteerVoorkeuren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exportList(Export.ExportListType.PREFERENCES);
			}

		});
		mnImporteerexporteer.add(mntmExporteerVoorkeuren);

		JMenuItem mntmImporteerVoorkeuren = new JMenuItem(
				"Importeer Voorkeuren");
		mntmImporteerVoorkeuren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				importList(Import.ImportListType.PREFERENCES);
			}
		});
		mnImporteerexporteer.add(mntmImporteerVoorkeuren);
		mntmImportTranslate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				importList(Import.ImportListType.TRANSLATE);
			}
		});
		mntmExportTranslate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exportList(Export.ExportListType.TRANSLATE);
			}
		});

		final JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		final JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showAbout();
			}
		});

		JMenuItem mntmControlerenVoorUpdate = new JMenuItem(
				"Controleer voor update");
		mntmControlerenVoorUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				checkUpdate(true);
			}
		});
		mnHelp.add(mntmControlerenVoorUpdate);
		mnHelp.add(mntmAbout);
	}

	private void restoreScreenSettings() {
		if (settingsControl.getSettings().getScreenSettings().isHideEpisode()) {
			videoTable.hideColumn(SearchColumnName.EPISODE);
		} else {
			chckbxmntmEpisode.setSelected(true);
			videoTable.unhideColumn(SearchColumnName.EPISODE);
		}
		if (settingsControl.getSettings().getScreenSettings().isHideFilename()) {
			videoTable.hideColumn(SearchColumnName.FILENAME);
		} else {
			chckbxmntmBestandsnaam.setSelected(true);
			videoTable.unhideColumn(SearchColumnName.FILENAME);
		}
		if (settingsControl.getSettings().getScreenSettings().isHideSeason()) {
			videoTable.hideColumn(SearchColumnName.SEASON);
		} else {
			chckbxmntmSeason.setSelected(true);
			videoTable.unhideColumn(SearchColumnName.SEASON);
		}
		if (settingsControl.getSettings().getScreenSettings().isHideType()) {
			videoTable.hideColumn(SearchColumnName.TYPE);
		} else {
			chckbxmntmType.setSelected(true);
			videoTable.unhideColumn(SearchColumnName.TYPE);
		}
		if (settingsControl.getSettings().getScreenSettings().isHideTitle()) {
			videoTable.hideColumn(SearchColumnName.TITLE);
		} else {
			chckbxmntmTitle.setSelected(true);
			videoTable.unhideColumn(SearchColumnName.TITLE);
		}
	}

	private void initPopupMenu() {
		popupMenu = new MyPopupMenu();
		JMenuItem menuItem = new JMenuItem("Kopiëren");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final VideoTable t = (VideoTable) popupMenu.getInvoker();
				final DefaultTableModel model = (DefaultTableModel) t
						.getModel();

				int col = t.columnAtPoint(popupMenu.getClickLocation());
				int row = t.rowAtPoint(popupMenu.getClickLocation());

				try {
					StringSelection selection = new StringSelection(
							(String) model.getValueAt(row, col));
					Toolkit.getDefaultToolkit().getSystemClipboard()
							.setContents(selection, null);
				} catch (HeadlessException e) {
					Logger.instance.error("initPopupMenu : "
							+ Logger.stack2String(e));
				}
			}
		});
		popupMenu.add(menuItem);
		// add the listener to the jtable
		MouseListener popupListener = new PopupListener(popupMenu);
		// add the listener specifically to the header
		videoTable.addMouseListener(popupListener);
		videoTable.getTableHeader().addMouseListener(popupListener);
		subtitleTable.addMouseListener(popupListener);
		subtitleTable.getTableHeader().addMouseListener(popupListener);
	}

	protected void showTranslateShowNames() {
		final MappingEpisodeNameDialog tDialog = new MappingEpisodeNameDialog(
				this, settingsControl);
		tDialog.setVisible(true);
	}

	private void showAbout() {
		JOptionPane.showConfirmDialog(this, "Sub download tool",
				"MultiSubDownloader", JOptionPane.CLOSED_OPTION);
	}

	protected void rename() {
		RenameWorker renameWorker = new RenameWorker(videoTable,
				settingsControl.getSettings());
		renameWorker.addPropertyChangeListener(this);
		pnlSearchFile.setEnableDownloadButtons(false);
		progressDialog = new ProgressDialog(this, renameWorker);
		progressDialog.setVisible(true);
		renameWorker.execute();
	}

	private void download() {
		Logger.instance.trace(MainWindow.class.toString(), "download", "");
		DownloadWorker downloadWorker = new DownloadWorker(videoTable,
				settingsControl.getSettings());
		downloadWorker.addPropertyChangeListener(this);
		pnlSearchFile.setEnableDownloadButtons(false);
		progressDialog = new ProgressDialog(this, downloadWorker);
		progressDialog.setVisible(true);
		downloadWorker.execute();
	}

	private void downloadText() {
		final VideoTableModel model = (VideoTableModel) subtitleTable
				.getModel();
		File path = MemoryFolderChooser.getInstance().selectDirectory(
				getContentPane(), "Selecteer map");

		for (int i = 0; i < model.getRowCount(); i++) {
			if ((Boolean) model.getValueAt(i,
					subtitleTable.getColumnIdByName(SearchColumnName.SELECT))) {
				final Subtitle subtitle = (Subtitle) model.getValueAt(i,
						subtitleTable
								.getColumnIdByName(SearchColumnName.OBJECT));
				String filename = "";
				if (!subtitle.getFilename().endsWith(".srt"))
					filename = subtitle.getFilename() + ".srt";
				if (OsCheck.getOperatingSystemType() == OSType.Windows)
					filename = StringUtils.removeIllegalWindowsChars(filename);
				
				try {
					if (subtitle.getSubtitleSource() == SubtitleSource.PRIVATEREPO) {
						DropBoxClient.getDropBoxClient().doDownloadFile(
								subtitle.getDownloadlink(),
								new File(path, subtitle.getFilename()));
					} else {
						if (HttpClient.isUrl(subtitle.getDownloadlink())) {
							HttpClient.getHttpClient().doDownloadFile(
									new URL(subtitle.getDownloadlink()),
									new File(path, filename));
						} else {
							Files.copy(new File(subtitle.getDownloadlink()),
									new File(path, subtitle.getFilename()));
						}
					}
				} catch (MalformedURLException e) {
					Logger.instance.error("downloadText : "
							+ Logger.stack2String(e));
				} catch (IOException e) {
					Logger.instance.error("downloadText : "
							+ Logger.stack2String(e));
				}
			}
		}
	}

	private void searchFile() {
		if (inputFileCheck()) {
			SearchFileWorker searchWorker = new SearchFileWorker(videoTable,
					settingsControl.getSettings());
			searchWorker.addPropertyChangeListener(this);
			progressDialog = new ProgressDialog(this, searchWorker);
			progressDialog.setVisible(true);
			btnSearch.setEnabled(false);
			StatusMessenger.instance.message("Zoeken...");
			clearTableFile();
			if (txtIncomingPath.getText().equals("")) {
				if (settingsControl.getSettings().getDefaultIncomingFolders()
						.size() > 0) {
					searchWorker.setParameters(settingsControl.getSettings()
							.getDefaultIncomingFolders(),
							getLanguageCodeFile(), chkRecursive.isSelected(),
							chkforceSubtitleOverwrite.isSelected());
					searchWorker.execute();
				}
			} else {
				searchWorker.setParameters(new File(txtIncomingPath.getText()),
						getLanguageCodeFile(), chkRecursive.isSelected(),
						chkforceSubtitleOverwrite.isSelected());
				searchWorker.execute();
			}
		}

	}

	private boolean inputFileCheck() {
		if (txtIncomingPath.getText().equals("")) {
			if (settingsControl.getSettings().getDefaultIncomingFolders()
					.size() == 0) {
				showErrorMessage("Geen map geselecteerd");
				return false;
			}
		}
		return true;
	}

	private void searchName() {
		if (inputNameCheck()) {
			SearchNameWorker searchWorker = new SearchNameWorker(subtitleTable,
					settingsControl.getSettings());
			searchWorker.addPropertyChangeListener(this);
			progressDialog = new ProgressDialog(searchWorker);
			progressDialog.setVisible(true);
			clearTableName();
			btnSearchText.setEnabled(false);
			final VideoSearchType videoTypeChoice = (VideoSearchType) this.cbxVideoType
					.getSelectedItem();

			int season = 0, episode = 0;
			if (!txtInputSeason.getText().isEmpty())
				season = Integer.parseInt(this.txtInputSeason.getText().trim());
			if (!txtInputEpisode.getText().isEmpty())
				episode = Integer.parseInt(this.txtInputEpisode.getText()
						.trim());

			searchWorker.setParameters(videoTypeChoice, txtInputVideoName
					.getText().trim(), season, episode, getLanguageCodeText(),
					txtQualityVersion.getText().trim());
			searchWorker.execute();
		}
	}

	private boolean inputNameCheck() {
		VideoSearchType videoTypeChoice = (VideoSearchType) this.cbxVideoType
				.getSelectedItem();
		if (txtInputVideoName.getText().isEmpty()) {
			showErrorMessage("Geen Movie/Episode/Release opgegeven");
			return false;
		}
		if (videoTypeChoice.equals(VideoSearchType.EPISODE)) {
			if (!this.txtInputSeason.getText().isEmpty()
					&& !isInteger(this.txtInputSeason.getText().trim())) {
				showErrorMessage("Seizoen is niet numeriek");
				return false;
			}
			if ((!this.txtInputEpisode.getText().isEmpty())
					&& !isInteger(this.txtInputEpisode.getText().trim())) {
				showErrorMessage("Aflevering is niet numeriek");
				return false;
			}
		}
		return true;
	}

	private boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private String getLanguageCodeFile() {
		if (cbxLanguageFile.getSelectedItem().equals("Nederlands")) {
			return "nl";
		} else if (cbxLanguageFile.getSelectedItem().equals("Engels")) {
			return "en";
		}
		return null;
	}

	private String getLanguageCodeText() {
		if (cbxLanguageText.getSelectedItem().equals("Nederlands")) {
			return "nl";
		} else if (cbxLanguageText.getSelectedItem().equals("Engels")) {
			return "en";
		}
		return null;
	}

	private void clearTableFile() {
		final VideoTableModel model = (VideoTableModel) videoTable.getModel();
		model.clearTable();
		pnlSearchFile.setEnableDownloadButtons(false);
	}

	private void clearTableName() {
		final VideoTableModel model = (VideoTableModel) subtitleTable
				.getModel();
		model.clearTable();
		pnlSearchText.setEnableDownloadButtons(false);
	}

	protected JFrame getThis() {
		return this;
	}

	private void showErrorMessage(String message) {
		JOptionPane.showConfirmDialog(this, message, "MultiSubDownloader",
				JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
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
		File path = MemoryFolderChooser.getInstance().selectDirectory(
				getThis(), "Selecteer map");
		txtIncomingPath.setText(path.getAbsolutePath());
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() instanceof SearchFileWorker) {
			final SearchFileWorker searchWorker = (SearchFileWorker) event
					.getSource();
			if (searchWorker.isDone()) {
				final DefaultTableModel model = (DefaultTableModel) videoTable
						.getModel();
				if (model.getRowCount() > 0) {
					pnlSearchFile.setEnableDownloadButtons(true);
				}
				StatusMessenger.instance.message("Found " + model.getRowCount()
						+ " files");
				progressDialog.setVisible(false);
				btnSearch.setEnabled(true);
			} else {
				final int progress = searchWorker.getProgress();
				progressDialog.updateProgress(progress);
				if (progress == 0) {
					StatusMessenger.instance
							.message("Bestanden lijst aan het opbouwen");
				} else {
					StatusMessenger.instance
							.message("Bestanden aan het verwerken");
				}
			}
		} else if (event.getSource() instanceof DownloadWorker) {
			final DownloadWorker downloadWorker = (DownloadWorker) event
					.getSource();
			if (downloadWorker.isDone()) {
				pnlSearchFile.setEnableDownloadButtons(true);
				progressDialog.setVisible(false);
			} else {
				final int progress = downloadWorker.getProgress();
				progressDialog.updateProgress(progress);
				StatusMessenger.instance.message("Downloaden ....");
			}
		} else if (event.getSource() instanceof RenameWorker) {
			final RenameWorker renameWorker = (RenameWorker) event.getSource();
			if (renameWorker.isDone()) {
				pnlSearchFile.setEnableDownloadButtons(true);
				progressDialog.setVisible(false);
			} else {
				final int progress = renameWorker.getProgress();
				progressDialog.updateProgress(progress);
				StatusMessenger.instance.message("Hernoemen ....");
			}
		} else if (event.getSource() instanceof SearchNameWorker) {
			final SearchNameWorker searchWorker = (SearchNameWorker) event
					.getSource();
			if (searchWorker.isDone()) {
				progressDialog.setVisible(false);
				final DefaultTableModel model = (DefaultTableModel) subtitleTable
						.getModel();
				StatusMessenger.instance.message("Found " + model.getRowCount()
						+ " files");
				pnlSearchText.setEnableDownloadButtons(true);
				btnSearchText.setEnabled(true);
			} else {
				final int progress = searchWorker.getProgress();
				progressDialog.updateProgress(progress);
				StatusMessenger.instance.message("Zoeken ....");
			}
		}
	}

	private void close() {
		Logger.instance.log("MainWindow, close()", Level.TRACE);
		settingsControl.getSettings().setOptionRecursive(
				chkRecursive.isSelected());
		storeScreenSettings();
		settingsControl.store();
	}

	private void storeScreenSettings() {
		settingsControl
				.getSettings()
				.getScreenSettings()
				.setHideEpisode(
						videoTable.isHideColumn(SearchColumnName.EPISODE));
		settingsControl
				.getSettings()
				.getScreenSettings()
				.setHideFilename(
						videoTable.isHideColumn(SearchColumnName.FILENAME));
		settingsControl
				.getSettings()
				.getScreenSettings()
				.setHideSeason(videoTable.isHideColumn(SearchColumnName.SEASON));
		settingsControl.getSettings().getScreenSettings()
				.setHideTitle(videoTable.isHideColumn(SearchColumnName.TITLE));
		settingsControl.getSettings().getScreenSettings()
				.setHideType(videoTable.isHideColumn(SearchColumnName.TYPE));

	}

}
