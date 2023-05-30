package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import java.io.Serial;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.PanelCheckBox;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcheckbox.JCheckBoxExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcombobox.MyComboBox;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jslider.JSliderExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.JTextFieldExtension;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JTextFieldExtension.class, JCheckBoxExtension.class, JComponentExtension.class, JSliderExtension.class })
public class OptionsPanel extends JPanel implements PreferencePanelIntf {

    @Serial
    private static final long serialVersionUID = -5458593307643063563L;

    private final SettingsControl settingsCtrl;

    private final JCheckBox chkAlwaysConfirm;
    private final JCheckBox chkMinScoreSelection;
    private final JSlider sldMinScoreSelection;
    private final JCheckBox chkDefaultSelection;
    private final DefaultSelectionPanel pnlDefaultSelection;
    private final JCheckBox chkSubtitleExactMethod;
    private final JCheckBox chkSubtitleKeywordMethod;
    private final JCheckBox chkExcludeHearingImpaired;
    private final JCheckBox chkOnlyFound;
    private final JCheckBox chkStopOnSearchError;
    private final MyComboBox<SettingsProcessEpisodeSource> cbxEpisodeProcessSource;
    private final JCheckBox chkConfirmProviderMapping;

    public OptionsPanel(SettingsControl settingsCtrl) {
        super(new MigLayout("insets 0, fill, nogrid"));
        this.settingsCtrl = settingsCtrl;

        TitlePanel.title(Messages.getString("PreferenceDialog.DownloadOptions"))
                .marginBottom(0).padding(0).paddingLeft(20).addTo(this, "span, grow, wrap")
                .addComponent(this.chkAlwaysConfirm = new JCheckBox(Messages.getString("PreferenceDialog.CheckBeforeDownloading")), "wrap")
                .addComponent("wrap, grow", PanelCheckBox
                        .checkbox(this.chkMinScoreSelection = new JCheckBox(Messages.getString("PreferenceDialog.MinAutomaticScoreSelection")))
                        .panelOnSameLine().build()
                        .addComponent(this.sldMinScoreSelection = new JSlider().withMinimum(0).withMaximum(100).withDisabled(), "wrap"))
                .addComponent("wrap, grow",
                        PanelCheckBox
                                .checkbox(this.chkDefaultSelection =
                                        new JCheckBox(Messages.getString("PreferenceDialog.DefaultSelection"), null, true))
                                .panelOnNewLine().build()
                                .addComponent(this.pnlDefaultSelection = new DefaultSelectionPanel(settingsCtrl).withDisabled()));

        TitlePanel.title(Messages.getString("PreferenceDialog.SearchFilter"))
                .marginBottom(0).padding(0).paddingLeft(20).addTo(this, "span, grow, wrap")
                .addComponent(this.chkSubtitleExactMethod = new JCheckBox(Messages.getString("PreferenceDialog.SearchFilterExact")), "wrap")
                .addComponent(this.chkSubtitleKeywordMethod = new JCheckBox(Messages.getString("PreferenceDialog.SearchFilterKeyword")), "wrap")
                .addComponent(this.chkExcludeHearingImpaired = new JCheckBox(Messages.getString("PreferenceDialog.ExcludeHearingImpaired")));

        TitlePanel.title(Messages.getString("PreferenceDialog.TableOptions"))
                .marginBottom(0).padding(0).paddingLeft(20).addTo(this, "span, grow, wrap")
                .addComponent(this.chkOnlyFound = new JCheckBox(Messages.getString("PreferenceDialog.ShowOnlyFound")));

        TitlePanel.title(Messages.getString("PreferenceDialog.ErrorHandlingOption"))
                .marginBottom(0).padding(0).paddingLeft(20).addTo(this, "span, grow, wrap")
                .addComponent(this.chkStopOnSearchError = new JCheckBox(Messages.getString("PreferenceDialog.StopAfterError")));

        TitlePanel.title(Messages.getString("PreferenceDialog.SerieDatabaseSource"))
                .marginBottom(0).padding(0).paddingLeft(20).addTo(this, "span, grow")
                .addComponent(this.cbxEpisodeProcessSource = MyComboBox.ofValues(SettingsProcessEpisodeSource.values()), "wrap")
                .addComponent(this.chkConfirmProviderMapping = new JCheckBox(Messages.getString("PreferenceDialog.ConfirmProviderMapping")));

        loadPreferenceSettings();
    }

    public void loadPreferenceSettings() {
        chkAlwaysConfirm.setSelected(settingsCtrl.getSettings().isOptionsAlwaysConfirm());
        chkMinScoreSelection.setSelected(settingsCtrl.getSettings().isOptionsMinAutomaticSelection());
        sldMinScoreSelection.setValue(settingsCtrl.getSettings().getOptionsMinAutomaticSelectionValue());
        chkDefaultSelection.setSelected(settingsCtrl.getSettings().isOptionsDefaultSelection());
        chkSubtitleExactMethod.setSelected(settingsCtrl.getSettings().isOptionSubtitleExactMatch());
        chkSubtitleKeywordMethod.setSelected(settingsCtrl.getSettings().isOptionSubtitleKeywordMatch());
        chkExcludeHearingImpaired.setSelected(settingsCtrl.getSettings().isOptionSubtitleExcludeHearingImpaired());
        chkOnlyFound.setSelected(settingsCtrl.getSettings().isOptionsShowOnlyFound());
        chkStopOnSearchError.setSelected(settingsCtrl.getSettings().isOptionsStopOnSearchError());
        cbxEpisodeProcessSource.setSelectedItem(settingsCtrl.getSettings().getProcessEpisodeSource());
        chkConfirmProviderMapping.setSelected(settingsCtrl.getSettings().isOptionsConfirmProviderMapping());
    }

    public void savePreferenceSettings() {
        settingsCtrl.getSettings()
                .setOptionsAlwaysConfirm(chkAlwaysConfirm.isSelected())
                .setOptionsMinAutomaticSelection(chkMinScoreSelection.isSelected())
                .setOptionsMinAutomaticSelectionValue(sldMinScoreSelection.getValue())
                .setOptionsDefaultSelection(chkDefaultSelection.isSelected())
                .setOptionSubtitleExactMatch(chkSubtitleExactMethod.isSelected())
                .setOptionSubtitleKeywordMatch(chkSubtitleKeywordMethod.isSelected())
                .setOptionSubtitleExcludeHearingImpaired(chkExcludeHearingImpaired.isSelected())
                .setOptionsShowOnlyFound(chkOnlyFound.isSelected())
                .setOptionsStopOnSearchError(chkStopOnSearchError.isSelected())
                .setProcessEpisodeSource(cbxEpisodeProcessSource.getSelectedItem())
                .setOptionsConfirmProviderMapping(chkConfirmProviderMapping.isSelected());
        pnlDefaultSelection.savePreferenceSettings();
    }

    @Override
    public boolean hasValidSettings() {
        return pnlDefaultSelection.hasValidSettings();
    }
}
