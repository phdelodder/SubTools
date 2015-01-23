package org.lodder.subtools.multisubdownloader.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public abstract class InputPanel extends JPanel {
  private JButton           btnSearch;
  private JComboBox<String> cbxLanguage;
  private ActionListener    searchAction;
  private final String[] languageSelection = new String[]{"Nederlands", "Engels"};

  public InputPanel() {
    createComponents();
    setupListeners();
  }

  public String getSelectedLanguage() {
    return ((String) cbxLanguage.getSelectedItem()).trim();
  }

  public void setSearchAction(ActionListener searchAction) {
    this.searchAction = searchAction;
  }

  public void enableSearchButton() {
    btnSearch.setEnabled(true);
  }

  public void disableSearchButton() {
    this.btnSearch.setEnabled(false);
  }

  protected JButton getSearchButton() {
    return this.btnSearch;
  }

  protected JComboBox<String> getLanguageCbx() {
    return this.cbxLanguage;
  }

  private void setupListeners() {
    btnSearch.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        searchAction.actionPerformed(e);
      }
    });
  }

  private void createComponents() {
    cbxLanguage = new JComboBox<String>();
    cbxLanguage.setModel(new DefaultComboBoxModel<String>(languageSelection));
    cbxLanguage.setSelectedIndex(0);

    btnSearch = new JButton("Zoeken naar ondertitels");
  }

}
