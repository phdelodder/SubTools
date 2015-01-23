package org.lodder.subtools.multisubdownloader.gui.panels;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import org.lodder.subtools.multisubdownloader.gui.dialog.StructureBuilderDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.MemoryFolderChooser;
import org.lodder.subtools.multisubdownloader.gui.extra.PartialDisableComboBox;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryActionType;
import org.lodder.subtools.multisubdownloader.lib.library.LibraryOtherFileActionType;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.VideoType;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

public abstract class VideoLibraryPanel extends JPanel {
  /**
     *
     */
  private static final long serialVersionUID = -9175813173306481849L;
  private LibrarySettings libSettings;
  protected StructureFolderPanel pnlStructureFolder;
  private JPanel pnlStructureFile;
  protected JCheckBox chkFilenameReplaceSpace;
  protected JCheckBox chkIncludeLanguageCode;
  private JTextField txtDefaultNlText;
  private JTextField txtDefaultEnText;
  private VideoType videoType;
  protected JTextField txtFileStructure;
  private JButton btnBuildStructureFile;
  private JComboBox<LibraryActionType> cbxLibraryAction;
  private JCheckBox chkReplaceWindowsChar;
  private JCheckBox chkUseTVDBNaming;
  private PartialDisableComboBox cbxLibraryOtherFileAction;
  private JComboBox<String> cbxFilenameReplaceSpaceChar;
  private SubtitleBackupPanel pnlBackup;

  public VideoLibraryPanel(LibrarySettings libSettings, VideoType videoType) {
    this.videoType = videoType;
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
        if (libraryActionType.equals(LibraryActionType.MOVE)) {
          if (ofa.equals(LibraryOtherFileActionType.MOVEANDRENAME)
              || ofa.equals(LibraryOtherFileActionType.RENAME)) {
            cbxLibraryOtherFileAction.setItemEnabled(i, false);
          } else {
            cbxLibraryOtherFileAction.setItemEnabled(i, true);
          }
        } else if (libraryActionType.equals(LibraryActionType.RENAME)) {
          if (ofa.equals(LibraryOtherFileActionType.MOVEANDRENAME)
              || ofa.equals(LibraryOtherFileActionType.MOVE)) {
            cbxLibraryOtherFileAction.setItemEnabled(i, false);
          } else {
            cbxLibraryOtherFileAction.setItemEnabled(i, true);
          }
        } else if (libraryActionType.equals(LibraryActionType.MOVEANDRENAME)) {
          // no disable needed
          cbxLibraryOtherFileAction.setItemEnabled(i, true);
        } else {
          if (ofa.equals(LibraryOtherFileActionType.NOTHING)) {
            cbxLibraryOtherFileAction.setItemEnabled(i, true);
          } else {
            cbxLibraryOtherFileAction.setItemEnabled(i, false);
          }
        }
      }
    }
  }

  private void checkEnableStatusPanel() {
    LibraryActionType libraryActionType = (LibraryActionType) cbxLibraryAction.getSelectedItem();
    if (libraryActionType != null) {
      if (libraryActionType.equals(LibraryActionType.MOVE)) {
        checkEnableStatus(pnlStructureFile, false);
        checkEnableStatus(pnlStructureFolder, true);
      } else if (libraryActionType.equals(LibraryActionType.RENAME)) {
        checkEnableStatus(pnlStructureFile, true);
        checkEnableStatus(pnlStructureFolder, false);
      } else if (libraryActionType.equals(LibraryActionType.MOVEANDRENAME)) {
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
        if (c instanceof JTextField && ((JTextField) c).getText().isEmpty()
            && VideoType.MOVIE == videoType && !c.equals(pnlStructureFolder.getStructure())
            && !c.equals(txtDefaultNlText) && !c.equals(txtDefaultEnText)) {
          c.setVisible(false);
        } else if (c instanceof JButton && c.equals(btnBuildStructureFile)
            && VideoType.MOVIE == videoType) {
          c.setVisible(false);
        } else {
          c.setVisible(status);
        }
        if (c instanceof JCheckBox && ((JCheckBox) c).isSelected())
          ((JCheckBox) c).setSelected(status);
      }
    }
  }

  public boolean isValidPanelValues() {
    LibraryActionType libraryActionType = (LibraryActionType) cbxLibraryAction.getSelectedItem();
    if (libraryActionType != null) {
      if (libraryActionType.equals(LibraryActionType.MOVEANDRENAME)
          || libraryActionType.equals(LibraryActionType.MOVE))
        if (!pnlStructureFolder.getLibraryFolder().isEmpty()) {
          File f = new File(pnlStructureFolder.getLibraryFolder());
          try {
            if (!f.getCanonicalFile().isDirectory()) {
              final String message =
                  "Geen geldig pad is ingegeven in 'Map - Locatie' op Bibliotheek info ";
              JOptionPane.showConfirmDialog(this, message, "MultiSubDownloader",
                  JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
              Logger.instance
                  .debug("isValidPanelValues: Geen geldig pad is ingegeven in 'Map - Locatie' op Bibliotheek info.");
              return false;
            }
          } catch (HeadlessException e) {
            Logger.instance.error(Logger.stack2String(e));
          } catch (IOException e) {
            Logger.instance.error(Logger.stack2String(e));
          }
        }
    }
    return true;
  }

  public void setLibrarySettings(final LibrarySettings libSettings) {
    this.libSettings = libSettings;

    pnlBackup.setBackupSubtitleSelected(libSettings.isLibraryBackupSubtitle());
    pnlBackup.setBackupSubtitlePath(libSettings.getLibraryBackupSubtitlePath().getAbsolutePath());
    pnlBackup.setBackupUseWebsiteFilenameSelected(libSettings.isLibraryBackupUseWebsiteFileName());
    this.cbxLibraryAction.setSelectedItem(libSettings.getLibraryAction());
    this.chkUseTVDBNaming.setSelected(libSettings.isLibraryUseTVDBNaming());
    this.chkReplaceWindowsChar.setSelected(libSettings.isLibraryReplaceChars());
    this.cbxLibraryOtherFileAction.setSelectedItem(libSettings.getLibraryOtherFileAction());

    if (libSettings.getLibraryFolder() != null) {
      pnlStructureFolder.setLibraryFolder(libSettings.getLibraryFolder().getAbsolutePath());
    }
    pnlStructureFolder.getStructure().setText(libSettings.getLibraryFolderStructure());
    pnlStructureFolder.setRemoveEmptyFolderSelected(libSettings.isLibraryRemoveEmptyFolders());

    this.txtFileStructure.setText(libSettings.getLibraryFilenameStructure());
    this.chkFilenameReplaceSpace.setSelected(libSettings.isLibraryFilenameReplaceSpace());
    this.cbxFilenameReplaceSpaceChar.setSelectedItem(libSettings
        .getLibraryFilenameReplacingSpaceSign());
    pnlStructureFolder.setFolderReplaceSpaceSelected(libSettings.isLibraryFolderReplaceSpace());
    pnlStructureFolder.setReplaceSpaceChar(libSettings.getLibraryFolderReplacingSpaceSign());
    this.chkIncludeLanguageCode.setSelected(libSettings.isLibraryIncludeLanguageCode());
    this.txtDefaultEnText.setText(libSettings.getDefaultEnText());
    this.txtDefaultNlText.setText(libSettings.getDefaultNlText());

    if (pnlStructureFolder.getLibraryFolder().isEmpty()
        && this.txtFileStructure.getText().isEmpty()
        && pnlStructureFolder.getStructure().getText().isEmpty()) initializeEmptyValues();
    checkEnableStatusPanel();
    checkPosibleOtherFileActions();
  }

  public LibrarySettings getLibrarySettings() {
    this.libSettings.setLibraryBackupSubtitle(pnlBackup.isBackupSubtitleSelected());
    this.libSettings.setLibraryBackupSubtitlePath(new File(pnlBackup.getBackupSubtitlePath()));
    this.libSettings.setLibraryBackupUseWebsiteFileName(pnlBackup
        .isBackupUseWebsiteFilenameSelected());
    this.libSettings.setLibraryAction((LibraryActionType) this.cbxLibraryAction.getSelectedItem());
    this.libSettings.setLibraryUseTVDBNaming(this.chkUseTVDBNaming.isSelected());
    this.libSettings.setLibraryReplaceChars(this.chkReplaceWindowsChar.isSelected());
    this.libSettings
        .setLibraryOtherFileAction((LibraryOtherFileActionType) this.cbxLibraryOtherFileAction
            .getSelectedItem());

    this.libSettings.setLibraryFolder(new File(pnlStructureFolder.getLibraryFolder()));
    this.libSettings.setLibraryFolderStructure(pnlStructureFolder.getStructure().getText());
    this.libSettings.setLibraryRemoveEmptyFolders(pnlStructureFolder.isRemoveEmptyFolderSelected());

    this.libSettings.setLibraryFilenameStructure(this.txtFileStructure.getText());
    this.libSettings.setLibraryFilenameReplaceSpace(this.chkFilenameReplaceSpace.isSelected());
    if (this.chkFilenameReplaceSpace.isSelected()) {
      this.libSettings
          .setLibraryFilenameReplacingSpaceSign((String) this.cbxFilenameReplaceSpaceChar
              .getSelectedItem());
    }
    this.libSettings
        .setLibraryFolderReplaceSpace(pnlStructureFolder.isFolderReplaceSpaceSelected());
    if (pnlStructureFolder.isFolderReplaceSpaceSelected()) {
      this.libSettings.setLibraryFolderReplacingSpaceSign(pnlStructureFolder.getReplaceSpaceChar());
    }
    this.libSettings.setLibraryIncludeLanguageCode(this.chkIncludeLanguageCode.isSelected());
    this.libSettings.setDefaultEnText(this.txtDefaultEnText.getText());
    this.libSettings.setDefaultNlText(this.txtDefaultNlText.getText());

    return libSettings;
  }

  protected abstract void initializeEmptyValues();

  private void initialize_ui() {
    setLayout(new MigLayout("", "[243.00,grow][grow]", "[][100px][][][][][125.00][]"));

    add(new JLabel("Bibiliotheek opties"), "cell 0 0 2 1,gapy 5");
    add(new JSeparator(), "cell 0 0 2 1,growx,gapy 5");

    createBackupPanel();
    add(pnlBackup, "cell 0 1 2 1,grow");

    add(new JLabel("Volgende acties uitvoeren:"), "cell 0 2,alignx left");

    cbxLibraryAction = new JComboBox<LibraryActionType>(LibraryActionType.values());
    // cbxLibraryAction = new JComboBox<LibraryActionType>();
    cbxLibraryAction.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
        checkEnableStatusPanel();
        checkPosibleOtherFileActions();
        if (!cbxLibraryOtherFileAction.isItemEnabled(cbxLibraryOtherFileAction.getSelectedIndex())) {
          cbxLibraryOtherFileAction.setSelectedIndex(0);
        }
      }
    });
    add(cbxLibraryAction, "cell 1 2,growx");

    chkReplaceWindowsChar = new JCheckBox("Ongeldige Windows karakters vervangen");
    add(chkReplaceWindowsChar, "cell 0 3 2 1");

    chkUseTVDBNaming =
        new JCheckBox(
            "Gebruik de benaming van TheTVDB in plaats van de serie naam in de bestandsnaam");
    if (videoType.equals(VideoType.MOVIE)) chkUseTVDBNaming.setVisible(false);
    add(chkUseTVDBNaming, "cell 0 4 2 1");

    add(new JLabel("Andere bestanden (nfo, jpg, sample, ...):"), "cell 0 5,alignx trailing");

    cbxLibraryOtherFileAction = new PartialDisableComboBox(LibraryOtherFileActionType.values());
    add(cbxLibraryOtherFileAction, "cell 1 5,growx");

    createStructureFolderPanel();
    add(pnlStructureFolder, "cell 0 6 2 1,grow");

    pnlStructureFile = new JPanel();
    pnlStructureFile.setLayout(new MigLayout("", "[][][][grow][]", "[][][][][][]"));
    add(pnlStructureFile, "cell 0 7 2 1,grow");

    pnlStructureFile.add(new JLabel("Bestanden hernoemen"), "cell 0 0 5 1,gapy 5");
    pnlStructureFile.add(new JSeparator(), "cell 0 0 5 1,growx,gapy 5");

    JLabel lblStructuur_1 = new JLabel("Structuur");
    pnlStructureFile.add(lblStructuur_1, "cell 1 1,alignx left");

    txtFileStructure = new JTextField();
    pnlStructureFile.add(txtFileStructure, "cell 2 1 2 1,growx");
    txtFileStructure.setColumns(10);

    btnBuildStructureFile = new JButton("Structuur");
    btnBuildStructureFile.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        final StructureBuilderDialog sDialog =
            new StructureBuilderDialog(null, "Structure Builder", true, videoType,
                StructureBuilderDialog.StrucutureType.FILE, getLibrarySettings());
        String value = sDialog.showDialog(txtFileStructure.getText());
        if (!value.isEmpty()) txtFileStructure.setText(value);
      }
    });
    pnlStructureFile.add(btnBuildStructureFile, "cell 4 1");

    chkFilenameReplaceSpace = new JCheckBox("Vervangen spatie door: ");
    pnlStructureFile.add(chkFilenameReplaceSpace, "cell 1 2 2 1");

    cbxFilenameReplaceSpaceChar = new JComboBox<String>();
    cbxFilenameReplaceSpaceChar.setModel(new DefaultComboBoxModel<String>(new String[] {"-", ".",
        "_"}));
    pnlStructureFile.add(cbxFilenameReplaceSpaceChar, "cell 3 2,growx");

    chkIncludeLanguageCode = new JCheckBox("Taal in de bestandsnaam van de ondertitel plaatsen");
    pnlStructureFile.add(chkIncludeLanguageCode, "cell 1 3 4 1");

    JLabel lblNederlands = new JLabel("Nederlands");
    pnlStructureFile.add(lblNederlands, "cell 1 4,alignx trailing");

    txtDefaultNlText = new JTextField();
    pnlStructureFile.add(txtDefaultNlText, "cell 2 4");
    txtDefaultNlText.setColumns(10);

    JLabel lblEnglish = new JLabel("English");
    pnlStructureFile.add(lblEnglish, "cell 1 5,alignx trailing");

    txtDefaultEnText = new JTextField();
    pnlStructureFile.add(txtDefaultEnText, "cell 2 5");
    txtDefaultEnText.setColumns(10);
  }

  private void createBackupPanel() {
    pnlBackup = new SubtitleBackupPanel();

    pnlBackup.setBrowseBackupAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        File path =
            MemoryFolderChooser.getInstance().selectDirectory(VideoLibraryPanel.this.getRootPane(),
                "Selecteer Ondertitel Backup map");
        pnlBackup.setBackupSubtitlePath(path.getAbsolutePath());
      }
    });
  }

  private void createStructureFolderPanel() {
    pnlStructureFolder = new StructureFolderPanel();

    pnlStructureFolder.setBrowseAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        File path =
            MemoryFolderChooser.getInstance().selectDirectory(VideoLibraryPanel.this.getRootPane(),
                "Selecteer Bibiliotheek map");
        pnlStructureFolder.setLibraryFolder(path.getAbsolutePath());
      }
    });

    pnlStructureFolder.setBuildStructureAction(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        final StructureBuilderDialog sDialog =
            new StructureBuilderDialog(null, "Structure Builder", true, videoType,
                StructureBuilderDialog.StrucutureType.FOLDER, getLibrarySettings());
        String value = sDialog.showDialog(pnlStructureFolder.getStructure().getText());
        if (!value.equals("")) pnlStructureFolder.getStructure().setText(value);
      }
    });
  }

}
