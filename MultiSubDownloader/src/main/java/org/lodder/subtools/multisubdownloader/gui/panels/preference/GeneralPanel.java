package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import java.io.Serial;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.BoxModelProperties;
import org.lodder.subtools.multisubdownloader.gui.extra.JListWithImages;
import org.lodder.subtools.multisubdownloader.gui.extra.JListWithImages.LabelPanel;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.PanelCheckBox;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyTextFieldInteger;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyTextFieldString;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.PathMatchType;
import org.lodder.subtools.multisubdownloader.settings.model.PathOrRegex;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateCheckPeriod;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateType;
import org.lodder.subtools.sublibrary.Language;

public class GeneralPanel extends JPanel implements PreferencePanelIntf {

    @Serial private static final long serialVersionUID = -5458593307643063563L;

    private final GUI gui;
    private final SettingsControl settingsCtrl;
    private final JComboBox<Language> cbxLanguage;
    private final JListWithImages<Path> defaultIncomingFoldersList;
    private final JListWithImages<PathOrRegex> excludeList;
    private final JComboBox<UpdateCheckPeriod> cbxUpdateCheckPeriod;
    private final JComboBox<UpdateType> cbxUpdateType;
    private final JCheckBox chkUseProxy;
    private final MyTextFieldString txtProxyHost;
    private final MyTextFieldInteger txtProxyPort;

    public GeneralPanel(GUI gui, SettingsControl settingsCtrl) {
        super(new MigLayout("fill, nogrid"));
        this.gui = gui;
        this.settingsCtrl = settingsCtrl;

        JPanel settingsPanel = new TitlePanel(
            title:getText("PreferenceDialog.Settings"),
            padding:new BoxModelProperties(0, 20, 0, 0),
            useGrid:true,
            fillContents:false)
            .addToPanel(this, "span, grow, wrap");
        {

            // Language \\

            new JLabel(getText("PreferenceDialog.Language")).addTo(settingsPanel);
            this.cbxLanguage = JComboBox.create(getAvailableLanguages())
                .toMessageStringRenderer(Language::getMsgCode)
                .addTo(settingsPanel, "wrap");

            // Default Incoming Folder \\

            new JLabel(getText("PreferenceDialog.DefaultIncomingFolder")).addTo(settingsPanel,
                "aligny center, span 1 2");

            new JScrollPane()
                .viewportView(this.defaultIncomingFoldersList = new JListWithImages<>())
                .addTo(settingsPanel, "growx, span, wrap");

            new JButton(getText("PreferenceDialog.AddFolder"))
                .actionListener(() -> MemoryFolderChooser.getInstance()
                    .selectDirectory(settingsPanel, getText("PreferenceDialog.SelectFolder"))
                    .map(Path::toAbsolutePath)
                    .filter(path -> !defaultIncomingFoldersList.contains(path))
                    .ifPresent(p -> defaultIncomingFoldersList.addItem(PathMatchType.FOLDER.image, p)))
                .addTo(settingsPanel, "span, split 2");

            new JButton(getText("PreferenceDialog.DeleteFolder"))
                .actionListener(defaultIncomingFoldersList::removeSelectedItem)
                .addTo(settingsPanel, "wrap, gapbottom 10px");

            // Exclude List \\

            new JLabel(getText("PreferenceDialog.ExcludeList"))
                .addTo(settingsPanel, "aligny center, span 1 2");

            new JScrollPane()
                .viewportView(this.excludeList = new JListWithImages<>())
                .addTo(settingsPanel, "growx, span, wrap");

            Consumer<PathMatchType> addExcludeItemConsumer = type -> {
                if (type == PathMatchType.FOLDER) {
                    MemoryFolderChooser.getInstance()
                        .selectDirectory(settingsPanel, getText("PreferenceDialog.SelectExcludeFolder"))
                        .map(Path::toAbsolutePath)
                        .map(PathOrRegex::new)
                        .ifPresent(pathOrRegex -> excludeList.addItem(pathOrRegex.image, pathOrRegex));
                } else if (type == PathMatchType.REGEX) {
                    String regex = JOptionPane.showInputDialog(getText("PreferenceDialog.EnterRegex"));
                    if (StringUtils.isNotBlank(regex)) {
                        excludeList.addItem(PathMatchType.REGEX.image, new PathOrRegex(regex));
                    }
                }
            };

            new JButton(getText("PreferenceDialog.AddFolder"))
                .actionListener(() -> addExcludeItemConsumer.accept(PathMatchType.FOLDER))
                .addTo(settingsPanel, "span, split 3");

            new JButton(getText("PreferenceDialog.DeleteFolder"))
                .actionListener(excludeList::removeSelectedItem)
                .addTo(settingsPanel);

            new JButton(getText("PreferenceDialog.RegexToevoegen"))
                .actionListener(() -> addExcludeItemConsumer.accept(PathMatchType.REGEX))
                .addTo(settingsPanel);
        }

        {

            JPanel updatePanel = new TitlePanel(
                title:getText("PreferenceDialog.Update"),
                padding:new BoxModelProperties(0, 20, 0, 0),
                useGrid:true,
                fillContents:false)
                .addToPanel(this, "span, grow, wrap");

            new JLabel(getText("PreferenceDialog.NewUpdateCheck")).addTo(updatePanel);
            this.cbxUpdateCheckPeriod = new JComboBox<>(UpdateCheckPeriod.values())
                .toMessageStringRenderer(UpdateCheckPeriod::getLangCode)
                .addTo(updatePanel, "wrap");
            new JLabel(getText("PreferenceDialog.UpdateType")).addTo(updatePanel);
            this.cbxUpdateType = new JComboBox<>(UpdateType.values())
                .toMessageStringRenderer(UpdateType::getMsgCode)
                .addTo(updatePanel);
        }

        {

            JPanel proxyPanel = new TitlePanel(
                title:getText("PreferenceDialog.ConfigureProxy"),
                padding:new BoxModelProperties(0, 20, 0, 0),
                fillContents:false)
                .addToPanel(this, "span, grow");

            new PanelCheckBox(
                checkbox:this.chkUseProxy = new JCheckBox(getText("PreferenceDialog.UseProxyServer")),
                panelOnNewLine:false,
                panelLayout:new MigLayout("insets 0, fill")
                )
                .addToPanel(proxyPanel)
                .addComponent(new JLabel(getText("PreferenceDialog.Hostname")))
                .addComponent("wrap",
                    this.txtProxyHost = MyTextFieldString.builder().requireValue().build().columns(30))
                .addComponent(new JLabel(getText("PreferenceDialog.Port")))
                .addComponent(this.txtProxyPort = MyTextFieldInteger.builder().requireValue().build().columns(5));
        }

        loadPreferenceSettings();
    }

    public void loadPreferenceSettings() {
        cbxLanguage.setSelectedItem(settingsCtrl.settings.language);
        defaultIncomingFoldersList.addItems(PathMatchType.FOLDER.image, settingsCtrl.settings.defaultIncomingFolders);
        settingsCtrl.settings.excludeList.forEach(pathOrRegex -> excludeList.addItem(pathOrRegex.image, pathOrRegex));
        cbxUpdateCheckPeriod.setSelectedItem(settingsCtrl.settings.updateCheckPeriod);
        cbxUpdateType.setSelectedItem(settingsCtrl.settings.updateType);
        chkUseProxy.setSelected(settingsCtrl.settings.generalProxyEnabled);
        txtProxyHost.setText(settingsCtrl.settings.generalProxyHost);
        txtProxyPort.setObject(settingsCtrl.settings.generalProxyPort);
    }

    public void savePreferenceSettings() {
        if (Messages.language != cbxLanguage.getSelectedValue()) {
            Messages.language = cbxLanguage.getSelectedValue();
            gui.redraw();
        }
        List<Path> defaultIncomingFolders = defaultIncomingFoldersList.stream().map(LabelPanel::getObject).toList();
        List<PathOrRegex> exclList =
            excludeList.stream().map(labelPanel -> new PathOrRegex(labelPanel.getObject().value)).toList();
        settingsCtrl.settings.language = cbxLanguage.getSelectedValue();
        settingsCtrl.settings.defaultIncomingFolders = defaultIncomingFolders;
        settingsCtrl.settings.replaceExcludeList(exclList);
        settingsCtrl.settings.updateCheckPeriod = cbxUpdateCheckPeriod.getSelectedValue();
        settingsCtrl.settings.updateType = cbxUpdateType.getSelectedValue();
        settingsCtrl.settings.generalProxyEnabled = chkUseProxy.isSelected();
        settingsCtrl.settings.generalProxyHost = txtProxyHost.getText();
        settingsCtrl.settings.generalProxyPort = txtProxyPort.getOptionalObject().orElse(80);
    }

    @Override
    public boolean hasValidSettings() {
        return txtProxyHost.hasValidValue() && txtProxyPort.hasValidValue();
    }
}
