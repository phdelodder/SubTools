package org.lodder.subtools.multisubdownloader.settings.model;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import manifold.ext.props.rt.api.get;

@Deprecated(since = "Settings version 6")
public class SettingsExcludeItem {

    @get String description;
    @get PathMatchType type;
    @get Predicate<Path> isExcludedPredicate;

    public SettingsExcludeItem(String description, PathMatchType type) {
        this.description = description;
        this.type = type;
        this.isExcludedPredicate = switch (type) {
            case FOLDER, FILE -> Path.of(description)::equals;
            case REGEX -> {
                Pattern pattern = Pattern.compile(description.replace("*", ".*") + ".*$", Pattern.CASE_INSENSITIVE);
                yield file -> pattern.matcher(file.getFileName().toString()).find();
            }
        };
    }

    public boolean isExcluded(Path path) {
        return isExcludedPredicate.test(path);
    }
}
