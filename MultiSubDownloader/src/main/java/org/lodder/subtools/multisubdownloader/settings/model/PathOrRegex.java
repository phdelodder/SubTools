package org.lodder.subtools.multisubdownloader.settings.model;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import manifold.ext.props.rt.api.val;
import manifold.ext.rt.api.Self;

public class PathOrRegex implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    @val String value;
    @val transient Image image;
    private final transient Predicate<Path> isExcludedPathPredicate;

    public PathOrRegex(Path path) {
        this.value = path.toString();
        this.image = getImage(path);
        this.isExcludedPathPredicate = path::equals;
    }

    public PathOrRegex(String value) {
        this.value = value;
        boolean regex;
        Path path = null;
        try {
            path = Path.of(value);
            regex = !path.isAbsolute();
        } catch (InvalidPathException e) {
            regex = true;
        }
        if (regex) {
            this.image = PathMatchType.REGEX.image;
            Pattern pattern = Pattern.compile(value.replace("*", ".*") + ".*$", Pattern.CASE_INSENSITIVE);
            this.isExcludedPathPredicate = p -> pattern.matcher(p.getFileName().toString()).find();
        } else {
            this.image = getImage(path);
            this.isExcludedPathPredicate = path::equals;
        }
    }

    private Image getImage(Path path) {
        return Files.isDirectory(path) ? PathMatchType.FOLDER.image : PathMatchType.FILE.image;
    }

    public boolean isExcludedPath(Path path) {
        return isExcludedPathPredicate.test(path);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(@Self Object obj) {
        return obj instanceof PathOrRegex other && Objects.equals(value, other.value);
    }
}
