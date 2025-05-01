package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import java.io.Serial;

import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.multisubdownloader.gui.extra.BoxModelProperties;
import org.lodder.subtools.multisubdownloader.gui.extra.PanelCheckBox;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.SettingsProcessEpisodeSource;

public class OptionsPanel extends JPanel implements PreferencePanelIntf {

    @Serial private static final long serialVersionUID = -5458593307643063563L;

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
    private final JComboBox<SettingsProcessEpisodeSource> cbxEpisodeProcessSource;
    private final JCheckBox chkConfirmProviderMapping;

    public OptionsPanel(SettingsControl settingsCtrl) {
        super(new MigLayout("insets 0, fill, nogrid"));
        this.settingsCtrl = settingsCtrl;

        new TitlePanel(
            title:getText("PreferenceDialog.DownloadOptions"),
            margin:new BoxModelProperties(null, null, 0, null),
            padding:new BoxModelProperties(0, 20, 0, 0))
            .addToPanel(this, "span, grow, wrap")
            .addComponent(this.chkAlwaysConfirm =
                new JCheckBox(getText("PreferenceDialog.CheckBeforeDownloading")), "wrap")
            .addComponent("wrap, grow",
                new PanelCheckBox(
                    checkbox:this.chkMinScoreSelection =
                        new JCheckBox(getText("PreferenceDialog.MinAutomaticScoreSelection")),
                    panelOnNewLine:false
                    )
                    .addComponent(this.sldMinScoreSelection = new JSlider().minimum(0).maximum(100), "wrap"))
            .addComponent("wrap, grow",
                new PanelCheckBox(
                    checkbox:this.chkDefaultSelection =
                        new JCheckBox(getText("PreferenceDialog.DefaultSelection"), null, true),
                    panelOnNewLine:true
                    )
                    .addComponent(this.pnlDefaultSelection = new DefaultSelectionPanel(settingsCtrl)));

        new TitlePanel(
            title:getText("PreferenceDialog.SearchFilter"),
            margin:new BoxModelProperties(null, null, 0, null),
            padding:new BoxModelProperties(0, 20, 0, 0))
            .addToPanel(this, "span, grow, wrap")
            .addComponent(
                this.chkSubtitleExactMethod = new JCheckBox(getText("PreferenceDialog.SearchFilterExact")),
                "wrap")
            .addComponent(this.chkSubtitleKeywordMethod =
                new JCheckBox(getText("PreferenceDialog.SearchFilterKeyword")), "wrap")
            .addComponent(this.chkExcludeHearingImpaired =
                new JCheckBox(getText("PreferenceDialog.ExcludeHearingImpaired")));

        new TitlePanel(
            title:getText("PreferenceDialog.TableOptions"),
            margin:new BoxModelProperties(null, null, 0, null),
            padding:new BoxModelProperties(0, 20, 0, 0))
            .addToPanel(this, "span, grow, wrap")
            .addComponent(this.chkOnlyFound = new JCheckBox(getText("PreferenceDialog.ShowOnlyFound")));

        new TitlePanel(
            title:getText("PreferenceDialog.ErrorHandlingOption"),
            margin:new BoxModelProperties(null, null, 0, null),
            padding:new BoxModelProperties(0, 20, 0, 0))
            .addToPanel(this, "span, grow, wrap")
            .addComponent(this.chkStopOnSearchError = new JCheckBox(getText("PreferenceDialog.StopAfterError")));

        new TitlePanel(
            title:getText("PreferenceDialog.SerieDatabaseSource"),
            margin:new BoxModelProperties(null, null, 0, null),
            padding:new BoxModelProperties(0, 20, 0, 0))
            .addToPanel(this, "span, grow")
            .addComponent(this.cbxEpisodeProcessSource = new JComboBox<>(SettingsProcessEpisodeSource.values()),
                "wrap")
            .addComponent(this.chkConfirmProviderMapping =
                new JCheckBox(getText("PreferenceDialog.ConfirmProviderMapping")));

        loadPreferenceSettings();
    }

    public void loadPreferenceSettings() {
        chkAlwaysConfirm.setSelected(settingsCtrl.settings.optionsAlwaysConfirm);
        chkMinScoreSelection.setSelected(settingsCtrl.settings.optionsMinAutomaticSelection);
        sldMinScoreSelection.setValue(settingsCtrl.settings.optionsMinAutomaticSelectionValue);
        chkDefaultSelection.setSelected(settingsCtrl.settings.optionsDefaultSelection);
        chkSubtitleExactMethod.setSelected(settingsCtrl.settings.optionSubtitleExactMatch);
        chkSubtitleKeywordMethod.setSelected(settingsCtrl.settings.optionSubtitleKeywordMatch);
        chkExcludeHearingImpaired.setSelected(settingsCtrl.settings.optionSubtitleExcludeHearingImpaired);
        chkOnlyFound.setSelected(settingsCtrl.settings.optionsShowOnlyFound);
        chkStopOnSearchError.setSelected(settingsCtrl.settings.optionsStopOnSearchError);
        cbxEpisodeProcessSource.setSelectedItem(settingsCtrl.settings.processEpisodeSource);
        chkConfirmProviderMapping.setSelected(settingsCtrl.settings.optionsConfirmProviderMapping);
    }

    public void savePreferenceSettings() {
        settingsCtrl.settings.optionsAlwaysConfirm = chkAlwaysConfirm.isSelected();
        settingsCtrl.settings.optionsMinAutomaticSelection = chkMinScoreSelection.isSelected();
        settingsCtrl.settings.optionsMinAutomaticSelectionValue = sldMinScoreSelection.getValue();
        settingsCtrl.settings.optionsDefaultSelection = chkDefaultSelection.isSelected();
        settingsCtrl.settings.optionSubtitleExactMatch = chkSubtitleExactMethod.isSelected();
        settingsCtrl.settings.optionSubtitleKeywordMatch = chkSubtitleKeywordMethod.isSelected();
        settingsCtrl.settings.optionSubtitleExcludeHearingImpaired = chkExcludeHearingImpaired.isSelected();
        settingsCtrl.settings.optionsShowOnlyFound = chkOnlyFound.isSelected();
        settingsCtrl.settings.optionsStopOnSearchError = chkStopOnSearchError.isSelected();
        settingsCtrl.settings.processEpisodeSource = cbxEpisodeProcessSource.getSelectedValue();
        settingsCtrl.settings.optionsConfirmProviderMapping = chkConfirmProviderMapping.isSelected();
        pnlDefaultSelection.savePreferenceSettings();
    }

    @Override
    public boolean hasValidSettings() {
        return pnlDefaultSelection.hasValidSettings();
    }
}
