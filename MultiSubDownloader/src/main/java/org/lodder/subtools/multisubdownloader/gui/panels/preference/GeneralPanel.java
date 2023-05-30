package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import java.io.Serial;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.JListWithImages;
import org.lodder.subtools.multisubdownloader.gui.extra.JListWithImages.LabelPanel;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.PanelCheckBox;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcheckbox.JCheckBoxExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcombobox.MyComboBox;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.JTextFieldExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyTextFieldInteger;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyTextFieldString;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.PathMatchType;
import org.lodder.subtools.multisubdownloader.settings.model.PathOrRegex;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateCheckPeriod;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateType;
import org.lodder.subtools.sublibrary.Language;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JTextFieldExtension.class, JCheckBoxExtension.class, JComponentExtension.class, AbstractButtonExtension.class })
public class GeneralPanel extends JPanel implements PreferencePanelIntf {

    @Serial
    private static final long serialVersionUID = -5458593307643063563L;

    private final GUI gui;
    private final SettingsControl settingsCtrl;

    private final MyComboBox<Language> cbxLanguage;
    private final JListWithImages<Path> defaultIncomingFoldersList;
    private final JListWithImages<PathOrRegex> excludeList;
    private final MyComboBox<UpdateCheckPeriod> cbxUpdateCheckPeriod;
    private final MyComboBox<UpdateType> cbxUpdateType;
    private final JCheckBox chkUseProxy;
    private final MyTextFieldString txtProxyHost;
    private final MyTextFieldInteger txtProxyPort;

    public GeneralPanel(GUI gui, SettingsControl settingsCtrl) {
        super(new MigLayout("fill, nogrid"));
        this.gui = gui;
        this.settingsCtrl = settingsCtrl;

        JPanel settingsPanel = TitlePanel.title(Messages.getString("PreferenceDialog.Settings"))
                .padding(0).paddingLeft(20).useGrid().fillContents(false).addTo(this, "span, grow, wrap");
        {

            {
                new JLabel(Messages.getString("PreferenceDialog.Language")).addTo(settingsPanel);

                this.cbxLanguage = new MyComboBox<>(Messages.getAvailableLanguages(), Language.class)
                        .withToMessageStringRenderer(Language::getMsgCode)
                        .addTo(settingsPanel, "wrap");
            }

            {
                new JLabel(Messages.getString("PreferenceDialog.DefaultIncomingFolder")).addTo(settingsPanel, "aligny center, span 1 2");

                new JScrollPane().addTo(settingsPanel, "growx, span, wrap")
                        .setViewportView(this.defaultIncomingFoldersList = new JListWithImages<>());

                new JButton(Messages.getString("PreferenceDialog.AddFolder"))
                        .withActionListener(
                                () -> MemoryFolderChooser.getInstance()
                                        .selectDirectory(settingsPanel, Messages.getString("PreferenceDialog.SelectFolder"))
                                        .map(Path::toAbsolutePath)
                                        .filter(path -> !defaultIncomingFoldersList.contains(path))
                                        .ifPresent(path -> defaultIncomingFoldersList.addItem(PathMatchType.FOLDER.getImage(), path)))
                        .addTo(settingsPanel, "span, split 2");

                new JButton(Messages.getString("PreferenceDialog.DeleteFolder"))
                        .withActionListener(() -> defaultIncomingFoldersList.removeSelectedItem())
                        .addTo(settingsPanel, "wrap, gapbottom 10px");
            }
            {
                new JLabel(Messages.getString("PreferenceDialog.ExcludeList")).addTo(settingsPanel, "aligny center, span 1 2");

                new JScrollPane().addTo(settingsPanel, "growx, span, wrap")
                        .setViewportView(this.excludeList = new JListWithImages<>());

                Consumer<PathMatchType> addExcludeItemConsumer = type -> {
                    if (type == PathMatchType.FOLDER) {
                        MemoryFolderChooser.getInstance().selectDirectory(settingsPanel, Messages.getString("PreferenceDialog.SelectExcludeFolder"))
                                .map(Path::toAbsolutePath).map(PathOrRegex::new)
                                .ifPresent(pathOrRegex -> excludeList.addItem(pathOrRegex.getImage(), pathOrRegex));
                    } else if (type == PathMatchType.REGEX) {
                        String regex = JOptionPane.showInputDialog(Messages.getString("PreferenceDialog.EnterRegex"));
                        if (StringUtils.isNotBlank(regex)) {
                            excludeList.addItem(PathMatchType.REGEX.getImage(), new PathOrRegex(regex));
                        }
                    }
                };

                new JButton(Messages.getString("PreferenceDialog.AddFolder"))
                        .withActionListener(() -> addExcludeItemConsumer.accept(PathMatchType.FOLDER))
                        .addTo(settingsPanel, "span, split 3");

                new JButton(Messages.getString("PreferenceDialog.DeleteFolder"))
                        .withActionListener(() -> excludeList.removeSelectedItem())
                        .addTo(settingsPanel);

                new JButton(Messages.getString("PreferenceDialog.RegexToevoegen"))
                        .withActionListener(() -> addExcludeItemConsumer.accept(PathMatchType.REGEX))
                        .addTo(settingsPanel);
            }
        }

        {

            JPanel updatePanel = TitlePanel.title(Messages.getString("PreferenceDialog.Update"))
                    .padding(0).paddingLeft(20).useGrid().fillContents(false).addTo(this, "span, grow, wrap");
            {
                new JLabel(Messages.getString("PreferenceDialog.NewUpdateCheck")).addTo(updatePanel);
                this.cbxUpdateCheckPeriod = new MyComboBox<>(UpdateCheckPeriod.values())
                        .withToMessageStringRenderer(UpdateCheckPeriod::getLangCode)
                        .addTo(updatePanel, "wrap");
                new JLabel(Messages.getString("PreferenceDialog.UpdateType")).addTo(updatePanel);
                this.cbxUpdateType = new MyComboBox<>(UpdateType.values())
                        .withToMessageStringRenderer(UpdateType::getMsgCode).addTo(updatePanel);
            }
        }

        {

            JPanel proxyPanel = TitlePanel.title(Messages.getString("PreferenceDialog.ConfigureProxy"))
                    .padding(0).paddingLeft(20).fillContents(false).addTo(this, "span, grow");
            {
                JPanel proxyChkPanel =
                        PanelCheckBox.checkbox(this.chkUseProxy = new JCheckBox(Messages.getString("PreferenceDialog.UseProxyServer")))
                                .panelOnSameLine().panelLayout(new MigLayout("insets 0, fill")).leftGap(0).addTo(proxyPanel);
                {
                    new JLabel(Messages.getString("PreferenceDialog.Hostname")).addTo(proxyChkPanel);
                    this.txtProxyHost =
                            MyTextFieldString.builder().requireValue().build().withColumns(10).withEnabled(false).addTo(proxyChkPanel, "wrap");
                    new JLabel(Messages.getString("PreferenceDialog.Port")).addTo(proxyChkPanel);
                    this.txtProxyPort = MyTextFieldInteger.builder().requireValue().build().withColumns(10).withEnabled(false).addTo(proxyChkPanel);
                }
            }
        }

        loadPreferenceSettings();
    }

    public void loadPreferenceSettings() {
        cbxLanguage.setSelectedItem(settingsCtrl.getSettings().getLanguage());
        defaultIncomingFoldersList.addItems(PathMatchType.FOLDER.getImage(), settingsCtrl.getSettings().getDefaultIncomingFolders());
        settingsCtrl.getSettings().getExcludeList().forEach(pathOrRegex -> excludeList.addItem(pathOrRegex.getImage(), pathOrRegex));
        cbxUpdateCheckPeriod.setSelectedItem(settingsCtrl.getSettings().getUpdateCheckPeriod());
        cbxUpdateType.setSelectedItem(settingsCtrl.getSettings().getUpdateType());
        chkUseProxy.setSelected(settingsCtrl.getSettings().isGeneralProxyEnabled());
        txtProxyHost.setText(settingsCtrl.getSettings().getGeneralProxyHost());
        txtProxyPort.setObject(settingsCtrl.getSettings().getGeneralProxyPort());
    }

    public void savePreferenceSettings() {
        if (Messages.getLanguage() != cbxLanguage.getSelectedItem()) {
            Messages.setLanguage(cbxLanguage.getSelectedItem());
            gui.redraw();
        }
        List<Path> defaultIncomingFolders = defaultIncomingFoldersList.stream().map(LabelPanel::getObject).toList();
        List<PathOrRegex> exclList = excludeList.stream().map(labelPanel -> new PathOrRegex(labelPanel.getObject().getValue())).toList();
        settingsCtrl.getSettings()
                .setLanguage(cbxLanguage.getSelectedItem())
                .setDefaultIncomingFolders(defaultIncomingFolders)
                .setExcludeList(exclList)
                .setUpdateCheckPeriod(cbxUpdateCheckPeriod.getSelectedItem())
                .setUpdateType(cbxUpdateType.getSelectedItem())
                .setGeneralProxyEnabled(chkUseProxy.isSelected())
                .setGeneralProxyHost(txtProxyHost.getText())
                .setGeneralProxyPort(txtProxyPort.getOptionalObject().orElse(80));

    }

    @Override
    public boolean hasValidSettings() {
        return txtProxyHost.hasValidValue() && txtProxyPort.hasValidValue();
    }
}
