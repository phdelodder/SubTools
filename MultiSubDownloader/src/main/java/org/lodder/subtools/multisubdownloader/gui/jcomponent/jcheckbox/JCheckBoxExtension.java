package org.lodder.subtools.multisubdownloader.gui.jcomponent.jcheckbox;

import javax.swing.*;
import java.util.Arrays;

import lombok.experimental.UtilityClass;
import org.lodder.subtools.sublibrary.util.BooleanConsumer;

@UtilityClass
public class JCheckBoxExtension {

    public <T extends JCheckBox> T addCheckedChangeListener(T checkBox, BooleanConsumer... listeners) {
        checkBox.addItemListener(e -> Arrays.stream(listeners).forEach(listener -> listener.accept(((JCheckBox) e.getSource()).isSelected())));
        return checkBox;
    }

    public <T extends JCheckBox> T visible(T checkBox, boolean visible) {
        checkBox.setVisible(visible);
        return checkBox;
    }
}
