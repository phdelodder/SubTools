package org.lodder.subtools.multisubdownloader.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public abstract class StructurePanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 7507970016496546514L;
  private ActionListener buildStructureAction;
  protected JButton btnBuildStructure;
  private JCheckBox chkReplaceSpace;
  private JComboBox<String> cbxReplaceSpaceChar;
  
  public StructurePanel(){
    createComponents();
    setupListeners();
  }

  private void setupListeners() {
    btnBuildStructure.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (buildStructureAction != null) buildStructureAction.actionPerformed(arg0);
      }
    });
  }

  private void createComponents() {
    btnBuildStructure = new JButton("Structuur");
    
    chkReplaceSpace = new JCheckBox("Vervangen spatie door: ");

    cbxReplaceSpaceChar = new JComboBox<String>();
    cbxReplaceSpaceChar.setModel(new DefaultComboBoxModel<String>(new String[] {"-", ".",
        "_"}));
  }
  
  protected JCheckBox getChkReplaceSpace() {
    return chkReplaceSpace;
  }

  protected JComboBox<String> getCbxReplaceSpaceChar() {
    return cbxReplaceSpaceChar;
  }

  protected JButton getBtnBuildStructure() {
    return btnBuildStructure;
  }

  public void setBuildStructureAction(ActionListener actionListener) {
    this.buildStructureAction = actionListener;
  }
  
  public String getReplaceSpaceChar() {
    return (String) this.getCbxReplaceSpaceChar().getSelectedItem();
  }
  
  public void setReplaceSpaceChar(String s){
    this.getCbxReplaceSpaceChar().setSelectedItem(s);
  }
  
  public boolean isReplaceSpaceSelected() {
    return this.getChkReplaceSpace().isSelected();
  }

  public void setReplaceSpaceSelected(boolean b) {
    this.getChkReplaceSpace().setSelected(b);
  }

}
