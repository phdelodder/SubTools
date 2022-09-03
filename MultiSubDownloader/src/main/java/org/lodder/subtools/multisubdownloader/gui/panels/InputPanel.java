package org.lodder.subtools.multisubdownloader.gui.panels;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.actions.SearchAction;

public abstract class InputPanel extends JPanel {

    private static final long serialVersionUID = 7753220002440733463L;
    private JButton btnSearch;
    private JComboBox<String> cbxLanguage;
    private SearchAction searchAction;
    private final String[] languageSelection = { Messages.getString("InputPanel.Dutch"),
            Messages.getString("InputPanel.English") };

    public InputPanel() {
        createComponents();
        setupListeners();
    }

    public String getSelectedLanguage() {
        return ((String) cbxLanguage.getSelectedItem()).trim();
    }

    public void setSearchAction(SearchAction searchAction) {
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
        btnSearch.addActionListener(event -> {
            if (searchAction == null) {
                return;
            }

            Thread searchThread = new Thread(searchAction);
            searchThread.start();

        });
    }

    private void createComponents() {
        cbxLanguage = new JComboBox<>();
        cbxLanguage.setModel(new DefaultComboBoxModel<>(languageSelection));
        cbxLanguage.setSelectedIndex(0);

        btnSearch = new JButton(Messages.getString("InputPanel.SearchForSubtitles"));
    }

}
