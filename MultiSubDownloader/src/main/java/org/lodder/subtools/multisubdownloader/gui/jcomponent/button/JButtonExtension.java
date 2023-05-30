package org.lodder.subtools.multisubdownloader.gui.jcomponent.button;

import javax.swing.JButton;
import javax.swing.JRootPane;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JButtonExtension {

    public <T extends JButton> T defaultButtonFor(T abstractButton, JRootPane rootPane) {
        rootPane.setDefaultButton(abstractButton);
        return abstractButton;
    }
}
