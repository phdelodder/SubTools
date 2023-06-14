package org.lodder.subtools.multisubdownloader.gui.jcomponent.button;

import java.util.function.Consumer;

import javax.swing.AbstractButton;

import java.awt.event.ActionListener;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AbstractButtonExtension {

    public <T extends AbstractButton> T withActionListener(T abstractButton, ActionListener listener) {
        abstractButton.addActionListener(listener);
        return abstractButton;
    }

    public <T extends AbstractButton> T withActionListener(T abstractButton, Runnable listener) {
        return withActionListener(abstractButton, (ActionListener) arg -> listener.run());
    }

    public <T extends AbstractButton> T withActionListenerSelf(T abstractButton, Consumer<T> selfConsumerListener) {
        return withActionListener(abstractButton, (ActionListener) arg -> selfConsumerListener.accept(abstractButton));
    }

    public <T extends AbstractButton> T withSelectedListener(T abstractButton, BooleanConsumer selectedConsumer) {
        return withActionListener(abstractButton, (ActionListener) arg -> selectedConsumer.accept(abstractButton.isSelected()));
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
