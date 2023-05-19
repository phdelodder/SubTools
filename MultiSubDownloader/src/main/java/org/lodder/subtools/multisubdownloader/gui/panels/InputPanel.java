package org.lodder.subtools.multisubdownloader.gui.panels;

import java.io.Serial;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.actions.SearchAction;
import org.lodder.subtools.multisubdownloader.gui.ToStringListCellRenderer;
import org.lodder.subtools.multisubdownloader.gui.extra.MyComboBox;
import org.lodder.subtools.sublibrary.Language;

public abstract class InputPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = 7753220002440733463L;
    private JButton btnSearch;
    private MyComboBox<Language> cbxLanguage;

    public InputPanel() {
        createComponents();
    }

    public Language getSelectedLanguage() {
        return cbxLanguage.getSelectedItem();
    }

    public void setSelectedlanguage(Language language) {
        cbxLanguage.setSelectedItem(language);
    }

    public void addSearchAction(SearchAction searchAction) {
        if (searchAction != null) {
            btnSearch.addActionListener(event -> new Thread(searchAction).start());
        }
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

    protected MyComboBox<Language> getLanguageCbx() {
        return this.cbxLanguage;
    }

    private void createComponents() {
        cbxLanguage = new MyComboBox<>();
        cbxLanguage.setModel(new DefaultComboBoxModel<>(Language.values()));
        cbxLanguage.setSelectedIndex(0);
        cbxLanguage.setRenderer(ToStringListCellRenderer.ofMessage(cbxLanguage.getRenderer(), Language::getMsgCode));

        btnSearch = new JButton(Messages.getString("InputPanel.SearchForSubtitles"));
    }
}
