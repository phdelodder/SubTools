package org.lodder.subtools.sublibrary.userinteraction;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;
import org.lodder.subtools.sublibrary.gui.InputPane;
import org.lodder.subtools.sublibrary.util.Validator;

@AllArgsConstructor
public class UserInteractionHandlerGUI implements UserInteractionHandler {

    private static final Object LOCK = new Object();
    @val @override UserInteractionSettingsIntf settings;
    @val JFrame frame;

    @Override
    public <T> Optional<T> selectFromList(Iterable<T> options, String message,
        @Nullable String title, @Nullable Function<T, String> toStringMapper) {
        synchronized (LOCK) {
            ElementWrapper<T>[] wrappedOptions = options.stream()
                .map(option -> new ElementWrapper<>(option, toStringMapper == null ? String::valueOf : toStringMapper))
                .toArray(ElementWrapper[]::new);
            return Optional.ofNullable(
                    (ElementWrapper<T>) JOptionPane.showInputDialog(frame, message, title, JOptionPane.DEFAULT_OPTION,
                        null, wrappedOptions, wrappedOptions[0]))
                .map(ElementWrapper::element);
        }
    }

    @Override
    public boolean confirm(String message, String title) {
        synchronized (LOCK) {
            int choice =
                Integer.parseInt(JOptionPane.showInputDialog(frame, message, title, JOptionPane.QUESTION_MESSAGE));
            return choice == JOptionPane.YES_OPTION;
        }
    }

    @Override
    public Optional<String> enter(String message, @Nullable String title,
        @Nullable List<Validator<String>> inputValidators) {
        synchronized (LOCK) {
            return new InputPane<>(
                title:title,
                message:message,
                inputValidators:inputValidators,
                toObjectMapper:Function.identity())
                .prompt();
        }
    }

    @Override
    public OptionalInt enterNumber(String title, String message,
        @Nullable List<Validator<Integer>> objectValidators) {
        synchronized (LOCK) {
            return new InputPane<>(
                title:title,
                message:message,
                toObjectMapper:Integer::parseInt,
                objectValidators:objectValidators)
                .prompt().mapToInt(v -> v);
        }
    }

    public void message(String message, String title) {
        synchronized (LOCK) {
            JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void showMessage(String message, String title, MessageSeverity messageSeverity) {
        int messageType = switch (messageSeverity) {
            case INFO -> JOptionPane.INFORMATION_MESSAGE;
            case WARNING -> JOptionPane.WARNING_MESSAGE;
            case ERROR -> JOptionPane.ERROR_MESSAGE;
        };
        synchronized (LOCK) {
            JOptionPane.showMessageDialog(frame, message, title, messageType);
        }
    }

    private record ElementWrapper<T>(T element, Function<T, String> toStringMapper) {
        @Override
        public @NonNull String toString() {
            return toStringMapper.apply(element);
        }
    }
}
