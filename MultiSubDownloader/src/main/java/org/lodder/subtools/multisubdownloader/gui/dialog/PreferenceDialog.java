package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.framework.event.Emitter;
import org.lodder.subtools.multisubdownloader.framework.event.Event;
import org.lodder.subtools.multisubdownloader.gui.ToStringListCellRenderer;
import org.lodder.subtools.multisubdownloader.gui.extra.JListWithImages;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.panels.DefaultSelectionPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.EpisodeLibraryPanel;
import org.lodder.subtools.multisubdownloader.gui.panels.MovieLibraryPanel;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeItem;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsExcludeType;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateCheckPeriod;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import net.miginfocom.swing.MigLayout;

public class PreferenceDialog extends MultiSubDialog {

    private static final long serialVersionUID = -5730220264781738564L;
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
    private JCheckBox chkProxyserverGebruiken, chkUserAddic7edLogin, chkUserOpenSubtitlesLogin, chkExcludeHearingImpaired;
    private JListWithImages defaultIncomingFoldersList, localSourcesFoldersList;
    private JCheckBox chkSerieSourceAddic7ed, chkSerieSourceTvSubtitles, chkSerieSourcePodnapisi,
            chkSerieSourceOpensubtitles, chkSerieSourceLocal, chkSerieSourceSubscene, chkSerieSourceAddic7edProxy;
    private JComboBox<SettingsProcessEpisodeSource> cbxEpisodeProcessSource;
    private JCheckBox chkMinScoreSelection;
    private JCheckBox chkConfirmProviderMapping;
    private JSlider sldMinScoreSelection;
    private final Manager manager;
    private JComboBox<UpdateCheckPeriod> cbxUpdateCheckPeriod;
    private JCheckBox chkDefaultSelection;
    private DefaultSelectionPanel pnlDefaultSelection;
    private final UserInteractionHandler userInteractionHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceDialog.class);

    /**
     * Create the dialog.
     */
    public PreferenceDialog(JFrame frame, final SettingsControl settingsCtrl, Emitter eventEmitter,
            Manager manager, UserInteractionHandler userInteractionHandler) {
        super(frame, Messages.getString("PreferenceDialog.Title"), true);
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
                pnlGeneral.setLayout(new MigLayout("", "[127px,grow][grow][grow]",
                        "[23px][grow][][][grow,center][][grow]"));
                {
                    JLabel lblDefaultIncomingFolder = new JLabel(Messages.getString("PreferenceDialog.DefaultIncomingFolder"));
                    pnlGeneral.add(lblDefaultIncomingFolder, "cell 0 0,alignx left,aligny center");
                }
                {
                    JButton btnBrowse = new JButton(Messages.getString("PreferenceDialog.AddFolder"));
                    btnBrowse.addActionListener(arg0 -> {
                        File path = MemoryFolderChooser.getInstance().selectDirectory(getContentPane(),
                                Messages.getString("PreferenceDialog.SelectFolder"));
                        if (defaultIncomingFoldersList.getModel().getSize() == 0) {
                            defaultIncomingFoldersList.addItem(SettingsExcludeType.FOLDER, path.getAbsolutePath());
                        } else {
                            boolean exists = false;
                            for (int i = 0; i < defaultIncomingFoldersList.getModel().getSize(); i++) {
                                if (defaultIncomingFoldersList.getDescription(i) != null
                                        && defaultIncomingFoldersList.getDescription(i)
                                                .equals(path.getAbsolutePath())) {
                                    exists = true;
                                }
                            }
                            if (!exists) {
                                defaultIncomingFoldersList.addItem(SettingsExcludeType.FOLDER, path.getAbsolutePath());
                            }
                        }
                    });
                    pnlGeneral.add(btnBrowse, "cell 1 0,alignx left,aligny top");
                }
                {
                    JButton button = new JButton(Messages.getString("PreferenceDialog.DeleteFolder"));
                    button.addActionListener(arg0 -> {
                        DefaultListModel<JPanel> model = (DefaultListModel<JPanel>) defaultIncomingFoldersList.getModel();
                        int selected = defaultIncomingFoldersList.getSelectedIndex();
                        if (model.size() > 0 && selected >= 0) {
                            model.removeElementAt(selected);
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
                    JLabel lblUitsluitLijst = new JLabel(Messages.getString("PreferenceDialog.ExcludeList"));
                    pnlGeneral.add(lblUitsluitLijst, "cell 0 2,alignx right,gaptop 10");
                }
                {
                    JButton btnAddUitsluitMap = new JButton(Messages.getString("PreferenceDialog.AddFolder"));
                    btnAddUitsluitMap.addActionListener(arg0 -> addExcludeItem(SettingsExcludeType.FOLDER));
                    pnlGeneral.add(btnAddUitsluitMap, "cell 1 2,alignx center,gaptop 10");
                }
                {
                    JButton btnVerwijderUitsluitMap = new JButton(Messages.getString("PreferenceDialog.DeleteItem"));
                    btnVerwijderUitsluitMap.addActionListener(arg0 -> removeExcludeItem());
                    pnlGeneral.add(btnVerwijderUitsluitMap, "cell 2 2,alignx center,gaptop 10");
                }
                {
                    JButton btnAddUitsluitRegex = new JButton(Messages.getString("PreferenceDialog.RegexToevoegen"));
                    btnAddUitsluitRegex.addActionListener(arg0 -> addExcludeItem(SettingsExcludeType.REGEX));
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
                    JLabel lblNewUpdateCheck = new JLabel(Messages.getString("PreferenceDialog.NewUpdateCheck"));
                    pnlGeneral.add(lblNewUpdateCheck, "cell 0 5 2 1");
                }
                {
                    cbxUpdateCheckPeriod = new JComboBox<>();
                    cbxUpdateCheckPeriod.setModel(new DefaultComboBoxModel<>(UpdateCheckPeriod.values()));
                    cbxUpdateCheckPeriod.setRenderer(new ToStringListCellRenderer<>(cbxUpdateCheckPeriod.getRenderer(),
                            o -> Messages.getString(((UpdateCheckPeriod) o).getLangCode())));
                    pnlGeneral.add(cbxUpdateCheckPeriod, "cell 2 5,growx");
                }
                {
                    JPanel pnlProxySettings = new JPanel();
                    pnlGeneral.add(pnlProxySettings, "cell 0 6 3 1,grow");
                    pnlProxySettings.setLayout(new MigLayout("", "[50px:n][][grow][grow]", "[][][][]"));
                    pnlProxySettings.add(new JLabel(Messages.getString("PreferenceDialog.ConfigureProxy")), "cell 0 0 4 1,gapy 5");
                    pnlProxySettings.add(new JSeparator(), "cell 0 0 4 1,growx,gapy 5");
                    {
                        chkProxyserverGebruiken = new JCheckBox(Messages.getString("PreferenceDialog.UseProxyServer"));
                        pnlProxySettings.add(chkProxyserverGebruiken, "cell 0 1 3 1");
                    }
                    {
                        JLabel lblProxyHost = new JLabel(Messages.getString("PreferenceDialog.Hostname"));
                        pnlProxySettings.add(lblProxyHost, "cell 1 2,alignx trailing");
                    }
                    {
                        txtProxyHost = new JTextField();
                        pnlProxySettings.add(txtProxyHost, "cell 2 2,growx");
                        txtProxyHost.setColumns(10);
                    }
                    {
                        JLabel lblProxyPoort = new JLabel(Messages.getString("PreferenceDialog.Port"));
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
            {

                JPanel pnlSerieSources = new JPanel();
                tabbedPane.addTab(Messages.getString("PreferenceDialog.SerieSources"), null, pnlSerieSources, null);
                pnlSerieSources.setLayout(new MigLayout("", "[grow]", "[][top][]"));
                JPanel pnlSerieSourcesSelectionSettings = new JPanel();
                pnlSerieSources.add(pnlSerieSourcesSelectionSettings, "cell 0 0 3 1,grow");
                pnlSerieSourcesSelectionSettings.setLayout(new MigLayout("",
                        "[50px:n][][100.00,grow][grow][grow]", "[][][][][][][]"));
                pnlSerieSourcesSelectionSettings.add(new JLabel(Messages.getString("PreferenceDialog.SelectPreferedSources")),
                        "cell 0 0 5 1,gapy 5");
                pnlSerieSources.add(pnlSerieSourcesSelectionSettings, "cell 0 0 3 1,grow");
                pnlSerieSourcesSelectionSettings.add(new JSeparator(), "cell 0 0 5 1,growx,gapy 5");

                chkSerieSourceAddic7ed = new JCheckBox("Addic7ed");
                pnlSerieSourcesSelectionSettings.add(chkSerieSourceAddic7ed, "cell 0 1 2 1");
                chkSerieSourceAddic7edProxy = new JCheckBox(Messages.getString("PreferenceDialog.Proxy"));
                pnlSerieSourcesSelectionSettings.add(chkSerieSourceAddic7edProxy, "cell 0 1 3 1");
                chkSerieSourceAddic7ed.addActionListener(e -> chkSerieSourceAddic7edProxy.setEnabled(((JCheckBox) e.getSource()).isSelected()));

                chkSerieSourceTvSubtitles = new JCheckBox("Tv Subtitles");
                pnlSerieSourcesSelectionSettings.add(chkSerieSourceTvSubtitles, "cell 0 2 2 1");
                chkSerieSourcePodnapisi = new JCheckBox("Podnapisi");
                pnlSerieSourcesSelectionSettings.add(chkSerieSourcePodnapisi, "cell 0 3 2 1");
                chkSerieSourceOpensubtitles = new JCheckBox("Opensubtitles");
                pnlSerieSourcesSelectionSettings.add(chkSerieSourceOpensubtitles, "cell 0 4 2 1");
                chkSerieSourceSubscene = new JCheckBox("Subscene");
                pnlSerieSourcesSelectionSettings.add(chkSerieSourceSubscene, "cell 0 5 2 1");
                chkSerieSourceLocal = new JCheckBox(Messages.getString("PreferenceDialog.Local"));
                pnlSerieSourcesSelectionSettings.add(chkSerieSourceLocal, "cell 0 6 2 1");
                //
                JPanel pnlAddic7edLoginSettings = new JPanel();
                pnlSerieSources.add(pnlAddic7edLoginSettings, "cell 0 1 3 1,grow");
                pnlAddic7edLoginSettings.setLayout(new MigLayout("", "[50px:n][][grow][grow]", "[][][][]"));
                pnlAddic7edLoginSettings.add(new JLabel(Messages.getString("PreferenceDialog.Addic7edLogin")), "cell 0 0 4 1,gapy 5");
                pnlAddic7edLoginSettings.add(new JSeparator(), "cell 0 0 4 1,growx,gapy 5");
                {
                    chkUserAddic7edLogin = new JCheckBox(Messages.getString("PreferenceDialog.UseAddic7edLogin"));
                    pnlAddic7edLoginSettings.add(chkUserAddic7edLogin, "cell 0 1 3 1");
                }
                {
                    JLabel lblUsername = new JLabel(Messages.getString("PreferenceDialog.Username"));
                    pnlAddic7edLoginSettings.add(lblUsername, "cell 1 2,alignx trailing");
                }
                {
                    txtAddic7edUsername = new JTextField();
                    pnlAddic7edLoginSettings.add(txtAddic7edUsername, "cell 2 2,growx");
                    txtAddic7edUsername.setColumns(10);
                }
                {
                    JLabel lblAddic7edPassword = new JLabel(Messages.getString("PreferenceDialog.Password"));
                    pnlAddic7edLoginSettings.add(lblAddic7edPassword, "cell 1 3,alignx trailing");
                }
                {
                    txtAddic7edPassword = new JPasswordField();
                    pnlAddic7edLoginSettings.add(txtAddic7edPassword, "cell 2 3,growx");
                    txtAddic7edPassword.setColumns(10);
                }
                //
                JPanel pnlOpenSubtiltesLoginSettings = new JPanel();
                pnlSerieSources.add(pnlOpenSubtiltesLoginSettings, "cell 0 1 3 1,grow");
                pnlOpenSubtiltesLoginSettings.setLayout(new MigLayout("", "[50px:n][][grow][grow]", "[][][][]"));
                pnlOpenSubtiltesLoginSettings.add(new JLabel(Messages.getString("PreferenceDialog.OpenSubtitlesLogin")), "cell 0 0 4 1,gapy 5");
                pnlOpenSubtiltesLoginSettings.add(new JSeparator(), "cell 0 0 4 1,growx,gapy 5");
                {
                    chkUserOpenSubtitlesLogin = new JCheckBox(Messages.getString("PreferenceDialog.UseOpenSubtitlesLogin"));
                    pnlOpenSubtiltesLoginSettings.add(chkUserOpenSubtitlesLogin, "cell 0 1 3 1");
                }
                {
                    JLabel lblUsername = new JLabel(Messages.getString("PreferenceDialog.Username"));
                    pnlOpenSubtiltesLoginSettings.add(lblUsername, "cell 1 2,alignx trailing");
                }
                {
                    txtOpenSubtitlesUsername = new JTextField();
                    pnlOpenSubtiltesLoginSettings.add(txtOpenSubtitlesUsername, "cell 2 2,growx");
                    txtOpenSubtitlesUsername.setColumns(10);
                }
                {
                    JLabel lblOpenSubtitlesPassword = new JLabel(Messages.getString("PreferenceDialog.Password"));
                    pnlOpenSubtiltesLoginSettings.add(lblOpenSubtitlesPassword, "cell 1 3,alignx trailing");
                }
                {
                    txtOpenSubtitlesPassword = new JPasswordField();
                    pnlOpenSubtiltesLoginSettings.add(txtOpenSubtitlesPassword, "cell 2 3,growx");
                    txtOpenSubtitlesPassword.setColumns(10);
                }
                //
                JPanel pnlLocalSourcesSettings = new JPanel();
                pnlSerieSources.add(pnlLocalSourcesSettings, "cell 0 2 3 1,grow");
                pnlLocalSourcesSettings.setLayout(new MigLayout("", "[][][][grow]", "[][][]"));
                pnlLocalSourcesSettings.add(new JLabel(Messages.getString("PreferenceDialog.LocalFolders")), "cell 0 0 5 1,gapy 5");
                pnlLocalSourcesSettings.add(new JSeparator(), "cell 0 0 5 1,growx,gapy 5");

                {
                    JLabel lblLocalSources = new JLabel(Messages.getString("PreferenceDialog.LocalFolderWithSubtitles"));
                    pnlLocalSourcesSettings.add(lblLocalSources, "cell 0 1,alignx left,aligny center");
                }
                {
                    JButton btnBrowseLocalSources = new JButton(Messages.getString("PreferenceDialog.AddFolder"));
                    btnBrowseLocalSources.addActionListener(arg0 -> {
                        File path = MemoryFolderChooser.getInstance().selectDirectory(getContentPane(),
                                Messages.getString("PreferenceDialog.SelectFolder"));
                        if (localSourcesFoldersList.getModel().getSize() == 0) {
                            localSourcesFoldersList.addItem(SettingsExcludeType.FOLDER, path.getAbsolutePath());
                        } else {
                            boolean exists = false;
                            for (int i = 0; i < localSourcesFoldersList.getModel().getSize(); i++) {
                                if (localSourcesFoldersList.getDescription(i) != null
                                        && localSourcesFoldersList.getDescription(i).equals(path.getAbsolutePath())) {
                                    exists = true;
                                }
                            }
                            if (!exists) {
                                localSourcesFoldersList.addItem(SettingsExcludeType.FOLDER, path.getAbsolutePath());
                            }
                        }
                    });
                    pnlLocalSourcesSettings.add(btnBrowseLocalSources, "cell 1 1,alignx left,aligny top");
                }
                {
                    JButton btnRemoveLocalSources = new JButton(Messages.getString("PreferenceDialog.DeleteFolder"));
                    btnRemoveLocalSources.addActionListener(arg0 -> {
                        DefaultListModel<JPanel> model = (DefaultListModel<JPanel>) localSourcesFoldersList.getModel();
                        int selected = localSourcesFoldersList.getSelectedIndex();
                        if (model.size() > 0 && selected >= 0) {
                            model.removeElementAt(selected);
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
                JButton okButton = new JButton(Messages.getString("PreferenceDialog.OK"));
                okButton.addActionListener(arg0 -> testAndSaveValues());
                okButton.setActionCommand(Messages.getString("PreferenceDialog.OK"));
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton(Messages.getString("PreferenceDialog.Cancel"));
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
        for (File element : settingsCtrl.getSettings().getDefaultIncomingFolders()) {
            defaultIncomingFoldersList.addItem(SettingsExcludeType.FOLDER, element.getAbsolutePath());
        }
        for (File element : settingsCtrl.getSettings().getLocalSourcesFolders()) {
            localSourcesFoldersList.addItem(SettingsExcludeType.FOLDER, element.getAbsolutePath());
        }
        chkProxyserverGebruiken.setSelected(settingsCtrl.getSettings().isGeneralProxyEnabled());
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
        chkSerieSourceAddic7ed.setSelected(settingsCtrl.getSettings().isSerieSourceAddic7ed());
        chkSerieSourceAddic7edProxy.setSelected(settingsCtrl.getSettings().isSerieSourceAddic7edProxy());
        chkSerieSourceAddic7edProxy.setEnabled(settingsCtrl.getSettings().isSerieSourceAddic7ed());
        chkSerieSourceTvSubtitles.setSelected(settingsCtrl.getSettings().isSerieSourceTvSubtitles());
        chkSerieSourcePodnapisi.setSelected(settingsCtrl.getSettings().isSerieSourcePodnapisi());
        chkSerieSourceOpensubtitles.setSelected(settingsCtrl.getSettings().isSerieSourceOpensubtitles());
        chkSerieSourceLocal.setSelected(settingsCtrl.getSettings().isSerieSourceLocal());
        chkSerieSourceSubscene.setSelected(settingsCtrl.getSettings().isSerieSourceSubscene());
        cbxUpdateCheckPeriod.setSelectedItem(settingsCtrl.getSettings().getUpdateCheckPeriod());
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
            File path = MemoryFolderChooser.getInstance().selectDirectory(getContentPane(),
                    Messages.getString("PreferenceDialog.SelectExcludeFolder"));
            excludeList.addItem(seType, path.getAbsolutePath());
        } else if (seType == SettingsExcludeType.REGEX) {
            String regex = JOptionPane.showInputDialog(Messages.getString("PreferenceDialog.EnterRegex"));
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
            List<File> folList = new ArrayList<>();
            for (int i = 0; i < defaultIncomingFoldersList.getModel().getSize(); i++) {
                folList.add(new File(defaultIncomingFoldersList.getDescription(i)));
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
            settingsCtrl.getSettings().setExcludeList(list);
            settingsCtrl.getSettings().setUpdateCheckPeriod((UpdateCheckPeriod) cbxUpdateCheckPeriod.getSelectedItem());
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
            settingsCtrl.getSettings().setOptionsMinAutomaticSelection(chkMinScoreSelection.isSelected());
            settingsCtrl.getSettings().setOptionsMinAutomaticSelectionValue(sldMinScoreSelection.getValue());
            settingsCtrl.getSettings().setOptionSubtitleExactMatch(chkSubtitleExactMethod.isSelected());
            settingsCtrl.getSettings().setOptionSubtitleKeywordMatch(chkSubtitleKeywordMethod.isSelected());
            settingsCtrl.getSettings().setOptionSubtitleExcludeHearingImpaired(chkExcludeHearingImpaired.isSelected());
            settingsCtrl.getSettings().setOptionsShowOnlyFound(chkOnlyFound.isSelected());
            settingsCtrl.getSettings().setOptionsStopOnSearchError(chkStopOnSearchError.isSelected());
            settingsCtrl.getSettings().setProcessEpisodeSource((SettingsProcessEpisodeSource) cbxEpisodeProcessSource.getSelectedItem());
            settingsCtrl.getSettings().setOptionsDefaultSelection(this.chkDefaultSelection.isSelected());
            settingsCtrl.getSettings().setOptionsDefaultSelectionQualityList(this.pnlDefaultSelection.getDefaultSelectionList());
            settingsCtrl.getSettings().setOptionsConfirmProviderMapping(this.chkConfirmProviderMapping.isSelected());
        } else {
            status = false;
        }
        if (testSerieSourcesTab()) {
            settingsCtrl.getSettings().setLoginAddic7edEnabled(chkUserAddic7edLogin.isSelected());
            settingsCtrl.getSettings().setLoginAddic7edUsername(txtAddic7edUsername.getText());
            settingsCtrl.getSettings().setLoginAddic7edPassword(txtAddic7edPassword.getText());
            settingsCtrl.getSettings().setLoginOpenSubtitlesEnabled(chkUserOpenSubtitlesLogin.isSelected());
            settingsCtrl.getSettings().setLoginOpenSubtitlesUsername(txtOpenSubtitlesUsername.getText());
            settingsCtrl.getSettings().setLoginOpenSubtitlesPassword(txtOpenSubtitlesPassword.getText());
            List<File> folList = new ArrayList<>();
            for (int i = 0; i < localSourcesFoldersList.getModel().getSize(); i++) {
                folList.add(new File(localSourcesFoldersList.getDescription(i)));
            }
            settingsCtrl.getSettings().setLocalSourcesFolders(folList);
            settingsCtrl.getSettings().setSerieSourceAddic7ed(chkSerieSourceAddic7ed.isSelected());
            settingsCtrl.getSettings().setSerieSourceAddic7edProxy(chkSerieSourceAddic7edProxy.isSelected());
            settingsCtrl.getSettings().setSerieSourceTvSubtitles(chkSerieSourceTvSubtitles.isSelected());
            settingsCtrl.getSettings().setSerieSourcePodnapisi(chkSerieSourcePodnapisi.isSelected());
            settingsCtrl.getSettings().setSerieSourceOpensubtitles(chkSerieSourceOpensubtitles.isSelected());
            settingsCtrl.getSettings().setSerieSourceLocal(chkSerieSourceLocal.isSelected());
            settingsCtrl.getSettings().setSerieSourceSubscene(chkSerieSourceSubscene.isSelected());
        } else {
            status = false;
        }

        if (status) {
            setVisible(false);
            settingsCtrl.store();
        }
        this.eventEmitter.fire(new Event("providers.settings.change"));
    }
}
