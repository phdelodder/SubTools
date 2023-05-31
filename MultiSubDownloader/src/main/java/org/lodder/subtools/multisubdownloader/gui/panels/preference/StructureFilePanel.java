package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import java.io.Serial;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.dialog.StructureBuilderDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.PanelCheckBox;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcheckbox.JCheckBoxExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcombobox.MyComboBox;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.JTextFieldExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyTextFieldString;
import org.lodder.subtools.multisubdownloader.lib.library.FilenameLibraryCommonBuilder;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryBuilder;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JTextFieldExtension.class, JComponentExtension.class, JCheckBoxExtension.class, AbstractButtonExtension.class })
public class StructureFilePanel extends JPanel {

    @Serial
    private static final long serialVersionUID = -5458593307643063563L;
    private final LibrarySettings librarySettings;

    private final MyTextFieldString txtFileStructure;
    private final JCheckBox chkReplaceSpace;
    private final MyComboBox<String> cbxReplaceSpaceChar;
    private final JCheckBox chkIncludeLanguageCode;
    private final JTextField txtDefaultNlText;
    private final JTextField txtDefaultEnText;

    public StructureFilePanel(LibrarySettings librarySettings, VideoType videoType, Manager manager,
            UserInteractionHandler userInteractionHandler) {
        super(new MigLayout("insets 0, fill, nogrid"));
        this.librarySettings = librarySettings;

        JPanel titelPanel = TitlePanel.title(Messages.getString("PreferenceDialog.RenameFiles"))
                .margin(0).padding(0).marginLeft(20).paddingLeft(20).addTo(this, "span, grow");

        {
            new JLabel(Messages.getString("PreferenceDialog.Structure")).addTo(titelPanel, "shrink");
            this.txtFileStructure = MyTextFieldString.builder().requireValue().build().withColumns(20).addTo(titelPanel, "grow");
            new JButton(Messages.getString("StructureBuilderDialog.Structure"))
                    .withActionListener(() -> {
                        StructureBuilderDialog sDialog =
                                new StructureBuilderDialog(null, Messages.getString("PreferenceDialog.StructureBuilderTitle"), true, videoType,
                                        StructureBuilderDialog.StructureType.FILE, manager, userInteractionHandler,
                                        getLibraryStructureBuilder(manager, userInteractionHandler));
                        String value = sDialog.showDialog(txtFileStructure.getText());
                        if (!value.isEmpty()) {
                            txtFileStructure.setText(value);
                        }

                    })
                    .addTo(titelPanel, "shrink, wrap");

            this.chkReplaceSpace = new JCheckBox(Messages.getString("PreferenceDialog.ReplaceSpaceWith"));

            PanelCheckBox.checkbox(chkReplaceSpace).panelOnSameLine().addTo(titelPanel, "wrap")
                    .addComponent("width pref+10px, wrap", this.cbxReplaceSpaceChar = MyComboBox.ofValues("-", ".", "_").withDisabled());

            this.chkIncludeLanguageCode = new JCheckBox(Messages.getString("PreferenceDialog.IncludeLanguageInFileName")).addTo(titelPanel, "wrap");

            PanelCheckBox.checkbox(chkIncludeLanguageCode)
                    .panelOnNewLine().panelLayout(new MigLayout("insets 0, novisualpadding")).addTo(titelPanel, "span, growx")
                    .addComponent("shrink", new JLabel("Nederlands").withEnabled(false))
                    .addComponent("grow, wrap", this.txtDefaultNlText = new JTextField().withColumns(10).withEnabled(false))
                    .addComponent("shrink", new JLabel("English").withEnabled(false))
                    .addComponent("grow, wrap", this.txtDefaultEnText = new JTextField().withColumns(10).withEnabled(false));
        }

        loadPreferenceSettings();
    }

    private Function<String, LibraryBuilder> getLibraryStructureBuilder(Manager manager, UserInteractionHandler userInteractionHandler) {
        return filenameStructure -> new FilenameLibraryCommonBuilder(manager, userInteractionHandler) {

            @Override
            protected boolean isUseTVDBNaming() {
                return false;
            }

            @Override
            protected boolean isReplaceChars() {
                return false;
            }

            @Override
            protected boolean isIncludeLanguageCode() {
                return chkIncludeLanguageCode.isSelected();
            }

            @Override
            protected boolean isFilenameReplaceSpace() {
                return chkReplaceSpace.isSelected();
            }

            @Override
            protected boolean hasAnyLibraryAction(LibraryActionType... libraryActions) {
                return true;
            }

            @Override
            protected String getFilenameStructure() {
                return filenameStructure;
            }

            @Override
            protected String getFilenameReplacingSpaceSign() {
                return cbxReplaceSpaceChar.getSelectedItem();
            }

            @Override
            protected String getDefaultNlText() {
                return txtDefaultNlText.getText();
            }

            @Override
            protected String getDefaultEnText() {
                return txtDefaultEnText.getText();
            }
        };

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtFileStructure.refreshState();
    }

    public void loadPreferenceSettings() {
        txtFileStructure.setText(librarySettings.getLibraryFilenameStructure());
        chkReplaceSpace.setSelected(librarySettings.isLibraryFilenameReplaceSpace());
        cbxReplaceSpaceChar.setSelectedItem(librarySettings.getLibraryFilenameReplacingSpaceSign());
        chkIncludeLanguageCode.setSelected(librarySettings.isLibraryIncludeLanguageCode());
        txtDefaultNlText.setText(librarySettings.getDefaultNlText());
        txtDefaultEnText.setText(librarySettings.getDefaultEnText());
    }

    public void savePreferenceSettings() {
        librarySettings
                .setLibraryFilenameStructure(txtFileStructure.getText())
                .setLibraryFilenameReplaceSpace(chkReplaceSpace.isSelected())
                .setLibraryFilenameReplacingSpaceSign(cbxReplaceSpaceChar.getSelectedItem())
                .setLibraryIncludeLanguageCode(chkIncludeLanguageCode.isSelected())
                .setDefaultNlText(txtDefaultNlText.getText())
                .setDefaultEnText(txtDefaultEnText.getText());
    }

    public boolean hasValidSettings() {
        return !isVisible() || txtFileStructure.hasValidValue();
    }
}
