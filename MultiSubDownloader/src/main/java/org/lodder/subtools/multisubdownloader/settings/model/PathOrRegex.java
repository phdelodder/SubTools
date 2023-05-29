package org.lodder.subtools.multisubdownloader.settings.model;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.lodder.subtools.sublibrary.util.NamedPattern;

import java.awt.Image;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathOrRegex {

    @Getter
    private final String value;
    @Getter
    private final Image image;
    private final Predicate<Path> isExcludedPathPredicate;

    public PathOrRegex(Path path) {
        this.value = path.toString();
        this.image = getImage(path);
        this.isExcludedPathPredicate = path::equals;
    }

    public PathOrRegex(String value) {
        this.value = value;
        Image img;
        Predicate<Path> excludedPathPredicate;
        try {
            Path path = Path.of(value);
            img = path.isAbsolute() ? getImage(path) : PathMatchType.REGEX.getImage();
            excludedPathPredicate = path::equals;
        } catch (InvalidPathException e) {
            img = PathMatchType.REGEX.getImage();
            NamedPattern np = NamedPattern.compile(value.replace("*", ".*") + ".*$", Pattern.CASE_INSENSITIVE);
            excludedPathPredicate = p -> np.matcher(p.getFileName().toString()).find();
        }
        this.image = img;
        this.isExcludedPathPredicate = excludedPathPredicate;
    }

    private Image getImage(Path path) {
        return Files.isDirectory(path) ? PathMatchType.FOLDER.getImage() : PathMatchType.FILE.getImage();
    }

    public boolean isExcludedPath(Path path) {
        return isExcludedPathPredicate.test(path);
    }

    @Override
    public String toString() {
        return value;
    }

}
