package org.lodder.subtools.multisubdownloader.lib;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface UserInteractionHandler {

    Optional<String> selectFromList(List<String> options, String message, String title);

    <T> Optional<T> selectFromList(List<T> options, String message, String title, Function<T, String> toStringMapper);
}
