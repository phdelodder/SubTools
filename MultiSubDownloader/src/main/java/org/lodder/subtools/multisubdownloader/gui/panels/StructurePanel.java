package org.lodder.subtools.multisubdownloader.gui.panels;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.sublibrary.util.JComponentExtension;

import java.awt.event.ActionListener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(JComponentExtension.class)
public abstract class StructurePanel extends JPanel {

    private static final long serialVersionUID = 7507970016496546514L;
    @Setter
    private ActionListener buildStructureAction;
    @Getter(value = AccessLevel.PROTECTED)
    private JButton btnBuildStructure;
    @Getter(value = AccessLevel.PROTECTED)
    private JCheckBox chkReplaceSpace;
    @Getter(value = AccessLevel.PROTECTED)
    private JComboBox<String> cbxReplaceSpaceChar;

    public StructurePanel() {
        createComponents();
        setupListeners();
    }

    private void setupListeners() {
        btnBuildStructure.addActionListener(arg0 -> {
            if (buildStructureAction != null) {
                buildStructureAction.actionPerformed(arg0);
            }
        });
    }

    private void createComponents() {
        btnBuildStructure = new JButton(Messages.getString("StructureBuilderDialog.Structure"));

        cbxReplaceSpaceChar = new JComboBox<>();
        cbxReplaceSpaceChar.setEnabled(false);
        cbxReplaceSpaceChar.setModel(new DefaultComboBoxModel<>(new String[] { "-", ".", "_" }));

        chkReplaceSpace = new JCheckBox(Messages.getString("PreferenceDialog.ReplaceSpaceWith"));
        chkReplaceSpace.addCheckedChangeListener(cbxReplaceSpaceChar::setEnabled);


    }

    public String getReplaceSpaceChar() {
        return (String) this.getCbxReplaceSpaceChar().getSelectedItem();
    }

    public void setReplaceSpaceChar(String s) {
        this.getCbxReplaceSpaceChar().setSelectedItem(s);
    }

    public boolean isReplaceSpaceSelected() {
        return this.getChkReplaceSpace().isSelected();
    }

    public void setReplaceSpaceSelected(boolean b) {
        this.getChkReplaceSpace().setSelected(b);
    }

}
