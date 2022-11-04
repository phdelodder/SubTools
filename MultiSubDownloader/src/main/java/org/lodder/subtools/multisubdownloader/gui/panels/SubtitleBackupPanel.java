package org.lodder.subtools.multisubdownloader.gui.panels;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.sublibrary.util.JComponentExtension;

import java.awt.event.ActionListener;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod(JComponentExtension.class)
public class SubtitleBackupPanel extends JPanel {

    private static final long serialVersionUID = -1498846730946617177L;
    private JCheckBox chkBackupSubtitle;
    private JLabel lblBackupLocatie;
    private JTextField txtBackupSubtitlePath;
    private JButton btnBrowseBackup;
    private JCheckBox chkBackupUseSourceFileName;
    private ActionListener browseBackupAction;
    private JLabel lblTitle;

    public SubtitleBackupPanel() {
        setLayout(new MigLayout("", "[][][][grow][center]", "[][][][][]"));

        createComponents();
        setupListeners();
        addComponentsToPanel();
    }

    private void createComponents() {
        chkBackupSubtitle = new JCheckBox(Messages.getString("PreferenceDialog.BackupSubtitles"));
        lblBackupLocatie = new JLabel(Messages.getString("PreferenceDialog.Location"));
        txtBackupSubtitlePath = new JTextField();
        txtBackupSubtitlePath.setColumns(10);
        txtBackupSubtitlePath.setEnabled(false);
        btnBrowseBackup = new JButton(Messages.getString("App.Browse"));
        btnBrowseBackup.setEnabled(false);
        chkBackupUseSourceFileName = new JCheckBox(Messages.getString("PreferenceDialog.IncludeSourceInFileName"));
        lblTitle = new JLabel(Messages.getString("PreferenceDialog.SubtitlesBackup"));
    }

    private void setupListeners() {
        btnBrowseBackup.addActionListener(arg0 -> {
            if (browseBackupAction != null) {
                browseBackupAction.actionPerformed(arg0);
            }
        });
        chkBackupSubtitle.addSelectedChangeListener(enabled -> {
            txtBackupSubtitlePath.setEnabled(enabled);
            btnBrowseBackup.setEnabled(enabled);
        });
    }

    private void addComponentsToPanel() {
        add(lblTitle, "cell 0 0 5 1,gapy 5");
        add(new JSeparator(), "cell 0 0 5 1,growx,gapy 5");
        add(chkBackupSubtitle, "cell 1 1 4 1");
        add(lblBackupLocatie, "cell 1 2,alignx left");
        add(txtBackupSubtitlePath, "cell 2 2 2 1,growx");
        add(btnBrowseBackup, "cell 4 2,alignx center");
        add(chkBackupUseSourceFileName, "cell 1 3 4 1");
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

    public String getBackupSubtitlePath() {
        return this.txtBackupSubtitlePath.getText();
    }

    public void setBackupUseWebsiteFilenameSelected(boolean b) {
        this.chkBackupUseSourceFileName.setSelected(b);
    }

    public boolean isBackupUseWebsiteFilenameSelected() {
        return chkBackupUseSourceFileName.isSelected();
    }

}
