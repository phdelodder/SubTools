package org.lodder.subtools.multisubdownloader.settings.model;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.lodder.subtools.sublibrary.util.NamedPattern;

import lombok.Getter;

@Deprecated(since = "Settings version 6")
@Getter
public class SettingsExcludeItem {

    private final String description;
    private final PathMatchType type;
    private final Predicate<Path> isExcludedPredicate;

    public SettingsExcludeItem(String description, PathMatchType type) {
        this.description = description;
        this.type = type;
        this.isExcludedPredicate = switch (type) {
            case FOLDER, FILE -> Path.of(description)::equals;
            case REGEX -> {
                NamedPattern np = NamedPattern.compile(description.replace("*", ".*") + ".*$", Pattern.CASE_INSENSITIVE);
                yield file -> np.matcher(file.getFileName().toString()).find();
            }
        };
    }

    public boolean isExcluded(Path path) {
        return isExcludedPredicate.test(path);
    }
}
