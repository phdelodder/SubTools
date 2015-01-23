package org.lodder.subtools.multisubdownloader.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class StructureFolderPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 3476596236588408382L;
  private JLabel lblLocatie;
  private JTextField txtLibraryFolder;
  private ActionListener browseAction;
  private JButton btnBrowse;
  private JLabel lblStructuur;
  private JTextField txtFolderStructure;
  private ActionListener buildStructureAction;
  private JButton btnBuildStructureFolder;
  private JCheckBox chkRemoveEmptyFolder;
  private JCheckBox chkFolderReplaceSpace;
  private JComboBox<String> cbxFolderReplaceSpaceChar;

  public StructureFolderPanel() {
    setLayout(new MigLayout("", "[][][][grow][center]", "[][][][][][]"));

    createComponents();
    setupListeners();
    addComponentsToPanel();
  }

  private void addComponentsToPanel() {
    add(new JLabel("Verplaatsen naar Bibiliotheek"), "cell 0 0 5 1,gapy 5");
    add(new JSeparator(), "cell 0 0 5 1,growx,gapy 5");
    add(lblLocatie, "cell 1 1,alignx left");
    add(txtLibraryFolder, "cell 2 1 2 1,growx");
    add(btnBrowse, "cell 4 1,alignx center");
    add(lblStructuur, "cell 1 2,alignx left");
    add(txtFolderStructure, "cell 2 2 2 1,growx");
    add(btnBuildStructureFolder, "cell 4 2,alignx center");
    add(chkRemoveEmptyFolder, "cell 1 3 4 1,alignx left");
    add(chkFolderReplaceSpace, "cell 1 4 2 1");
    add(cbxFolderReplaceSpaceChar, "cell 3 4,growx");
  }

  private void setupListeners() {
    btnBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (browseAction != null) browseAction.actionPerformed(arg0);
      }
    });

    btnBuildStructureFolder.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (buildStructureAction != null) buildStructureAction.actionPerformed(arg0);
      }
    });
  }

  private void createComponents() {
    lblLocatie = new JLabel("Locatie");

    txtLibraryFolder = new JTextField();
    txtLibraryFolder.setColumns(10);

    btnBrowse = new JButton("Bladeren");

    lblStructuur = new JLabel("Structuur");

    txtFolderStructure = new JTextField();
    txtFolderStructure.setColumns(10);

    btnBuildStructureFolder = new JButton("Structuur");

    chkRemoveEmptyFolder = new JCheckBox("Lege mappen verwijderen");

    chkFolderReplaceSpace = new JCheckBox("Vervangen spatie door: ");

    cbxFolderReplaceSpaceChar = new JComboBox<String>();
    cbxFolderReplaceSpaceChar.setModel(new DefaultComboBoxModel<String>(
        new String[] {"-", ".", "_"}));
  }

  public void setBuildStructureAction(ActionListener actionListener) {
    this.buildStructureAction = actionListener;
  }
  
  public void setBrowseAction(ActionListener actionListener) {
    this.browseAction = actionListener;
  }

  public JTextField getStructure() {
    return this.txtFolderStructure;
  }
  
  public boolean isFolderReplaceSpaceSelected() {
    return chkFolderReplaceSpace.isSelected();
  }

  public void setFolderReplaceSpaceSelected(boolean b) {
    chkFolderReplaceSpace.setSelected(b);
  }
  
  public boolean isRemoveEmptyFolderSelected() {
    return chkRemoveEmptyFolder.isSelected();
  }

  public void setRemoveEmptyFolderSelected(boolean b) {
    chkRemoveEmptyFolder.setSelected(b);
  }
  
  public String getReplaceSpaceChar() {
    return (String) cbxFolderReplaceSpaceChar.getSelectedItem();
  }
  
  public void setReplaceSpaceChar(String s){
    cbxFolderReplaceSpaceChar.setSelectedItem(s);
  }
  
  public void setLibraryFolder(String path){
    this.txtLibraryFolder.setText(path);
  }
  
  public String getLibraryFolder(){
    return this.txtLibraryFolder.getText();
  }

}
