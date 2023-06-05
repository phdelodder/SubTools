package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import java.io.Serial;
import java.nio.file.Files;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.PartialDisableComboBox;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcheckbox.JCheckBoxExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcombobox.MyComboBox;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ Files.class, JComponentExtension.class, JCheckBoxExtension.class })
public abstract class VideoLibraryPanel extends JPanel implements PreferencePanelIntf {

    @Serial
    private static final long serialVersionUID = -9175813173306481849L;

    @Getter
    private final LibrarySettings librarySettings;

    protected StructureFolderPanel pnlStructureFolder;
    protected StructureFilePanel pnlStructureFile;
    private final MyComboBox<LibraryActionType> cbxLibraryAction;
    private final JCheckBox chkUseTVDBNaming;
    private final PartialDisableComboBox<LibraryOtherFileActionType> cbxLibraryOtherFileAction;
    private final SubtitleBackupPanel pnlBackup;

    public VideoLibraryPanel(LibrarySettings librarySettings, VideoType videoType, Manager manager, boolean renameMode,
            UserInteractionHandler userInteractionHandler) {
        super(new MigLayout("fillx, nogrid"));
        this.librarySettings = librarySettings;

        this.pnlBackup = renameMode ? null : new SubtitleBackupPanel(librarySettings).addTo(this, "wrap, span, growx");

        JPanel performActionPanel = TitlePanel.title(Messages.getString("PreferenceDialog.PerformActions"))
                .margin(0).padding(0).paddingLeft(20).addTo(this, "span, growx");
        {

            this.chkUseTVDBNaming = new JCheckBox(Messages.getString("PreferenceDialog.UseTvdbName"))
                    .visible(VideoType.EPISODE == videoType)
                    .addTo(performActionPanel, "hidemode 3, wrap");

            new JLabel(Messages.getString("PreferenceDialog.ActionForShowFiles")).addTo(performActionPanel);
            this.cbxLibraryAction = new MyComboBox<>(LibraryActionType.values())
                    .withToMessageStringRenderer(LibraryActionType::getMsgCode)
                    .addTo(performActionPanel, "wrap");

            this.pnlStructureFolder = new StructureFolderPanel(librarySettings, videoType, manager, userInteractionHandler)
                    .addTo(performActionPanel, "hidemode 3, wrap, span, growx");
            this.pnlStructureFile = new StructureFilePanel(librarySettings, videoType, manager, userInteractionHandler)
                    .addTo(performActionPanel, "hidemode 3, wrap, span, growx");

            JLabel lblActionForOtherFiles = new JLabel(Messages.getString("PreferenceDialog.ActionForOtherFiles")).addTo(performActionPanel);
            this.cbxLibraryOtherFileAction = PartialDisableComboBox.of(LibraryOtherFileActionType.values()).addTo(performActionPanel);

            //
            this.cbxLibraryAction.withSelectedItemConsumer(action -> {
                boolean enable = action != LibraryActionType.NOTHING;
                cbxLibraryOtherFileAction.setEnabled(enable);
                lblActionForOtherFiles.setEnabled(enable);
            });
        }

        this.cbxLibraryAction
                .withItemListener(() -> {
                    checkEnableStatusPanel();
                    checkPossibleOtherFileActions();
                    if (!cbxLibraryOtherFileAction.isItemEnabled(cbxLibraryOtherFileAction.getSelectedIndex())) {
                        cbxLibraryOtherFileAction.setSelectedIndex(0);
                    }
                });

        loadPreferenceSettings();
    }

    private void checkPossibleOtherFileActions() {
        LibraryActionType libraryActionType = cbxLibraryAction.getSelectedItem();
        for (int i = 0; i < cbxLibraryOtherFileAction.getModel().getSize(); i++) {
            LibraryOtherFileActionType ofa = cbxLibraryOtherFileAction.getItemAt(i);
            boolean enabled = switch (libraryActionType) {
                case MOVE -> LibraryOtherFileActionType.MOVEANDRENAME != ofa && LibraryOtherFileActionType.RENAME != ofa;
                case RENAME -> LibraryOtherFileActionType.MOVEANDRENAME != ofa && LibraryOtherFileActionType.MOVE != ofa;
                case MOVEANDRENAME -> true;
                case NOTHING -> LibraryOtherFileActionType.NOTHING == ofa;
            };
            cbxLibraryOtherFileAction.setItemEnabled(i, enabled);
        }
    }

    private void checkEnableStatusPanel() {
        LibraryActionType libraryActionType = cbxLibraryAction.getSelectedItem();
        boolean pnlStructureFileVisible = switch (libraryActionType) {
            case MOVE -> false;
            case RENAME -> true;
            case MOVEANDRENAME -> true;
            case NOTHING -> false;
        };
        boolean pnlStructureFolderVisible = switch (libraryActionType) {
            case MOVE -> true;
            case RENAME -> false;
            case MOVEANDRENAME -> true;
            case NOTHING -> false;
        };
        checkEnableStatus(pnlStructureFile, pnlStructureFileVisible);
        checkEnableStatus(pnlStructureFolder, pnlStructureFolderVisible);
    }

    private void checkEnableStatus(JPanel panel, boolean status) {
        panel.setEnabled(status);
        panel.setVisible(status);
    }

    public void loadPreferenceSettings() {
        cbxLibraryAction.setSelectedItem(librarySettings.getLibraryAction());
        chkUseTVDBNaming.setSelected(librarySettings.isLibraryUseTVDBNaming());
        cbxLibraryOtherFileAction.setSelectedItem(librarySettings.getLibraryOtherFileAction());

        pnlStructureFolder.loadPreferenceSettings();
        pnlStructureFile.loadPreferenceSettings();

        checkEnableStatusPanel();
        checkPossibleOtherFileActions();
    }

    public void savePreferenceSettings() {
        if (pnlBackup != null) {
            pnlBackup.savePreferenceSettings();
        }
        librarySettings.setLibraryAction(this.cbxLibraryAction.getSelectedItem())
                .setLibraryUseTVDBNaming(this.chkUseTVDBNaming.isSelected())
                .setLibraryOtherFileAction((LibraryOtherFileActionType) this.cbxLibraryOtherFileAction.getSelectedItem());

        pnlStructureFolder.savePreferenceSettings();
        pnlStructureFile.savePreferenceSettings();
    }

    @Override
    public boolean hasValidSettings() {
        return pnlStructureFolder.hasValidSettings() && pnlStructureFile.hasValidSettings() && pnlBackup.hasValidSettings();
    }

}
