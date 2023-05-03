package org.lodder.subtools.multisubdownloader.gui.panels;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.dialog.StructureBuilderDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.PartialDisableComboBox;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Component;
import java.awt.HeadlessException;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ Files.class })
public abstract class VideoLibraryPanel extends JPanel {

    private static final long serialVersionUID = -9175813173306481849L;
    private LibrarySettings libSettings;
    protected StructureFolderPanel pnlStructureFolder;
    protected StructureFilePanel pnlStructureFile;
    private final VideoType videoType;
    private JComboBox<LibraryActionType> cbxLibraryAction;
    private JCheckBox chkReplaceWindowsChar;
    private JCheckBox chkUseTVDBNaming;
    private PartialDisableComboBox cbxLibraryOtherFileAction;
    private SubtitleBackupPanel pnlBackup;
    private final Manager manager;
    private final Boolean renameMode;
    private final UserInteractionHandler userInteractionHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoLibraryPanel.class);

    public VideoLibraryPanel(LibrarySettings libSettings, VideoType videoType, Manager manager, Boolean renameMode,
            UserInteractionHandler userInteractionHandler) {
        this.videoType = videoType;
        this.manager = manager;
        this.renameMode = renameMode;
        this.userInteractionHandler = userInteractionHandler;
        initialize_ui();
        setLibrarySettings(libSettings);
        // repaint();
    }

    private void checkPosibleOtherFileActions() {
        LibraryActionType libraryActionType = (LibraryActionType) cbxLibraryAction.getSelectedItem();
        if (libraryActionType != null) {
            for (int i = 0; i < cbxLibraryOtherFileAction.getModel().getSize(); i++) {
                LibraryOtherFileActionType ofa =
                        (LibraryOtherFileActionType) cbxLibraryOtherFileAction.getItemAt(i);
                if (LibraryActionType.MOVE.equals(libraryActionType)) {
                    cbxLibraryOtherFileAction.setItemEnabled(i, !LibraryOtherFileActionType.MOVEANDRENAME.equals(ofa)
                            && !LibraryOtherFileActionType.RENAME.equals(ofa));
                } else if (LibraryActionType.RENAME.equals(libraryActionType)) {
                    cbxLibraryOtherFileAction.setItemEnabled(i, !LibraryOtherFileActionType.MOVEANDRENAME.equals(ofa)
                            && !LibraryOtherFileActionType.MOVE.equals(ofa));
                } else if (LibraryActionType.MOVEANDRENAME.equals(libraryActionType)) {
                    // no disable needed
                    cbxLibraryOtherFileAction.setItemEnabled(i, true);
                } else {
                    cbxLibraryOtherFileAction.setItemEnabled(i, LibraryOtherFileActionType.NOTHING.equals(ofa));
                }
            }
        }
    }

    private void checkEnableStatusPanel() {
        LibraryActionType libraryActionType = (LibraryActionType) cbxLibraryAction.getSelectedItem();
        if (libraryActionType != null) {
            if (LibraryActionType.MOVE.equals(libraryActionType)) {
                checkEnableStatus(pnlStructureFile, false);
                checkEnableStatus(pnlStructureFolder, true);
            } else if (LibraryActionType.RENAME.equals(libraryActionType)) {
                checkEnableStatus(pnlStructureFile, true);
                checkEnableStatus(pnlStructureFolder, false);
            } else if (LibraryActionType.MOVEANDRENAME.equals(libraryActionType)) {
                checkEnableStatus(pnlStructureFile, true);
                checkEnableStatus(pnlStructureFolder, true);
            } else {
                checkEnableStatus(pnlStructureFile, false);
                checkEnableStatus(pnlStructureFolder, false);
            }
        }
    }

    private void checkEnableStatus(JPanel panel, boolean status) {
        for (Component c : panel.getComponents()) {
            if (!(VideoType.MOVIE == videoType && c.equals(chkUseTVDBNaming))) {
                if (c instanceof JTextField textField && textField.getText().isEmpty()
                        && VideoType.MOVIE == videoType && !textField.equals(pnlStructureFolder.getStructure())
                        && !textField.equals(pnlStructureFile.getTxtDefaultNlText())
                        && !textField.equals(pnlStructureFile.getTxtDefaultEnText())) {
                    c.setVisible(false);
                } else if (c instanceof JButton && c.equals(pnlStructureFile.getBtnBuildStructure()) && VideoType.MOVIE == videoType) {
                    c.setVisible(false);
                } else {
                    c.setVisible(status);
                }
                if (c instanceof JCheckBox checkBox && checkBox.isSelected()) {
                    checkBox.setSelected(status);
                }
            }
        }
    }

    public boolean isValidPanelValues() {
        LibraryActionType libraryActionType = (LibraryActionType) cbxLibraryAction.getSelectedItem();
        if (libraryActionType != null &&
                (LibraryActionType.MOVEANDRENAME.equals(libraryActionType) || LibraryActionType.MOVE.equals(libraryActionType))) {
            if (!pnlStructureFolder.getLibraryFolder().isEmpty()) {
                Path f = Path.of(pnlStructureFolder.getLibraryFolder());
                try {
                    if (!f.toAbsolutePath().isDirectory()) {
                        String message = Messages.getString("PreferenceDialog.ErrorInvalidPath", "'%s - %s'"
                                .formatted(Messages.getString("PreferenceDialog.MoveToLibrary"), Messages.getString("PreferenceDialog.Location")));
                        JOptionPane.showConfirmDialog(this, message, "MultiSubDownloader", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
                        LOGGER.debug("isValidPanelValues: " + message);
                        return false;
                    }
                } catch (HeadlessException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    public void setLibrarySettings(final LibrarySettings libSettings) {
        this.libSettings = libSettings;

        if (!renameMode) {
            pnlBackup.setBackupSubtitleSelected(libSettings.isLibraryBackupSubtitle());
            pnlBackup.setBackupSubtitlePath(libSettings.getLibraryBackupSubtitlePath() == null ? null
                    : libSettings.getLibraryBackupSubtitlePath().toAbsolutePath().toString());
            pnlBackup.setBackupUseWebsiteFilenameSelected(libSettings.isLibraryBackupUseWebsiteFileName());
        }
        this.cbxLibraryAction.setSelectedItem(libSettings.getLibraryAction());
        this.chkUseTVDBNaming.setSelected(libSettings.isLibraryUseTVDBNaming());
        this.chkReplaceWindowsChar.setSelected(libSettings.isLibraryReplaceChars());
        this.cbxLibraryOtherFileAction.setSelectedItem(libSettings.getLibraryOtherFileAction());

        if (libSettings.getLibraryFolder() != null) {
            pnlStructureFolder
                    .setLibraryFolder(libSettings.getLibraryFolder() == null ? null : libSettings.getLibraryFolder().toAbsolutePath().toString());
        }
        pnlStructureFolder.getStructure().setText(libSettings.getLibraryFolderStructure());
        pnlStructureFolder.setRemoveEmptyFolderSelected(libSettings.isLibraryRemoveEmptyFolders());

        pnlStructureFile.setFileStructure(libSettings.getLibraryFilenameStructure());
        pnlStructureFile.setReplaceSpaceSelected(libSettings.isLibraryFilenameReplaceSpace());
        pnlStructureFile.setReplaceSpaceChar(libSettings.getLibraryFilenameReplacingSpaceSign());
        pnlStructureFolder.setReplaceSpaceSelected(libSettings.isLibraryFolderReplaceSpace());
        pnlStructureFolder.setReplaceSpaceChar(libSettings.getLibraryFolderReplacingSpaceSign());
        pnlStructureFile.setIncludeLanguageCodeSelected(libSettings.isLibraryIncludeLanguageCode());
        pnlStructureFile.getTxtDefaultEnText().setText(libSettings.getDefaultEnText());
        pnlStructureFile.getTxtDefaultNlText().setText(libSettings.getDefaultNlText());

        checkEnableStatusPanel();
        checkPosibleOtherFileActions();
    }

    public LibrarySettings getLibrarySettings() {
        if (!renameMode) {
            this.libSettings.setLibraryBackupSubtitle(pnlBackup.isBackupSubtitleSelected());
            this.libSettings.setLibraryBackupSubtitlePath(
                    StringUtils.isBlank(pnlBackup.getBackupSubtitlePath()) ? null : Path.of(pnlBackup.getBackupSubtitlePath()));
            this.libSettings.setLibraryBackupUseWebsiteFileName(pnlBackup.isBackupUseWebsiteFilenameSelected());
        }
        this.libSettings.setLibraryAction((LibraryActionType) this.cbxLibraryAction.getSelectedItem());
        this.libSettings.setLibraryUseTVDBNaming(this.chkUseTVDBNaming.isSelected());
        this.libSettings.setLibraryReplaceChars(this.chkReplaceWindowsChar.isSelected());
        this.libSettings.setLibraryOtherFileAction((LibraryOtherFileActionType) this.cbxLibraryOtherFileAction.getSelectedItem());

        this.libSettings.setLibraryFolder(
                StringUtils.isBlank(pnlStructureFolder.getLibraryFolder()) ? null : Path.of(pnlStructureFolder.getLibraryFolder()));
        this.libSettings.setLibraryFolderStructure(pnlStructureFolder.getStructure().getText());
        this.libSettings.setLibraryRemoveEmptyFolders(pnlStructureFolder.isRemoveEmptyFolderSelected());

        this.libSettings.setLibraryFilenameStructure(pnlStructureFile.getFileStructure());
        this.libSettings.setLibraryFilenameReplaceSpace(pnlStructureFile.isReplaceSpaceSelected());
        if (pnlStructureFile.isReplaceSpaceSelected()) {
            this.libSettings.setLibraryFilenameReplacingSpaceSign(pnlStructureFile.getReplaceSpaceChar());
        }
        this.libSettings.setLibraryFolderReplaceSpace(pnlStructureFolder.isReplaceSpaceSelected());
        if (pnlStructureFolder.isReplaceSpaceSelected()) {
            this.libSettings.setLibraryFolderReplacingSpaceSign(pnlStructureFolder.getReplaceSpaceChar());
        }
        this.libSettings.setLibraryIncludeLanguageCode(pnlStructureFile.isIncludeLanguageCodeSelected());
        this.libSettings.setDefaultEnText(pnlStructureFile.getTxtDefaultEnText().getText());
        this.libSettings.setDefaultNlText(pnlStructureFile.getTxtDefaultNlText().getText());

        return libSettings;
    }

    private void initialize_ui() {
        setLayout(new MigLayout("", "[243.00,grow][grow]", "[][100px][][][][][125.00][]"));

        add(new JLabel(Messages.getString("PreferenceDialog.LibraryOptions")), "cell 0 0 2 1,gapy 5");
        add(new JSeparator(), "cell 0 0 2 1,growx,gapy 5");

        if (!renameMode) {
            createBackupPanel();
            add(pnlBackup, "cell 0 1 2 1,grow");
        }

        add(new JLabel(Messages.getString("PreferenceDialog.PerformActions")), "cell 0 2,alignx left");

        cbxLibraryAction = new JComboBox<>();
        cbxLibraryAction.setModel(new DefaultComboBoxModel<>(LibraryActionType.values()));
        cbxLibraryAction.addItemListener(arg0 -> {
            checkEnableStatusPanel();
            checkPosibleOtherFileActions();
            if (!cbxLibraryOtherFileAction.isItemEnabled(cbxLibraryOtherFileAction.getSelectedIndex())) {
                cbxLibraryOtherFileAction.setSelectedIndex(0);
            }
        });
        add(cbxLibraryAction, "cell 1 2,growx");

        chkReplaceWindowsChar = new JCheckBox(Messages.getString("PreferenceDialog.ReplaceInvalidWindowsChars"));
        add(chkReplaceWindowsChar, "cell 0 3 2 1");

        chkUseTVDBNaming = new JCheckBox(Messages.getString("PreferenceDialog.UseTvdbName"));
        if (VideoType.MOVIE.equals(videoType)) {
            chkUseTVDBNaming.setVisible(false);
        }
        add(chkUseTVDBNaming, "cell 0 4 2 1");

        add(new JLabel(Messages.getString("PreferenceDialog.ActionForOtherFiles")), "cell 0 5,alignx trailing");

        cbxLibraryOtherFileAction = new PartialDisableComboBox(LibraryOtherFileActionType.values());
        add(cbxLibraryOtherFileAction, "cell 1 5,growx");

        createStructureFolderPanel();
        add(pnlStructureFolder, "cell 0 6 2 1,grow");

        createStructureFilePanel();
        add(pnlStructureFile, "cell 0 7 2 1,grow");
    }

    private void createBackupPanel() {
        pnlBackup = new SubtitleBackupPanel();

        pnlBackup.setBrowseBackupAction(arg0 -> MemoryFolderChooser.getInstance()
                .selectDirectory(VideoLibraryPanel.this.getRootPane(), Messages.getString("PreferenceDialog.SubtitleBackupFolder"))
                .map(Path::toAbsolutePath).map(Path::toString).ifPresent(pnlBackup::setBackupSubtitlePath));
    }

    private void createStructureFolderPanel() {
        pnlStructureFolder = new StructureFolderPanel();

        pnlStructureFolder.setBrowseAction(arg0 -> MemoryFolderChooser.getInstance()
                .selectDirectory(VideoLibraryPanel.this.getRootPane(), Messages.getString("PreferenceDialog.LibraryFolder"))
                .map(Path::toAbsolutePath).map(Path::toString).ifPresent(pnlStructureFolder::setLibraryFolder));

        pnlStructureFolder.setBuildStructureAction(arg0 -> {
            final StructureBuilderDialog sDialog = new StructureBuilderDialog(null, Messages.getString("PreferenceDialog.StructureBuilderTitle"),
                    true, videoType, StructureBuilderDialog.StructureType.FOLDER, getLibrarySettings(), manager, userInteractionHandler);
            String value = sDialog.showDialog(pnlStructureFolder.getStructure().getText());
            if (!"".equals(value)) {
                pnlStructureFolder.getStructure().setText(value);
            }
        });
    }

    private void createStructureFilePanel() {
        pnlStructureFile = new StructureFilePanel();

        pnlStructureFile.setBuildStructureAction(arg0 -> {
            final StructureBuilderDialog sDialog = new StructureBuilderDialog(null, Messages.getString("PreferenceDialog.StructureBuilderTitle"),
                    true, videoType, StructureBuilderDialog.StructureType.FILE, getLibrarySettings(), manager, userInteractionHandler);
            String value = sDialog.showDialog(pnlStructureFile.getFileStructure());
            if (!value.isEmpty()) {
                pnlStructureFile.setFileStructure(value);
            }
        });
    }

}
