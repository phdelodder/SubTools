package org.lodder.subtools.sublibrary.userinteraction;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;

public interface UserInteractionHandler {

    UserInteractionSettingsIntf getSettings();

    boolean confirm(String message, String title);

    Optional<String> selectFromList(Collection<String> options, String message, String title);

    <T> Optional<T> selectFromList(Collection<T> options, String message, String title, Function<T, String> toStringMapper);

    default Optional<String> enter(String title, String message) {
        return enter(title, message, null, null);
    }

    Optional<String> enter(String title, String message, String errorMessage, Predicate<String> validator);
}
