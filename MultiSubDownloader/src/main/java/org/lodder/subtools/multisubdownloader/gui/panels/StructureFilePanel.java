package org.lodder.subtools.multisubdownloader.gui.panels;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.lodder.subtools.multisubdownloader.Messages;

import net.miginfocom.swing.MigLayout;

public class StructureFilePanel extends StructurePanel {

    private static final long serialVersionUID = -5458593307643063563L;
    private JLabel lblStructuur_1;
    private JTextField txtFileStructure;
    private JCheckBox chkIncludeLanguageCode;
    private JLabel lblNederlands;
    private JTextField txtDefaultNlText;
    private JLabel lblEnglish;
    private JTextField txtDefaultEnText;

    public StructureFilePanel() {
        super();
        setLayout(new MigLayout("", "[][][][grow][]", "[][][][][][]"));

        createComponents();
        setupListeners();
        addComponentsToPanel();
    }

    private void addComponentsToPanel() {
        add(new JLabel(Messages.getString("PreferenceDialog.RenameFiles")), "cell 0 0 5 1,gapy 5");
        add(new JSeparator(), "cell 0 0 5 1,growx,gapy 5");
        add(lblStructuur_1, "cell 1 1,alignx left");
        add(txtFileStructure, "cell 2 1 2 1,growx");
        add(getBtnBuildStructure(), "cell 4 1");
        add(this.getChkReplaceSpace(), "cell 1 2 2 1");
        add(this.getCbxReplaceSpaceChar(), "cell 3 2,growx");
        add(chkIncludeLanguageCode, "cell 1 3 4 1");
        add(lblNederlands, "cell 1 4,alignx trailing");
        add(txtDefaultNlText, "cell 2 4");
        add(lblEnglish, "cell 1 5,alignx trailing");
        add(txtDefaultEnText, "cell 2 5");
    }

    private void setupListeners() {

    }

    private void createComponents() {
        lblStructuur_1 = new JLabel(Messages.getString("PreferenceDialog.Structure"));

        txtFileStructure = new JTextField();
        txtFileStructure.setColumns(10);

        chkIncludeLanguageCode = new JCheckBox(Messages.getString("PreferenceDialog.IncludeLanguageInFileName"));

        lblNederlands = new JLabel("Nederlands");

        txtDefaultNlText = new JTextField();
        txtDefaultNlText.setColumns(10);

        lblEnglish = new JLabel("English");

        txtDefaultEnText = new JTextField();
        txtDefaultEnText.setColumns(10);
    }

    public void setFileStructure(String s) {
        this.txtFileStructure.setText(s);
    }

    public String getFileStructure() {
        return this.txtFileStructure.getText();
    }

    public boolean isIncludeLanguageCodeSelected() {
        return chkIncludeLanguageCode.isSelected();
    }

    public void setIncludeLanguageCodeSelected(boolean b) {
        chkIncludeLanguageCode.setSelected(b);
    }

    @Override
    public JButton getBtnBuildStructure() {
        return super.getBtnBuildStructure();
    }

    public JTextField getTxtDefaultNlText() {
        return txtDefaultNlText;
    }

    public JTextField getTxtDefaultEnText() {
        return txtDefaultEnText;
    }

}
