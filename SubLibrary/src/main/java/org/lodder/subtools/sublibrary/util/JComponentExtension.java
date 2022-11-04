package org.lodder.subtools.sublibrary.util;

import java.util.function.Consumer;

import javax.swing.JCheckBox;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JComponentExtension {

    public JCheckBox addSelectedChangeListener(JCheckBox checkBox, Consumer<Boolean> listener) {
        checkBox.addChangeListener(e -> listener.accept(((JCheckBox) e.getSource()).isSelected()));
        return checkBox;
    }
}
