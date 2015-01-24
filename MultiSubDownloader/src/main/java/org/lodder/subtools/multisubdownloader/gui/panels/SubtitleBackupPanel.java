package org.lodder.subtools.multisubdownloader.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class SubtitleBackupPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = -1498846730946617177L;
  private JCheckBox chkBackupSubtitle;
  private JLabel lblBackupLocatie;
  private JTextField txtBackupSubtitlePath;
  private JButton btnBrowseBackup;
  private JCheckBox chkBackupUseWebsiteFileName;
  private ActionListener browseBackupAction;
  private JLabel lblTitle;

  public SubtitleBackupPanel() {
    setLayout(new MigLayout("", "[][][][grow][center]", "[][][][][]"));

    createComponents();
    setupListeners();
    addComponentsToPanel();
  }

  private void createComponents() {
    chkBackupSubtitle = new JCheckBox("Backup ondertitels?");
    lblBackupLocatie = new JLabel("Locatie");
    txtBackupSubtitlePath = new JTextField();
    txtBackupSubtitlePath.setColumns(10);
    btnBrowseBackup = new JButton("Bladeren");
    chkBackupUseWebsiteFileName = new JCheckBox("Naam gebruik van de ondertitel bron?");
    lblTitle = new JLabel("Ondertitel Backup");
  }

  private void setupListeners() {
    btnBrowseBackup.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (browseBackupAction != null) browseBackupAction.actionPerformed(arg0);
      }
    });
  }

  private void addComponentsToPanel() {
    add(lblTitle, "cell 0 0 5 1,gapy 5");
    add(new JSeparator(), "cell 0 0 5 1,growx,gapy 5");
    add(chkBackupSubtitle, "cell 1 1 4 1");
    add(lblBackupLocatie, "cell 1 2,alignx left");
    add(txtBackupSubtitlePath, "cell 2 2 2 1,growx");
    add(btnBrowseBackup, "cell 4 2,alignx center");
    add(chkBackupUseWebsiteFileName, "cell 1 3 4 1");
    add(btnBrowseBackup, "cell 4 2,alignx center");
  }

  public void setBrowseBackupAction(ActionListener browseBackupAction) {
    this.browseBackupAction = browseBackupAction;
  }

  public boolean isBackupSubtitleSelected() {
    return chkBackupSubtitle.isSelected();
  }

  public void setBackupSubtitleSelected(boolean b) {
    chkBackupSubtitle.setSelected(b);
  }

  public void setBackupSubtitlePath(String path) {
    this.txtBackupSubtitlePath.setText(path);
  }
  
  public String getBackupSubtitlePath(){
    return this.txtBackupSubtitlePath.getText();
  }

  public void setBackupUseWebsiteFilenameSelected(boolean b) {
    this.chkBackupUseWebsiteFileName.setSelected(b);
  }
  
  public boolean isBackupUseWebsiteFilenameSelected() {
    return chkBackupUseWebsiteFileName.isSelected();
  }

}
