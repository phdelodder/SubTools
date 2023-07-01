package org.lodder.subtools.multisubdownloader.gui.panels;

import java.io.Serial;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield.JTextFieldExtension;

import java.awt.event.ActionListener;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JTextFieldExtension.class })
public class SearchFileInputPanel extends InputPanel {

    @Serial
    private static final long serialVersionUID = 6522020963519514345L;
    private JTextField txtIncomingPath;
    private JCheckBox chkRecursive;
    private JCheckBox chkForceSubtitleOverwrite;
    private JButton btnBrowse;

    public SearchFileInputPanel() {
        super();
        setLayout(new MigLayout("", "[][][][][][]", "[][][][][][]"));

        createComponents();
        addComponentsToPanel();
    }

    private void addComponentsToPanel() {
        add(new JLabel(Messages.getString("MainWindow.LocationNewEpisodes")), "cell 1 0,alignx trailing");
        add(txtIncomingPath, "cell 2 0,alignx leading");
        add(btnBrowse, "cell 3 0");
        add(chkRecursive, "cell 2 1 2 1");
        add(chkForceSubtitleOverwrite, "cell 2 3 2 1");
        add(getSearchButton(), "cell 0 5 3 1,alignx center");
        add(new JLabel(Messages.getString("MainWindow.SelectSubtitleLanguage")), "cell 2 2");
        add(getLanguageCbx(), "cell 3 2");
    }

    private void createComponents() {
        txtIncomingPath = new JTextField().withColumns(20);

        chkRecursive = new JCheckBox(Messages.getString("MainWindow.RecursiveSearch"));
        chkForceSubtitleOverwrite = new JCheckBox(Messages.getString("MainWindow.ignoreExistingSubtitles"));

        btnBrowse = new JButton(Messages.getString("App.Browse"));
    }

    public void setRecursiveSelected(boolean selected) {
        chkRecursive.setSelected(selected);
    }

    public void addSelectFolderAction(ActionListener selectFolderAction) {
        if (selectFolderAction != null) {
            btnBrowse.addActionListener(selectFolderAction);
        }
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
        return chkForceSubtitleOverwrite.isSelected();
    }

}
