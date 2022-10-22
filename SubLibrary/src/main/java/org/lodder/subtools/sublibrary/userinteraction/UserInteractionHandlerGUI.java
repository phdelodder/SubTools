package org.lodder.subtools.sublibrary.userinteraction;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.gui.InputPane;
import org.lodder.subtools.sublibrary.gui.OptionsPane;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class UserInteractionHandlerGUI implements UserInteractionHandler {

    private final UserInteractionSettingsIntf settings;
    private final JFrame frame;

    @Override
    public Optional<String> selectFromList(Collection<String> options, String message, String title) {
        if (options.isEmpty()) {
            return Optional.empty();
        }
        return OptionsPane.stringOptions(options).title(title).message(message).defaultOption().parent(frame).prompt();
    }

    @Override
    public <T> Optional<T> selectFromList(Collection<T> options, String message, String title, Function<T, String> toStringMapper) {
        if (options.isEmpty()) {
            return Optional.empty();
        }
        return OptionsPane.options(options).toStringMapper(toStringMapper).title(title).message(message).defaultOption().parent(frame).prompt();
    }

    @Override
    public boolean confirm(String message, String title) {
        int choice = Integer.parseInt(JOptionPane.showInputDialog(frame, message, title, JOptionPane.YES_NO_OPTION));
        return choice == JOptionPane.YES_OPTION;
    }

    @Override
    public Optional<String> enter(String title, String message, String errorMessage, Predicate<String> validator) {
        return InputPane.create().title(title).message(message).errorMessage(errorMessage).validator(validator).prompt();
    }
}
