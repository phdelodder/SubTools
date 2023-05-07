package org.lodder.subtools.multisubdownloader.gui.panels;

import java.io.Serial;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.actions.SearchAction;
import org.lodder.subtools.multisubdownloader.gui.LanguageWrapper;
import org.lodder.subtools.sublibrary.Language;

public abstract class InputPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = 7753220002440733463L;
    private JButton btnSearch;
    private JComboBox<LanguageWrapper> cbxLanguage;
    private SearchAction searchAction;
    private final LanguageWrapper[] languageSelection = Arrays.stream(Language.values()).map(LanguageWrapper::new).toArray(LanguageWrapper[]::new);

    public InputPanel() {
        createComponents();
        setupListeners();
    }

    public Language getSelectedLanguage() {
        return ((LanguageWrapper) cbxLanguage.getSelectedItem()).language();
    }

    public void setSelectedlanguage(Language language) {
        cbxLanguage.setSelectedItem(new LanguageWrapper(language));
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

    protected JComboBox<LanguageWrapper> getLanguageCbx() {
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
