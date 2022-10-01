package org.lodder.subtools.sublibrary;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import org.lodder.subtools.sublibrary.data.UserInteractionSettingsIntf;

public interface UserInteractionHandler {

    UserInteractionSettingsIntf getSettings();

    boolean confirm(String message, String title);

    Optional<String> selectFromList(Collection<String> options, String message, String title);

    <T> Optional<T> selectFromList(Collection<T> options, String message, String title, Function<T, String> toStringMapper);
}
