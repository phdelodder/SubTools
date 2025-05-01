package org.lodder.subtools.multisubdownloader.gui.panels;

import static manifold.ext.props.rt.api.PropOption.*;

import javax.swing.*;
import java.io.Serial;

import manifold.ext.props.rt.api.val;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.actions.SearchAction;
import org.lodder.subtools.sublibrary.Language;

public abstract sealed class InputPanel extends JPanel permits SearchFileInputPanel, SearchTextInputPanel {

    @Serial
    private static final long serialVersionUID = 7753220002440733463L;
    @val JButton searchButton = new JButton(Messages.getText("InputPanel.SearchForSubtitles"));
    @val(Protected) JComboBox<Language> languageCbx =
        new JComboBox<>(Language.values()).toMessageStringRenderer(Language::getMsgCode);

    public Language getSelectedLanguage() {
        return languageCbx.getSelectedValue();
    }

    public void setSelectedLanguage(Language language) {
        languageCbx.setSelectedItem(language);
    }

    public void addSearchAction(SearchAction searchAction) {
        searchButton.addActionListener(_ -> new Thread(searchAction).start());
    }

    public void enableSearchButton() {
        searchButton.setEnabled(true);
    }

    public void disableSearchButton() {
        this.searchButton.setEnabled(false);
    }
}
