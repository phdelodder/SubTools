package org.lodder.subtools.sublibrary.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import javax.swing.JOptionPane;

import java.awt.Component;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OptionsPane {

    public static Object LOCK = new Object();

    public static <T> OptionsPaneBuilderToStringMapperIntf<T> options(Collection<T> options) {
        return OptionsPaneBuilder.options(options);
    }

    public static <T> OptionsPaneBuilderToStringMapperIntf<T> options(T[] options) {
        return OptionsPaneBuilder.options(options);
    }

    public static OptionsPaneBuilderTitleIntf<String> stringOptions(Collection<String> options) {
        return OptionsPaneBuilder.options(options);
    }

    public static OptionsPaneBuilderTitleIntf<String> stringOptions(String[] options) {
        return OptionsPaneBuilder.options(options);
    }

    // interface

    public interface OptionsPaneBuilderToStringMapperIntf<T> {
        OptionsPaneBuilderTitleIntf<T> toStringMapper(Function<T, String> toStringMapper);
    }

    public interface OptionsPaneBuilderTitleIntf<T> {
        OptionsPaneBuilderMessageIntf<T> title(String title);
    }

    public interface OptionsPaneBuilderMessageIntf<T> {
        OptionsPaneBuilderMessageTypeIntf<T> message(String message);
    }

    public interface OptionsPaneBuilderMessageTypeIntf<T> {
        OptionsPaneBuilderPromptIntf<T> messageType(int messageType);

        default OptionsPaneBuilderPromptIntf<T> defaultOption() {
            return messageType(JOptionPane.DEFAULT_OPTION);
        }

        default OptionsPaneBuilderPromptIntf<T> yesNoOption() {
            return messageType(JOptionPane.YES_NO_OPTION);
        }

        default OptionsPaneBuilderPromptIntf<T> yesNoCancelOption() {
            return messageType(JOptionPane.YES_NO_CANCEL_OPTION);
        }

        default OptionsPaneBuilderPromptIntf<T> okCancelOption() {
            return messageType(JOptionPane.OK_CANCEL_OPTION);
        }
    }

    public interface OptionsPaneBuilderPromptIntf<T> {
        OptionsPaneBuilderPromptIntf<T> parent(Component parent);

        Optional<T> prompt();
    }

    // builder

    @Setter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    private static class OptionsPaneBuilder<T> implements OptionsPaneBuilderPromptIntf<T>, OptionsPaneBuilderMessageTypeIntf<T>,
            OptionsPaneBuilderMessageIntf<T>, OptionsPaneBuilderTitleIntf<T>, OptionsPaneBuilderToStringMapperIntf<T> {
        private final T[] optionsArray;
        private final Collection<T> optionsList;
        private String title;
        private String message;
        private int messageType;
        private Function<T, String> toStringMapper;
        private Component parent;

        public static <S> OptionsPaneBuilder<S> options(Collection<S> options) {
            return new OptionsPaneBuilder<>(null, options);
        }

        public static <S> OptionsPaneBuilder<S> options(S[] options) {
            return new OptionsPaneBuilder<>(options, null);
        }

        @Override
        public Optional<T> prompt() {
            synchronized (LOCK) {
                if (toStringMapper == null) {
                    T[] options;
                    if (optionsList != null) {
                        options = (T[]) optionsList.stream().toArray(Object[]::new);
                    } else {
                        options = optionsArray;
                    }
                    return Optional.ofNullable((T) JOptionPane.showInputDialog(parent, message, title, messageType, null, options, "0"));
                } else {
                    ElementWrapper<T>[] options;
                    if (optionsList != null) {
                        options = optionsList.stream().map(option -> new ElementWrapper<>(option, toStringMapper)).toArray(ElementWrapper[]::new);
                    } else {
                        options = Arrays.stream(optionsArray).map(option -> new ElementWrapper<>(option, toStringMapper))
                                .toArray(ElementWrapper[]::new);
                    }
                    return Optional
                            .ofNullable((ElementWrapper<T>) JOptionPane.showInputDialog(parent, message, title, messageType, null, options, "0"))
                            .map(ElementWrapper::element);
                }
            }
        }
    }

    private record ElementWrapper<T> (T element, Function<T, String> toStringMapper) {
        @Override
        public String toString() {
            return toStringMapper.apply(element);
        }
    }
}
