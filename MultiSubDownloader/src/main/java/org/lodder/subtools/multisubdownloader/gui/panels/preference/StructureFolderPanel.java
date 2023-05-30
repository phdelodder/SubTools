package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import java.io.Serial;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.dialog.StructureBuilderDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.PanelCheckBox;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcheckbox.JCheckBoxExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcombobox.MyComboBox;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.JTextFieldExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyTextFieldPath;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.MyTextFieldString;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JTextFieldExtension.class, JComponentExtension.class, JCheckBoxExtension.class, AbstractButtonExtension.class })
public class StructureFolderPanel extends JPanel implements PreferencePanelIntf {

    @Serial
    private static final long serialVersionUID = 3476596236588408382L;
    private final LibrarySettings librarySettings;

    private final MyTextFieldPath txtLibraryFolder;
    private final MyTextFieldString txtFolderStructure;
    private final JCheckBox chkRemoveEmptyFolder;
    private final JCheckBox chkReplaceSpace;
    private final MyComboBox<String> cbxReplaceSpaceChar;

    public StructureFolderPanel(LibrarySettings librarySettings, VideoType videoType, Manager manager,
            UserInteractionHandler userInteractionHandler) {
        super(new MigLayout("insets 0, fill, nogrid"));
        this.librarySettings = librarySettings;

        JPanel titelPanel = TitlePanel.title(Messages.getString("PreferenceDialog.MoveToLibrary"))
                .margin(0).padding(0).marginLeft(20).paddingLeft(20).useGrid()
                .panelColumnConstraints("[shrink][grow][shrink]").addTo(this, "span, grow");

        {
            new JLabel(Messages.getString("PreferenceDialog.Location")).addTo(titelPanel, "shrink");
            this.txtLibraryFolder = MyTextFieldPath.builder().requireValue().build().withColumns(20).addTo(titelPanel, "grow");
            new JButton(Messages.getString("App.Browse"))
                    .withActionListener(() -> MemoryFolderChooser.getInstance()
                            .selectDirectory(getRootPane(), Messages.getString("PreferenceDialog.LibraryFolder"))
                            .ifPresent(txtLibraryFolder::setObject))
                    .addTo(titelPanel, "shrink, wrap");

            new JLabel(Messages.getString("StructureBuilderDialog.Structure")).addTo(titelPanel, "shrink");
            this.txtFolderStructure =
                    MyTextFieldString.builder().requireValue().build().withColumns(20).withDisabled().addTo(titelPanel, "grow");
            JButton btnStructure = new JButton(Messages.getString("StructureBuilderDialog.Structure"))
                    .withActionListener(() -> {
                        StructureBuilderDialog sDialog = new StructureBuilderDialog(null,
                                Messages.getString("PreferenceDialog.StructureBuilderTitle"),
                                true, videoType, StructureBuilderDialog.StructureType.FOLDER, librarySettings, manager, userInteractionHandler);
                        String value = sDialog.showDialog(txtFolderStructure.getText());
                        if (!"".equals(value)) {
                            txtFolderStructure.setText(value);
                        }

                    })
                    .withDisabled()
                    .addTo(titelPanel, "shrink, wrap");

            this.chkRemoveEmptyFolder = new JCheckBox(Messages.getString("PreferenceDialog.RemoveEmptyFolders")).addTo(titelPanel, "span, wrap");

            PanelCheckBox.checkbox(this.chkReplaceSpace = new JCheckBox(Messages.getString("PreferenceDialog.ReplaceSpaceWith")))
                    .panelOnSameLine().addTo(titelPanel, "span")
                    .addComponent(this.cbxReplaceSpaceChar = MyComboBox.ofValues("-", ".", "_").withDisabled());

            // behaviour
            txtLibraryFolder.withValidityChangedCallback(txtFolderStructure::setEnabled, btnStructure::setEnabled);
        }

        loadPreferenceSettings();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtLibraryFolder.refreshState();
        txtFolderStructure.refreshState();
    }

    public void loadPreferenceSettings() {
        txtLibraryFolder.setObject(librarySettings.getLibraryFolder());
        txtFolderStructure.setText(librarySettings.getLibraryFolderStructure());
        chkRemoveEmptyFolder.setSelected(librarySettings.isLibraryRemoveEmptyFolders());
        chkReplaceSpace.setSelected(librarySettings.isLibraryFolderReplaceSpace());
        cbxReplaceSpaceChar.setSelectedItem(librarySettings.getLibraryFolderReplacingSpaceSign());
    }

    public void savePreferenceSettings() {
        librarySettings
                .setLibraryFolder(txtLibraryFolder.getObject())
                .setLibraryFolderStructure(txtFolderStructure.getText())
                .setLibraryRemoveEmptyFolders(chkRemoveEmptyFolder.isSelected())
                .setLibraryFolderReplaceSpace(chkReplaceSpace.isSelected())
                // if (pnlStructureFolder.isReplaceSpaceSelected()) {
                .setLibraryFolderReplacingSpaceSign(cbxReplaceSpaceChar.getSelectedItem());
        // }
    }

    @Override
    public boolean hasValidSettings() {
        return !isEnabled() || (txtLibraryFolder.hasValidValue() && txtFolderStructure.hasValidValue());
    }
}
