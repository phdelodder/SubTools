package org.lodder.subtools.multisubdownloader.gui.panels;

import java.awt.event.ActionListener;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class SearchFileInputPanel extends InputPanel {

  private JTextField        txtIncomingPath;
  private JCheckBox         chkRecursive;
  private JCheckBox         chkforceSubtitleOverwrite;
  private JButton           btnBrowse;
  private ActionListener    selectFolderAction;

  public SearchFileInputPanel() {
    super();
    setLayout(new MigLayout("", "[][][][][][]", "[][][][][][]"));

    createComponents();
    setupListeners();
    addComponentsToPanel();
  }

  private void addComponentsToPanel() {
    add(new JLabel("Locatie nieuwe afleveringen"), "cell 1 0,alignx trailing");
    add(txtIncomingPath, "cell 2 0,alignx leading");
    add(btnBrowse, "cell 3 0");
    add(chkRecursive, "cell 2 1 2 1");
    add(chkforceSubtitleOverwrite, "cell 2 3 2 1");
    add(getSearchButton(), "cell 0 5 3 1,alignx center");
    add(new JLabel("Selecteer de gewenste ondertitel taal"), "cell 2 2");
    add(getLanguageCbx(), "cell 3 2");
  }

  private void setupListeners() {
    btnBrowse.addActionListener(selectFolderAction);
  }

  private void createComponents() {
    txtIncomingPath = new JTextField();
    txtIncomingPath.setColumns(20);

    chkRecursive = new JCheckBox("Mappen in map doorzoeken");
    chkforceSubtitleOverwrite = new JCheckBox("Negeer bestaande ondertitel bestanden");

    btnBrowse = new JButton("Bladeren");
  }

  public void setRecursiveSelected(boolean selected) {
    chkRecursive.setSelected(selected);
  }

  public void setSelectFolderAction(ActionListener selectFolderAction) {
    this.selectFolderAction = selectFolderAction;
  }

  public void setIncomingPath(String path) {
    txtIncomingPath.setText(path);
  }

  public String getIncomingPath() {
    return txtIncomingPath.getText().trim();
  }

  public boolean isRecursiveSelected() {
    return chkRecursive.isSelected();
  }

  public boolean isForceOverwrite() {
    return chkforceSubtitleOverwrite.isSelected();
  }

}
