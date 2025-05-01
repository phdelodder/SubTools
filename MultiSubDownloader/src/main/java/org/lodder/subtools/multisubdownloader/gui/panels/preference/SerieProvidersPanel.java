package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import static java.util.function.Predicate.*;
import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import java.io.Serial;
import java.nio.file.Path;

import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.multisubdownloader.gui.extra.JListWithImages;
import org.lodder.subtools.multisubdownloader.gui.extra.JListWithImages.LabelPanel;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.PanelCheckBox;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyPasswordField;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyTextFieldString;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.PathMatchType;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.OpenSubtitlesApi;

public class SerieProvidersPanel extends JPanel implements PreferencePanelIntf {

    @Serial
    private static final long serialVersionUID = -5458593307643063563L;

    private final SettingsControl settingsCtrl;
    private final JCheckBox chkSourceAddic7ed;
    private final JCheckBox chkUserAddic7edLogin;
    private final JCheckBox chkSourceAddic7edProxy;
    private final MyTextFieldString txtAddic7edUsername;
    private final MyPasswordField txtAddic7edPassword;
    private final JCheckBox chkSourceTvSubtitles;
    private final JCheckBox chkSourcePodnapisi;
    private final JCheckBox chkSourceOpenSubtitles;
    private final JCheckBox chkUserOpenSubtitlesLogin;
    private final MyTextFieldString txtOpenSubtitlesUsername;
    private final MyPasswordField txtOpenSubtitlesPassword;
    //    private final JCheckBox chkSourceSubscene;
    private final JCheckBox chkSourceLocal;
    private final JListWithImages<Path> localSourcesFoldersList;

    public SerieProvidersPanel(SettingsControl settingsCtrl) {
        super(new MigLayout("insets 0, fill, nogrid"));
        this.settingsCtrl = settingsCtrl;

        JPanel titlePanel = new TitlePanel(getText("PreferenceDialog.SelectPreferredSources"))
            .addToPanel(this, "span, grow");

        {
            // ADDIC7ED
            this.chkSourceAddic7ed = new JCheckBox("Addic7ed");
            this.chkUserAddic7edLogin = new JCheckBox(getText("PreferenceDialog.UseAddic7edLogin"));
            this.chkSourceAddic7edProxy = new JCheckBox(getText("PreferenceDialog.Proxy"));

            new PanelCheckBox(checkbox:chkSourceAddic7ed, panelOnNewLine:true)
                .addToPanel(titlePanel, "wrap")
                .addComponent("wrap", chkSourceAddic7edProxy)
                .addComponent(new PanelCheckBox(
                    checkbox:chkUserAddic7edLogin,
                    panelOnNewLine:true,
                    panelLayout:new MigLayout("insets 0, novisualpadding")
                    )
                    .addComponent(new JLabel(getText("PreferenceDialog.Username")))
                    .addComponent("wrap", this.txtAddic7edUsername =
                        MyTextFieldString.builder().requireValue().build().columns(20))
                    .addComponent(new JLabel(getText("PreferenceDialog.Password")))
                    .addComponent(this.txtAddic7edPassword =
                        MyPasswordField.builder().requireValue().build().columns(20)));

            // TV SUBTITLES
            this.chkSourceTvSubtitles = new JCheckBox("Tv Subtitles").addTo(titlePanel, "wrap");

            // PODNAPISI
            this.chkSourcePodnapisi = new JCheckBox("Podnapisi").addTo(titlePanel, "wrap");

            // OPENSUBTITLES
            this.chkSourceOpenSubtitles = new JCheckBox("OpenSubtitles");
            this.chkUserOpenSubtitlesLogin = new JCheckBox(getText("PreferenceDialog.UseOpenSubtitlesLogin"));
            new PanelCheckBox(checkbox:chkSourceOpenSubtitles, panelOnNewLine:true)
                .addTo(titlePanel, "wrap").panel
                .addComponent(new PanelCheckBox(
                    checkbox:chkUserOpenSubtitlesLogin,
                    panelOnNewLine:true,
                    panelLayout:new MigLayout("insets 0, novisualpadding")
                    )
                    .addComponent(new JLabel(getText("PreferenceDialog.Username")))
                    .addComponent("wrap", txtOpenSubtitlesUsername =
                        MyTextFieldString.builder().requireValue().build().columns(20))
                    .addComponent(new JLabel(getText("PreferenceDialog.Password")))
                    .addComponent(txtOpenSubtitlesPassword =
                        MyPasswordField.builder().requireValue().build().columns(20)));

            // SUBSCENE
//            this.chkSourceSubscene = new JCheckBox("Subscene").addTo(titlePanel, "wrap");

            // LOCAL
            this.chkSourceLocal = new JCheckBox(getText("PreferenceDialog.Local"));
            JScrollPane scrLocalSources =
                new JScrollPane().viewportView(this.localSourcesFoldersList = new JListWithImages<>());
            JButton btnBrowseLocalSources = new JButton(getText("PreferenceDialog.AddFolder"))
                .actionListener(() -> MemoryFolderChooser.getInstance()
                    .selectDirectory(this, getText("PreferenceDialog.SelectFolder"))
                    .map(Path::toAbsolutePath).filter(not(localSourcesFoldersList::contains))
                    .ifPresent(path -> localSourcesFoldersList.addItem(PathMatchType.FOLDER.image, path)));
            JButton btnRemoveLocalSources = new JButton(getText("PreferenceDialog.DeleteFolder"))
                .actionListener(localSourcesFoldersList::removeSelectedItem);

            new PanelCheckBox(checkbox:chkSourceLocal, panelOnNewLine:true)
                .addTo(titlePanel).panel
                .addComponent("aligny top, gapy 5px",
                    new JLabel(getText("PreferenceDialog.LocalFolderWithSubtitles")))
                .addComponent("wrap",
                    new JPanel(new MigLayout("insets 0", "[grow, nogrid]"))
                        .addComponent("split", btnBrowseLocalSources)
                        .addComponent("wrap", btnRemoveLocalSources)
                        .addComponent("wrap", scrLocalSources));
        }

        loadPreferenceSettings();
    }

    public void loadPreferenceSettings() {
        Settings settings = settingsCtrl.settings;
        chkSourceAddic7ed.setSelected(settings.serieSourceAddic7ed);
        chkUserAddic7edLogin.setSelected(settings.loginAddic7edEnabled);
        chkSourceAddic7edProxy.setSelected(settings.serieSourceAddic7edProxy);
        // chkSourceAddic7edProxy.setEnabled(settings.serieSourceAddic7ed);
        txtAddic7edUsername.setText(settings.loginAddic7edUsername);
        txtAddic7edPassword.setText(settings.loginAddic7edPassword);

        chkSourceTvSubtitles.setSelected(settings.serieSourceTvSubtitles);
        chkSourcePodnapisi.setSelected(settings.serieSourcePodnapisi);
        chkSourceOpenSubtitles.setSelected(settings.serieSourceOpensubtitles);
        chkUserOpenSubtitlesLogin.setSelected(settings.loginOpenSubtitlesEnabled);
        txtOpenSubtitlesUsername.setText(settings.loginOpenSubtitlesUsername);
        txtOpenSubtitlesPassword.setText(settings.loginOpenSubtitlesPassword);
//        chkSourceSubscene.setSelected(settings.serieSourceSubscene);
        chkSourceLocal.setSelected(settings.serieSourceLocal);
        settings.localSourcesFolders.forEach(path -> localSourcesFoldersList.addItem(PathMatchType.FOLDER.image, path));
    }

    public void savePreferenceSettings() {
        Settings settings = settingsCtrl.settings;
        settings.serieSourceAddic7ed = chkSourceAddic7ed.isSelected();
        settings.loginAddic7edEnabled = chkUserAddic7edLogin.isSelected();
        settings.serieSourceAddic7edProxy = chkSourceAddic7edProxy.isSelected();
        settings.loginAddic7edUsername = txtAddic7edUsername.getText();
        settings.loginAddic7edPassword = new String(txtAddic7edPassword.getPassword());
        settings.serieSourceTvSubtitles = chkSourceTvSubtitles.isSelected();
        settings.serieSourcePodnapisi = chkSourcePodnapisi.isSelected();
        settings.serieSourceOpensubtitles = chkSourceOpenSubtitles.isSelected();
        settings.loginOpenSubtitlesEnabled = chkUserOpenSubtitlesLogin.isSelected();
        settings.loginOpenSubtitlesUsername = txtOpenSubtitlesUsername.getText();
        settings.loginOpenSubtitlesPassword = new String(txtOpenSubtitlesPassword.getPassword());
        settings.serieSourceSubscene = false; //chkSourceSubscene.isSelected();
        settings.serieSourceLocal = chkSourceLocal.isSelected();
        settings.localSourcesFolders = localSourcesFoldersList.stream().map(LabelPanel::getObject).toList();
    }

    private boolean hasValidSettingsAddic7ed() {
        return txtAddic7edUsername.hasValidValue() && txtAddic7edPassword.hasValidValue();
    }

    private boolean hasValidSettingsOpenSubtitles() {
        if (!txtOpenSubtitlesUsername.hasValidValue() || !txtOpenSubtitlesPassword.hasValidValue()) {
            return false;
        }
        if (chkUserOpenSubtitlesLogin.isSelected() &&
            !OpenSubtitlesApi.isValidCredentials(txtOpenSubtitlesUsername.getText(),
                new String(txtOpenSubtitlesPassword.getPassword()))) {
            txtOpenSubtitlesUsername.setErrorBorder();
            txtOpenSubtitlesPassword.setErrorBorder();
            return false;
        }
        return true;
    }

    @Override
    public boolean hasValidSettings() {
        return hasValidSettingsAddic7ed() && hasValidSettingsOpenSubtitles();
    }
}
