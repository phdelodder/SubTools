package org.lodder.subtools.multisubdownloader.gui.jcomponent.button;

import javax.swing.AbstractButton;

import java.awt.event.ActionListener;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AbstractButtonExtension {

    public <T extends AbstractButton> T withActionListener(T abstractButton, ActionListener listener) {
        abstractButton.addActionListener(listener);
        return abstractButton;
    }

    public <T extends AbstractButton> T withActionListener(T abstractButton, Runnable listener) {
        return withActionListener(abstractButton, arg -> listener.run());
    }

    public <T extends AbstractButton> T actionCommand(T abstractButton, String actionCommand) {
        abstractButton.setActionCommand(actionCommand);
        return abstractButton;
    }

    public <T extends AbstractButton> T withActionCommand(T abstractButton, String actionCommand) {
        abstractButton.getModel().setActionCommand(actionCommand);
        return abstractButton;
    }
}
