package org.lodder.subtools.multisubdownloader.gui.jcomponent.jcheckbox;

import java.util.Arrays;

import javax.swing.JCheckBox;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JCheckBoxExtension {

    // public <T extends JCheckBox> T addCheckedChangeListener(T checkBox, Consumer<Boolean> listener) {
    // checkBox.addItemListener(
    // e -> {
    // JCheckBox chk = (JCheckBox) e.getSource();
    // // if (chk.isEnabled()) {
    // listener.accept(chk.isSelected());
    // // }
    // });
    // return checkBox;
    // }

    public <T extends JCheckBox> T addCheckedChangeListener(T checkBox, BooleanConsumer... listeners) {
        checkBox.addItemListener(
                e -> {
                    JCheckBox chk = (JCheckBox) e.getSource();
                    // if (chk.isEnabled()) {
                    Arrays.stream(listeners).forEach(listener -> listener.accept(chk.isSelected()));
                    // }
                });
        return checkBox;
    }

    public <T extends JCheckBox> T visible(T checkBox, boolean visible) {
        checkBox.setVisible(visible);
        return checkBox;
    }
}
