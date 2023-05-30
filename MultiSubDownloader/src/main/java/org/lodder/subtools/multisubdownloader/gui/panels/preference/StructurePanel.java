package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import java.io.Serial;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcheckbox.JCheckBoxExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcombobox.MyComboBox;

import java.awt.event.ActionListener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ JCheckBoxExtension.class })
public abstract class StructurePanel<T extends StructurePanel<T>> extends JPanel implements PreferencePanelIntf {

    @Serial
    private static final long serialVersionUID = 7507970016496546514L;
    @Getter(value = AccessLevel.PROTECTED)
    private final JButton btnBuildStructure;
    @Getter(value = AccessLevel.PROTECTED)
    private final JCheckBox chkReplaceSpace;
    @Getter(value = AccessLevel.PROTECTED)
    private final MyComboBox<String> cbxReplaceSpaceChar;

    public StructurePanel() {
        this.btnBuildStructure = new JButton(Messages.getString("StructureBuilderDialog.Structure"));

        this.cbxReplaceSpaceChar = new MyComboBox<>(new String[] { "-", ".", "_" });
        cbxReplaceSpaceChar.setEnabled(false);

        this.chkReplaceSpace = new JCheckBox(Messages.getString("PreferenceDialog.ReplaceSpaceWith"))
                .addCheckedChangeListener(cbxReplaceSpaceChar::setEnabled);
    }

    @SuppressWarnings("unchecked")
    public T addBuildStructureAction(ActionListener buildStructureAction) {
        btnBuildStructure.addActionListener(buildStructureAction::actionPerformed);
        return (T) this;
    }

    public String getReplaceSpaceChar() {
        return this.getCbxReplaceSpaceChar().getSelectedItem();
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
