package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import java.io.Serial;
import java.nio.file.Files;

import lombok.experimental.ExtensionMethod;
import manifold.ext.props.rt.api.val;
import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.multisubdownloader.gui.extra.BoxModelProperties;
import org.lodder.subtools.multisubdownloader.gui.extra.PartialDisableComboBox;
import org.lodder.subtools.multisubdownloader.gui.extra.TitlePanel;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

@ExtensionMethod({ Files.class })
public abstract sealed class VideoLibraryPanel extends JPanel implements PreferencePanelIntf
    permits EpisodeLibraryPanel, MovieLibraryPanel {

    @Serial private static final long serialVersionUID = -9175813173306481849L;

    @val LibrarySettings librarySettings;
    private final JComboBox<LibraryActionType> cbxLibraryAction;
    private final JCheckBox chkUseTVDBNaming;
    private final PartialDisableComboBox<LibraryOtherFileActionType> cbxLibraryOtherFileAction;
    private final SubtitleBackupPanel pnlBackup;
    protected final StructureFolderPanel pnlStructureFolder;
    protected final StructureFilePanel pnlStructureFile;

    VideoLibraryPanel(LibrarySettings librarySettings, VideoType videoType, Manager manager, boolean renameMode,
        UserInteractionHandler userInteractionHandler) {
        super(new MigLayout("fillx, nogrid"));
        this.librarySettings = librarySettings;

        this.pnlBackup = renameMode ? null : new SubtitleBackupPanel(librarySettings).addTo(this, "wrap, span, growx");

        JPanel performActionPanel = new TitlePanel(
            title:getText("PreferenceDialog.PerformActions"),
            margin:new BoxModelProperties(0),
            padding:new BoxModelProperties(0, 20, 0, 0))
            .addToPanel(this, "span, growx");
        {

            this.chkUseTVDBNaming = new JCheckBox(getText("PreferenceDialog.UseTvdbName")).visible(
                VideoType.EPISODE == videoType).addTo(performActionPanel, "hidemode 3, wrap");

            new JLabel(getText("PreferenceDialog.ActionForShowFiles")).addTo(performActionPanel);
            this.cbxLibraryAction = new JComboBox<>(LibraryActionType.values())
                .toMessageStringRenderer(LibraryActionType::getMsgCode)
                .addTo(performActionPanel, "wrap");

            this.pnlStructureFolder =
                new StructureFolderPanel(librarySettings, videoType, manager, userInteractionHandler)
                    .addTo(performActionPanel, "hidemode 3, wrap, span, growx");
            this.pnlStructureFile =
                new StructureFilePanel(librarySettings, videoType, manager, userInteractionHandler)
                    .addTo(performActionPanel, "hidemode 3, wrap, span, growx");

            JLabel lblActionForOtherFiles =
                new JLabel(getText("PreferenceDialog.ActionForOtherFiles")).addTo(performActionPanel);
            this.cbxLibraryOtherFileAction =
                PartialDisableComboBox.of(LibraryOtherFileActionType.values()).addTo(performActionPanel);

            //
            this.cbxLibraryAction.selectedItemConsumer(action -> {
                boolean enable = action != LibraryActionType.NOTHING;
                cbxLibraryOtherFileAction.setEnabled(enable);
                lblActionForOtherFiles.setEnabled(enable);
            });
        }

        this.cbxLibraryAction.itemListener(() -> {
            checkEnableStatusPanel();
            checkPossibleOtherFileActions();
            if (!cbxLibraryOtherFileAction.isItemEnabled(cbxLibraryOtherFileAction.getSelectedIndex())) {
                cbxLibraryOtherFileAction.setSelectedIndex(0);
            }
        });

        loadPreferenceSettings();
    }

    private void checkPossibleOtherFileActions() {
        LibraryActionType libraryActionType = cbxLibraryAction.getSelectedValue();
        for (int i = 0; i < cbxLibraryOtherFileAction.getModel().getSize(); i++) {
            LibraryOtherFileActionType ofa = cbxLibraryOtherFileAction.getItemAt(i);
            boolean enabled = switch (libraryActionType) {
                case MOVE ->
                    LibraryOtherFileActionType.MOVEANDRENAME != ofa && LibraryOtherFileActionType.RENAME != ofa;
                case RENAME ->
                    LibraryOtherFileActionType.MOVEANDRENAME != ofa && LibraryOtherFileActionType.MOVE != ofa;
                case MOVEANDRENAME -> true;
                case NOTHING -> LibraryOtherFileActionType.NOTHING == ofa;
            };
            cbxLibraryOtherFileAction.setItemEnabled(i, enabled);
        }
    }

    private void checkEnableStatusPanel() {
        LibraryActionType libraryActionType = cbxLibraryAction.getSelectedValue();
        boolean pnlStructureFileVisible = switch (libraryActionType) {
            case MOVE, NOTHING -> false;
            case RENAME, MOVEANDRENAME -> true;
        };
        boolean pnlStructureFolderVisible = switch (libraryActionType) {
            case MOVE, MOVEANDRENAME -> true;
            case RENAME, NOTHING -> false;
        };
        checkEnableStatus(pnlStructureFile, pnlStructureFileVisible);
        checkEnableStatus(pnlStructureFolder, pnlStructureFolderVisible);
    }

    private void checkEnableStatus(JPanel panel, boolean status) {
        panel.setEnabled(status);
        panel.setVisible(status);
    }

    public void loadPreferenceSettings() {
        cbxLibraryAction.setSelectedItem(librarySettings.action);
        chkUseTVDBNaming.setSelected(librarySettings.useTVDBNaming);
        cbxLibraryOtherFileAction.setSelectedItem(librarySettings.otherFileAction);

        checkEnableStatusPanel();
        checkPossibleOtherFileActions();
    }

    public void savePreferenceSettings() {
        if (pnlBackup != null) {
            pnlBackup.savePreferenceSettings();
        }
        librarySettings.action = this.cbxLibraryAction.getSelectedValue();
        librarySettings.useTVDBNaming = this.chkUseTVDBNaming.isSelected();
        librarySettings.otherFileAction = this.cbxLibraryOtherFileAction.getSelectedValue();

        pnlStructureFolder.savePreferenceSettings();
        pnlStructureFile.savePreferenceSettings();
    }

    @Override
    public boolean hasValidSettings() {
        return pnlStructureFolder.hasValidSettings() && pnlStructureFile.hasValidSettings() &&
            (pnlBackup == null || pnlBackup.hasValidSettings());
    }

}
