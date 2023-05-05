package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.framework.event.Emitter;
import org.lodder.subtools.multisubdownloader.framework.event.Event;
import org.lodder.subtools.multisubdownloader.gui.ToStringListCellRenderer;
import org.lodder.subtools.multisubdownloader.gui.extra.JListWithImages;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.PanelCheckBox;
import org.lodder.subtools.multisubdownloader.gui.panels.DefaultSelectionPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.EpisodeLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.MovieLibraryPanel;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeItem;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateCheckPeriod;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateType;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.lodder.subtools.sublibrary.util.JComponentExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod(JComponentExtension.class)
public class PreferenceDialog extends MultiSubDialog {

    private static final long serialVersionUID = -5730220264781738564L;
    private final GUI gui;
    private final JPanel contentPanel = new JPanel();
    private final Emitter eventEmitter;
    private JCheckBox chkOnlyFound, chkAlwaysConfirm, chkSubtitleExactMethod, chkSubtitleKeywordMethod;
    private final SettingsControl settingsCtrl;
    private EpisodeLibraryPanel pnlEpisodeLibrary;
    private JListWithImages excludeList;
    private JCheckBox chkStopOnSearchError;
    private MovieLibraryPanel pnlMovieLibrary;
    private JTextField txtProxyHost, txtAddic7edUsername, txtOpenSubtitlesUsername;
    private JTextField txtProxyPort, txtAddic7edPassword, txtOpenSubtitlesPassword;
    private JCheckBox chkUseProxy, chkUserAddic7edLogin, chkUserOpenSubtitlesLogin, chkExcludeHearingImpaired;
    private JListWithImages defaultIncomingFoldersList, localSourcesFoldersList;
    private JCheckBox chkSourceAddic7ed, chkSourceTvSubtitles, chkSourcePodnapisi,
            chkSourceOpenSubtitles, chkSourceLocal, chkSourceSubscene, chkSourceAddic7edProxy;
    private JComboBox<SettingsProcessEpisodeSource> cbxEpisodeProcessSource;
    private JCheckBox chkMinScoreSelection;
    private JCheckBox chkConfirmProviderMapping;
    private JSlider sldMinScoreSelection;
    private final Manager manager;
    private JComboBox<UpdateCheckPeriod> cbxUpdateCheckPeriod;
    private JComboBox<UpdateType> cbxUpdateType;
    private JComboBox<Language> cbxLanguage;
    private JCheckBox chkDefaultSelection;
    private DefaultSelectionPanel pnlDefaultSelection;
    private JButton btnBrowseLocalSources, btnRemoveLocalSources;
    private JScrollPane scrlPlocalSources;
    private final UserInteractionHandler userInteractionHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceDialog.class);

    /**
     * Create the dialog.
     */
    public PreferenceDialog(GUI gui, final SettingsControl settingsCtrl, Emitter eventEmitter,
            Manager manager, UserInteractionHandler userInteractionHandler) {
        super(gui, Messages.getString("PreferenceDialog.Title"), true);
        this.gui = gui;
        this.settingsCtrl = settingsCtrl;
        this.eventEmitter = eventEmitter;
        this.manager = manager;
        this.userInteractionHandler = userInteractionHandler;
        initialize();
        setPreferenceSettings();
        repaint();
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
            JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
            tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            contentPanel.add(tabbedPane);
            {
                JPanel pnlGeneral = new JPanel();
                tabbedPane.addTab(Messages.getString("PreferenceDialog.TabGeneral"), null, pnlGeneral, null);
                pnlGeneral.setLayout(new MigLayout("", "[][grow, nogrid]"));
                {
                    JLabel lblLanguage = new JLabel(Messages.getString("PreferenceDialog.Language"));
                    pnlGeneral.add(lblLanguage, "width 2");

                    cbxLanguage = new JComboBox<>();
                    cbxLanguage.setModel(new DefaultComboBoxModel<>(Messages.getAvailableLanguages().toArray(Language[]::new)));
                    cbxLanguage.setRenderer(new ToStringListCellRenderer<>(cbxLanguage.getRenderer(),
                            o -> Messages.getString(((Language) o).getMsgCode())));
                    pnlGeneral.add(cbxLanguage, "span");
                }

                {
                    JLabel lblDefaultIncomingFolder = new JLabel(Messages.getString("PreferenceDialog.DefaultIncomingFolder"));
                    pnlGeneral.add(lblDefaultIncomingFolder, "alignx left,aligny center, span 1 2");

                    JScrollPane scrollPane = new JScrollPane();
                    pnlGeneral.add(scrollPane, "grow, span, wrap");
                    defaultIncomingFoldersList = new JListWithImages();
                    scrollPane.setViewportView(defaultIncomingFoldersList);

                    JButton btnAddFolder = new JButton(Messages.getString("PreferenceDialog.AddFolder"));
                    btnAddFolder.addActionListener(arg -> {
                        MemoryFolderChooser.getInstance().selectDirectory(getContentPane(), Messages.getString("PreferenceDialog.SelectFolder"))
                                .map(Path::toAbsolutePath).ifPresent(path -> {
                                    if (defaultIncomingFoldersList.getModel().getSize() == 0) {
                                        defaultIncomingFoldersList.addItem(SettingsExcludeType.FOLDER, path);
                                    } else {
                                        boolean exists = stream(defaultIncomingFoldersList.getModel())
                                                .map(panel -> ((JLabel) panel.getComponent(0)).getText())
                                                .filter(Objects::nonNull).anyMatch(path::equals);
                                        if (!exists) {
                                            defaultIncomingFoldersList.addItem(SettingsExcludeType.FOLDER, path);
                                        }
                                    }
                                });
                    });
                    pnlGeneral.add(btnAddFolder, "split");

                    JButton buttonDeleteFolder = new JButton(Messages.getString("PreferenceDialog.DeleteFolder"));
                    buttonDeleteFolder.addActionListener(arg0 -> {
                        DefaultListModel<JPanel> model = (DefaultListModel<JPanel>) defaultIncomingFoldersList.getModel();
                        int selected = defaultIncomingFoldersList.getSelectedIndex();
                        if (model.size() > 0 && selected >= 0) {
                            model.removeElementAt(selected);
                        }
                    });
                    pnlGeneral.add(buttonDeleteFolder, "wrap, gapbottom 10px");
                }
                {
                    JLabel lblExcludeList = new JLabel(Messages.getString("PreferenceDialog.ExcludeList"));
                    pnlGeneral.add(lblExcludeList, "alignx left,aligny center, span 1 2");

                    JScrollPane scrollPane = new JScrollPane();
                    pnlGeneral.add(scrollPane, "grow, span, wrap");
                    excludeList = new JListWithImages();
                    scrollPane.setViewportView(excludeList);

                    JButton btnAddExcludeFolder = new JButton(Messages.getString("PreferenceDialog.AddFolder"));
                    btnAddExcludeFolder.addActionListener(arg0 -> addExcludeItem(SettingsExcludeType.FOLDER));
                    pnlGeneral.add(btnAddExcludeFolder, "split");

                    JButton btnDeleteExcludeFoledr = new JButton(Messages.getString("PreferenceDialog.DeleteFolder"));
                    btnDeleteExcludeFoledr.addActionListener(arg0 -> removeExcludeItem());
                    pnlGeneral.add(btnDeleteExcludeFoledr);

                    JButton btnAddExcludeRegex = new JButton(Messages.getString("PreferenceDialog.RegexToevoegen"));
                    btnAddExcludeRegex.addActionListener(arg0 -> addExcludeItem(SettingsExcludeType.REGEX));
                    pnlGeneral.add(btnAddExcludeRegex, "wrap, gapbottom 10px");

                }

                {
                    pnlGeneral
                            .addComponent(new JLabel(Messages.getString("PreferenceDialog.Update")), "span 2, split")
                            .addComponent(new JSeparator(), "wrap, grow, gapy 5")
                            .addComponent(new JLabel(Messages.getString("PreferenceDialog.NewUpdateCheck")), "gapx 5")
                            .addComponent(cbxUpdateCheckPeriod = new JComboBox<>(), "wrap")
                            .addComponent(new JLabel(Messages.getString("PreferenceDialog.UpdateType")), "gapx 5")
                            .addComponent(cbxUpdateType = new JComboBox<>(), "wrap");

                    cbxUpdateCheckPeriod.setModel(new DefaultComboBoxModel<>(UpdateCheckPeriod.values()));
                    cbxUpdateCheckPeriod.setRenderer(new ToStringListCellRenderer<>(cbxUpdateCheckPeriod.getRenderer(),
                            o -> Messages.getString(((UpdateCheckPeriod) o).getLangCode())));

                    cbxUpdateType.setModel(new DefaultComboBoxModel<>(UpdateType.values()));
                    cbxUpdateType.setRenderer(new ToStringListCellRenderer<>(cbxUpdateType.getRenderer(),
                            o -> Messages.getString(((UpdateType) o).getMsgCode())));
                }

                {
                    pnlGeneral.add(new JLabel(Messages.getString("PreferenceDialog.ConfigureProxy")), "span 2, split");
                    pnlGeneral.add(new JSeparator(), "wrap, grow, gapy 5");

                    chkUseProxy = new JCheckBox(Messages.getString("PreferenceDialog.UseProxyServer"));
                    pnlGeneral.add(chkUseProxy, "alignx left,aligny center, span 1 2");
                    chkUseProxy.addCheckedChangeListener(checked -> {
                        txtProxyHost.setEnabled(checked);
                        txtProxyPort.setEnabled(checked);
                    });

                    JLabel lblProxyHost = new JLabel(Messages.getString("PreferenceDialog.Hostname"));
                    pnlGeneral.add(lblProxyHost, "split");

                    txtProxyHost = new JTextField();
                    pnlGeneral.add(txtProxyHost, "wrap");
                    txtProxyHost.setColumns(10);
                    txtProxyHost.setEnabled(false);

                    JLabel lblProxyPoort = new JLabel(Messages.getString("PreferenceDialog.Port"));
                    pnlGeneral.add(lblProxyPoort, "split");

                    txtProxyPort = new JTextField();
                    pnlGeneral.add(txtProxyPort, "wrap");
                    txtProxyPort.setColumns(10);
                    txtProxyPort.setEnabled(false);
                }
            }

            {
                pnlEpisodeLibrary =
                        new EpisodeLibraryPanel(settingsCtrl.getSettings().getEpisodeLibrarySettings(), manager, false, userInteractionHandler);
                tabbedPane.addTab(Messages.getString("PreferenceDialog.SerieLibrary"), null, pnlEpisodeLibrary, null);
            }
            {
                pnlMovieLibrary =
                        new MovieLibraryPanel(settingsCtrl.getSettings().getMovieLibrarySettings(), manager, false, userInteractionHandler);
                tabbedPane.addTab(Messages.getString("PreferenceDialog.MovieLibrary"), null, pnlMovieLibrary, null);
            }
            {
                JPanel pnlOptions = new JPanel();
                tabbedPane.addTab(Messages.getString("PreferenceDialog.Options"), null, pnlOptions, null);
                pnlOptions.setLayout(new MigLayout("", "[][433px,grow][433px,grow][100px,grow][]",
                        "[][][][][grow][][25px][][][][23px][][23px][][]"));
                pnlOptions.add(new JLabel(Messages.getString("PreferenceDialog.DownloadOptions")), "cell 0 0 5 1");
                pnlOptions.add(new JSeparator(), "cell 0 0 5 1,growx");
                chkAlwaysConfirm = new JCheckBox(Messages.getString("PreferenceDialog.CheckBeforeDownloading"));
                pnlOptions.add(chkAlwaysConfirm, "cell 1 1 3 1,grow");
                {
                    chkMinScoreSelection = new JCheckBox(Messages.getString("PreferenceDialog.MinAutomaticScoreSelection"));
                    pnlOptions.add(chkMinScoreSelection, "cell 1 2");
                }
                {
                    sldMinScoreSelection = new JSlider();
                    sldMinScoreSelection.setMinimum(0);
                    sldMinScoreSelection.setMaximum(100);
                    pnlOptions.add(sldMinScoreSelection, "cell 2 2");
                }
                {
                    chkDefaultSelection = new JCheckBox(Messages.getString("PreferenceDialog.DefaultSelection"));
                    chkDefaultSelection.addActionListener(arg0 -> pnlDefaultSelection.setEnabled(chkDefaultSelection.isSelected()));
                    pnlOptions.add(chkDefaultSelection, "cell 1 3");
                }
                {
                    pnlDefaultSelection = new DefaultSelectionPanel();
                    pnlDefaultSelection.setEnabled(false);
                    pnlOptions.add(pnlDefaultSelection, "cell 1 4 2 1,grow");
                }
                pnlOptions.add(new JLabel(Messages.getString("PreferenceDialog.SearchFilter")), "cell 0 5 5 1");
                pnlOptions.add(new JSeparator(), "cell 0 5 5 1,growx");
                chkSubtitleExactMethod = new JCheckBox(Messages.getString("PreferenceDialog.SearchFilterExact"));
                pnlOptions.add(chkSubtitleExactMethod, "cell 1 6 3 1,grow");
                {
                    chkSubtitleKeywordMethod = new JCheckBox(Messages.getString("PreferenceDialog.SearchFilterKeyword"));
                    pnlOptions.add(chkSubtitleKeywordMethod, "cell 1 7 3 1");
                }
                {
                    chkExcludeHearingImpaired = new JCheckBox(Messages.getString("PreferenceDialog.ExcludeHearingImpaired"));
                    pnlOptions.add(chkExcludeHearingImpaired, "cell 1 8 3 1");
                }
                pnlOptions.add(new JLabel(Messages.getString("PreferenceDialog.TableOptions")), "cell 0 9 5 1");
                pnlOptions.add(new JSeparator(), "cell 0 9 5 1,growx");
                chkOnlyFound = new JCheckBox(Messages.getString("PreferenceDialog.ShowOnlyFound"));
                pnlOptions.add(chkOnlyFound, "cell 1 10 3 1,growx,aligny center");
                pnlOptions.add(new JLabel(Messages.getString("PreferenceDialog.ErrorHandlingOption")), "cell 0 11 5 1");
                pnlOptions.add(new JSeparator(), "cell 0 11 5 1,growx");
                chkStopOnSearchError = new JCheckBox(Messages.getString("PreferenceDialog.StopAfterError"));
                pnlOptions.add(chkStopOnSearchError, "cell 1 12 3 1,alignx left,aligny center");
                {
                    JLabel label = new JLabel(Messages.getString("PreferenceDialog.SerieDatabaseSource"));
                    pnlOptions.add(label, "cell 0 13 5 1");
                    pnlOptions.add(new JSeparator(), "cell 0 13 5 1,growx");
                }
                {
                    cbxEpisodeProcessSource = new JComboBox<>();
                    cbxEpisodeProcessSource.setModel(new DefaultComboBoxModel<>(SettingsProcessEpisodeSource.values()));
                    cbxEpisodeProcessSource.setEnabled(false);
                    pnlOptions.add(cbxEpisodeProcessSource, "cell 1 14,growx");
                }
                {
                    chkConfirmProviderMapping = new JCheckBox(Messages.getString("PreferenceDialog.ConfirmProviderMapping"));
                    pnlOptions.add(chkConfirmProviderMapping, "cell 1 15,growx");
                }
            }

            // SERIE PROVIDERS TAB
            {
                JPanel pnlSerieSources = new JPanel(new MigLayout("", "[grow, nogrid]"));
                tabbedPane.addTab(Messages.getString("PreferenceDialog.SerieSources"), null, pnlSerieSources, null);

                pnlSerieSources.add(new JLabel(Messages.getString("PreferenceDialog.SelectPreferedSources")), "split");
                pnlSerieSources.add(new JSeparator(), "wrap, growx, gapy 5");

                // ADDIC7ED
                chkSourceAddic7ed = new JCheckBox("Addic7ed");
                chkUserAddic7edLogin = new JCheckBox(Messages.getString("PreferenceDialog.UseAddic7edLogin"));
                chkSourceAddic7edProxy = new JCheckBox(Messages.getString("PreferenceDialog.Proxy"));
                pnlSerieSources
                        .addComponent("wrap", new PanelCheckBox(chkSourceAddic7ed)
                                .addComponent("wrap", chkSourceAddic7edProxy)
                                .addComponent("wrap", new PanelCheckBox(chkUserAddic7edLogin)
                                        .addComponent("alignx trailing", new JLabel(Messages.getString("PreferenceDialog.Username")))
                                        .addComponent("wrap", txtAddic7edUsername = new JTextField().columns(10))
                                        .addComponent("alignx trailing", new JLabel(Messages.getString("PreferenceDialog.Password")))
                                        .addComponent("wrap", txtAddic7edPassword = new JPasswordField(10).columns(10))));

                // TV SUBTITLES
                pnlSerieSources.add(chkSourceTvSubtitles = new JCheckBox("Tv Subtitles"), "wrap");

                // PODNAPISI
                pnlSerieSources.add(chkSourcePodnapisi = new JCheckBox("Podnapisi"), "wrap");

                // OPENSUBTITLES
                chkSourceOpenSubtitles = new JCheckBox("OpenSubtitles");
                chkUserOpenSubtitlesLogin = new JCheckBox(Messages.getString("PreferenceDialog.UseOpenSubtitlesLogin"));
                pnlSerieSources
                        .addComponent("wrap", new PanelCheckBox(chkSourceOpenSubtitles)
                                .addComponent("wrap", new PanelCheckBox(chkUserOpenSubtitlesLogin)
                                        .addComponent("alignx trailing", new JLabel(Messages.getString("PreferenceDialog.Username")))
                                        .addComponent("wrap", txtOpenSubtitlesUsername = new JTextField().columns(10))
                                        .addComponent("alignx trailing", new JLabel(Messages.getString("PreferenceDialog.Password")))
                                        .addComponent("wrap", txtOpenSubtitlesPassword = new JPasswordField(10).columns(10))));

                // SUBSCENE
                pnlSerieSources.add(chkSourceSubscene = new JCheckBox("Subscene"), "wrap");

                // LOCAL
                chkSourceLocal = new JCheckBox(Messages.getString("PreferenceDialog.Local"));
                btnBrowseLocalSources = new JButton(Messages.getString("PreferenceDialog.AddFolder"));
                btnRemoveLocalSources = new JButton(Messages.getString("PreferenceDialog.DeleteFolder"));
                scrlPlocalSources = new JScrollPane().scrollPane(localSourcesFoldersList = new JListWithImages());

                pnlSerieSources
                        .addComponent("wrap", new PanelCheckBox(chkSourceLocal)
                                .addComponent("aligny top, gapy 5px", new JLabel(Messages.getString("PreferenceDialog.LocalFolderWithSubtitles")))
                                .addComponent("wrap", new JPanel(new MigLayout("insets 0", "[grow, nogrid]"))
                                        .addComponent("split", btnBrowseLocalSources)
                                        .addComponent("wrap", btnRemoveLocalSources)
                                        .addComponent("wrap", scrlPlocalSources)));

                btnBrowseLocalSources.addActionListener(arg0 -> {
                    MemoryFolderChooser.getInstance().selectDirectory(getContentPane(), Messages.getString("PreferenceDialog.SelectFolder"))
                            .map(Path::toAbsolutePath).ifPresent(path -> {
                                if (localSourcesFoldersList.getModel().getSize() == 0) {
                                    localSourcesFoldersList.addItem(SettingsExcludeType.FOLDER, path);
                                } else {
                                    boolean exists = false;
                                    for (int i = 0; i < localSourcesFoldersList.getModel().getSize(); i++) {
                                        if (localSourcesFoldersList.getDescription(i) != null
                                                && localSourcesFoldersList.getDescription(i).equals(path)) {
                                            exists = true;
                                        }
                                    }
                                    if (!exists) {
                                        localSourcesFoldersList.addItem(SettingsExcludeType.FOLDER, path);
                                    }
                                }
                            });
                });

                btnRemoveLocalSources.addActionListener(arg0 -> {
                    DefaultListModel<JPanel> model = (DefaultListModel<JPanel>) localSourcesFoldersList.getModel();
                    int selected = localSourcesFoldersList.getSelectedIndex();
                    if (model.size() > 0 && selected >= 0) {
                        model.removeElementAt(selected);
                    }
                });
            }
        }

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton(Messages.getString("App.OK"));
                okButton.addActionListener(arg0 -> testAndSaveValues());
                okButton.setActionCommand(Messages.getString("App.OK"));
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton(Messages.getString("App.Cancel"));
                cancelButton.addActionListener(arg0 -> setVisible(false));
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    private void setPreferenceSettings() {
        for (SettingsExcludeItem element : settingsCtrl.getSettings().getExcludeList()) {
            excludeList.addItem(element.getType(), element.getDescription());
        }
        for (Path element : settingsCtrl.getSettings().getDefaultIncomingFolders()) {
            defaultIncomingFoldersList.addItem(SettingsExcludeType.FOLDER, element.toAbsolutePath());
        }
        for (Path element : settingsCtrl.getSettings().getLocalSourcesFolders()) {
            localSourcesFoldersList.addItem(SettingsExcludeType.FOLDER, element.toAbsolutePath());
        }
        cbxLanguage.setSelectedItem(settingsCtrl.getSettings().getLanguage());
        chkUseProxy.setSelected(settingsCtrl.getSettings().isGeneralProxyEnabled());
        txtProxyHost.setText(settingsCtrl.getSettings().getGeneralProxyHost());
        txtProxyPort.setText(String.valueOf(settingsCtrl.getSettings().getGeneralProxyPort()));
        chkAlwaysConfirm.setSelected(settingsCtrl.getSettings().isOptionsAlwaysConfirm());
        chkMinScoreSelection.setSelected(settingsCtrl.getSettings().isOptionsMinAutomaticSelection());
        sldMinScoreSelection.setValue(settingsCtrl.getSettings().getOptionsMinAutomaticSelectionValue());
        chkSubtitleExactMethod.setSelected(settingsCtrl.getSettings().isOptionSubtitleExactMatch());
        chkSubtitleKeywordMethod.setSelected(settingsCtrl.getSettings().isOptionSubtitleKeywordMatch());
        chkExcludeHearingImpaired.setSelected(settingsCtrl.getSettings().isOptionSubtitleExcludeHearingImpaired());
        chkOnlyFound.setSelected(settingsCtrl.getSettings().isOptionsShowOnlyFound());
        chkStopOnSearchError.setSelected(settingsCtrl.getSettings().isOptionsStopOnSearchError());
        cbxEpisodeProcessSource.setSelectedItem(settingsCtrl.getSettings().getProcessEpisodeSource());
        pnlEpisodeLibrary.setLibrarySettings(settingsCtrl.getSettings().getEpisodeLibrarySettings());
        pnlMovieLibrary.setLibrarySettings(settingsCtrl.getSettings().getMovieLibrarySettings());
        chkUserAddic7edLogin.setSelected(settingsCtrl.getSettings().isLoginAddic7edEnabled());
        txtAddic7edUsername.setText(settingsCtrl.getSettings().getLoginAddic7edUsername());
        txtAddic7edPassword.setText(settingsCtrl.getSettings().getLoginAddic7edPassword());
        chkUserOpenSubtitlesLogin.setSelected(settingsCtrl.getSettings().isLoginOpenSubtitlesEnabled());
        txtOpenSubtitlesUsername.setText(settingsCtrl.getSettings().getLoginOpenSubtitlesUsername());
        txtOpenSubtitlesPassword.setText(settingsCtrl.getSettings().getLoginOpenSubtitlesPassword());
        chkSourceAddic7ed.setSelected(settingsCtrl.getSettings().isSerieSourceAddic7ed());
        chkSourceAddic7edProxy.setSelected(settingsCtrl.getSettings().isSerieSourceAddic7edProxy());
        chkSourceAddic7edProxy.setEnabled(settingsCtrl.getSettings().isSerieSourceAddic7ed());
        chkSourceTvSubtitles.setSelected(settingsCtrl.getSettings().isSerieSourceTvSubtitles());
        chkSourcePodnapisi.setSelected(settingsCtrl.getSettings().isSerieSourcePodnapisi());
        chkSourceOpenSubtitles.setSelected(settingsCtrl.getSettings().isSerieSourceOpensubtitles());
        chkSourceLocal.setSelected(settingsCtrl.getSettings().isSerieSourceLocal());
        chkSourceSubscene.setSelected(settingsCtrl.getSettings().isSerieSourceSubscene());
        cbxUpdateCheckPeriod.setSelectedItem(settingsCtrl.getSettings().getUpdateCheckPeriod());
        cbxUpdateType.setSelectedItem(settingsCtrl.getSettings().getUpdateType());
        chkDefaultSelection.setSelected(settingsCtrl.getSettings().isOptionsDefaultSelection());
        pnlDefaultSelection.setDefaultSelectionList(settingsCtrl.getSettings().getOptionsDefaultSelectionQualityList());
        chkConfirmProviderMapping.setSelected(settingsCtrl.getSettings().isOptionsConfirmProviderMapping());
    }

    protected boolean testOptionsTab() {
        return true;
    }

    protected boolean testGeneralTab() {
        try {
            Integer.parseInt(txtProxyPort.getText());
            return true;
        } catch (NumberFormatException e) {
            String message = Messages.getString("PreferenceDialog.ProxyPortNumericRequired");
            JOptionPane.showConfirmDialog(this, message, Messages.getString("PreferenceDialog.Name"),
                    JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
            LOGGER.debug(Messages.getString("PreferenceDialog.ProxyPortNumericRequired"));
            return false;
        }
    }

    protected boolean testSerieSourcesTab() {
        if (chkUserAddic7edLogin.isSelected() && (txtAddic7edUsername.getText().isEmpty() || txtAddic7edPassword.getText().isEmpty())) {
            String message = Messages.getString("PreferenceDialog.LoginSelectEnterUsernamePassword").formatted(SubtitleSource.ADDIC7ED.getName());
            JOptionPane.showConfirmDialog(this, message, Messages.getString("PreferenceDialog.Name"),
                    JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
            LOGGER.debug(message);
            return false;
        }
        if (chkUserOpenSubtitlesLogin.isSelected()
                && (txtOpenSubtitlesUsername.getText().isEmpty() || txtOpenSubtitlesPassword.getText().isEmpty())) {
            String message =
                    Messages.getString("PreferenceDialog.LoginSelectEnterUsernamePassword").formatted(SubtitleSource.OPENSUBTITLES.getName());
            JOptionPane.showConfirmDialog(this, message, Messages.getString("PreferenceDialog.Name"),
                    JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
            LOGGER.debug(message);
            return false;
        }
        return true;
    }

    private void addExcludeItem(SettingsExcludeType seType) {
        if (seType == SettingsExcludeType.FOLDER) {
            MemoryFolderChooser.getInstance().selectDirectory(getContentPane(), Messages.getString("PreferenceDialog.SelectExcludeFolder"))
                    .map(Path::toAbsolutePath).ifPresent(path -> excludeList.addItem(seType, path));
        } else if (seType == SettingsExcludeType.REGEX) {
            String regex = JOptionPane.showInputDialog(Messages.getString("PreferenceDialog.EnterRegex"));
            if (StringUtils.isNotBlank(regex)) {
                excludeList.addItem(seType, regex);
            }
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
            List<Path> folList = new ArrayList<>();
            for (int i = 0; i < defaultIncomingFoldersList.getModel().getSize(); i++) {
                folList.add(Path.of(defaultIncomingFoldersList.getDescription(i)));
            }
            settingsCtrl.getSettings().setDefaultIncomingFolders(folList);

            List<SettingsExcludeItem> list = new ArrayList<>();
            for (int i = 0; i < excludeList.getModel().getSize(); i++) {
                SettingsExcludeType excludeListType = excludeList.getType(i);
                if (excludeListType == null || excludeList.getDescription(i) == null) {
                    continue;
                }
                SettingsExcludeItem sei = new SettingsExcludeItem(excludeList.getDescription(i), excludeListType);
                list.add(sei);
            }
            if (Messages.getLanguage() != (Language) cbxLanguage.getSelectedItem()) {
                Messages.setLanguage((Language) cbxLanguage.getSelectedItem());
                gui.redraw();
            }
            settingsCtrl.getSettings()
                    .setLanguage((Language) cbxLanguage.getSelectedItem())
                    .setExcludeList(list)
                    .setUpdateCheckPeriod((UpdateCheckPeriod) cbxUpdateCheckPeriod.getSelectedItem())
                    .setUpdateType((UpdateType) cbxUpdateType.getSelectedItem())
                    .setGeneralProxyEnabled(chkUseProxy.isSelected())
                    .setGeneralProxyHost(txtProxyHost.getText())
                    .setGeneralProxyPort(Integer.parseInt(txtProxyPort.getText()));
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
            settingsCtrl.getSettings()
                    .setOptionsAlwaysConfirm(chkAlwaysConfirm.isSelected())
                    .setOptionsMinAutomaticSelection(chkMinScoreSelection.isSelected())
                    .setOptionsMinAutomaticSelectionValue(sldMinScoreSelection.getValue())
                    .setOptionSubtitleExactMatch(chkSubtitleExactMethod.isSelected())
                    .setOptionSubtitleKeywordMatch(chkSubtitleKeywordMethod.isSelected())
                    .setOptionSubtitleExcludeHearingImpaired(chkExcludeHearingImpaired.isSelected())
                    .setOptionsShowOnlyFound(chkOnlyFound.isSelected())
                    .setOptionsStopOnSearchError(chkStopOnSearchError.isSelected())
                    .setProcessEpisodeSource((SettingsProcessEpisodeSource) cbxEpisodeProcessSource.getSelectedItem())
                    .setOptionsDefaultSelection(this.chkDefaultSelection.isSelected())
                    .setOptionsDefaultSelectionQualityList(this.pnlDefaultSelection.getDefaultSelectionList())
                    .setOptionsConfirmProviderMapping(this.chkConfirmProviderMapping.isSelected());
        } else {
            status = false;
        }
        if (testSerieSourcesTab()) {
            settingsCtrl.getSettings()
                    .setLoginAddic7edEnabled(chkUserAddic7edLogin.isSelected())
                    .setLoginAddic7edUsername(txtAddic7edUsername.getText())
                    .setLoginAddic7edPassword(txtAddic7edPassword.getText())
                    .setLoginOpenSubtitlesEnabled(chkUserOpenSubtitlesLogin.isSelected())
                    .setLoginOpenSubtitlesUsername(txtOpenSubtitlesUsername.getText())
                    .setLoginOpenSubtitlesPassword(txtOpenSubtitlesPassword.getText());
            List<Path> folList = new ArrayList<>();
            for (int i = 0; i < localSourcesFoldersList.getModel().getSize(); i++) {
                folList.add(Path.of(localSourcesFoldersList.getDescription(i)));
            }
            settingsCtrl.getSettings()
                    .setLocalSourcesFolders(folList)
                    .setSerieSourceAddic7ed(chkSourceAddic7ed.isSelected())
                    .setSerieSourceAddic7edProxy(chkSourceAddic7edProxy.isSelected())
                    .setSerieSourceTvSubtitles(chkSourceTvSubtitles.isSelected())
                    .setSerieSourcePodnapisi(chkSourcePodnapisi.isSelected())
                    .setSerieSourceOpensubtitles(chkSourceOpenSubtitles.isSelected())
                    .setSerieSourceLocal(chkSourceLocal.isSelected())
                    .setSerieSourceSubscene(chkSourceSubscene.isSelected());
        } else {
            status = false;
        }

        if (status) {
            setVisible(false);
            settingsCtrl.store();
        }
        this.eventEmitter.fire(new Event("providers.settings.change"));
    }

    private <T> Stream<T> stream(ListModel<T> listModel) {
        return IntStream.range(0, listModel.getSize()).mapToObj(listModel::getElementAt);
    }
}
