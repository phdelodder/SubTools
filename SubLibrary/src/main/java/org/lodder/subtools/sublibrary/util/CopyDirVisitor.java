package org.lodder.subtools.sublibrary.util;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CopyDirVisitor extends SimpleFileVisitor<Path> {

    private final Path fromPath;

    private final Path toPath;

    private final StandardCopyOption[] copyOptions;

    public CopyDirVisitor(Path fromPath, Path toPath) {
        this(fromPath, toPath, new StandardCopyOption[] { StandardCopyOption.REPLACE_EXISTING });
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, @Nullable BasicFileAttributes attrs) throws IOException {

        Path targetPath = toPath.resolve(fromPath.relativize(dir));
        if (!Files.exists(targetPath)) {
            Files.createDirectory(targetPath);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, @Nullable BasicFileAttributes attrs) throws IOException {
        Files.copy(file, toPath.resolve(fromPath.relativize(file)), copyOptions);
        return FileVisitResult.CONTINUE;
    }
}
