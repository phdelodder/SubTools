package org.lodder.subtools.multisubdownloader.gui.panels;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.lodder.subtools.multisubdownloader.Messages;

import java.awt.event.ActionListener;
import java.io.Serial;

import net.miginfocom.swing.MigLayout;

public class SearchFileInputPanel extends InputPanel {

    @Serial
    private static final long serialVersionUID = 6522020963519514345L;
    private JTextField txtIncomingPath;
    private JCheckBox chkRecursive;
    private JCheckBox chkForceSubtitleOverwrite;
    private JButton btnBrowse;
    private ActionListener selectFolderAction;

    public SearchFileInputPanel() {
        super();
        setLayout(new MigLayout("", "[][][][][][]", "[][][][][][]"));

        createComponents();
        setupListeners();
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

    private void setupListeners() {
        btnBrowse.addActionListener(e -> {
            if (selectFolderAction != null) {
                selectFolderAction.actionPerformed(e);
            }
        });
    }

    private void createComponents() {
        txtIncomingPath = new JTextField();
        txtIncomingPath.setColumns(20);

        chkRecursive = new JCheckBox(Messages.getString("MainWindow.RecursiveSearch"));
        chkForceSubtitleOverwrite = new JCheckBox(Messages.getString("MainWindow.ignoreExistingSubtitles"));

        btnBrowse = new JButton(Messages.getString("App.Browse"));
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
        return chkForceSubtitleOverwrite.isSelected();
    }

}
