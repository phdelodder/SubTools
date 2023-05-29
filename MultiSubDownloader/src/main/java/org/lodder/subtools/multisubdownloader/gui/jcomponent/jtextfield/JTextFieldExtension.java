package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import javax.swing.JTextField;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JTextFieldExtension {

    public <T extends JTextField> T withColumns(T textField, int columns) {
        textField.setColumns(columns);
        return textField;
    }

    // public <T extends JTextField> T enabled(T textField, boolean enabled) {
    // textField.setEnabled(enabled);
    // return textField;
    // }
}
