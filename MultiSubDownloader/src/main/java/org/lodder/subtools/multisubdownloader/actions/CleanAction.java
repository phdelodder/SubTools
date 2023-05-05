package org.lodder.subtools.multisubdownloader.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.util.FileUtils;
import org.lodder.subtools.sublibrary.util.StreamExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ StringUtils.class, FileUtils.class, Files.class, StreamExtension.class })
public class CleanAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanAction.class);

    private final LibrarySettings librarySettings;
    private final Set<String> fileFilters = Set.of("nfo", "jpg", "sfv", "srr", "srs", "nzb", "torrent", "txt");
    private final static String sampleDirName = "sample";

    public CleanAction(LibrarySettings librarySettings) {
        this.librarySettings = librarySettings;
    }

    public void cleanUpFiles(Release release, Path destination, String videoFileName) throws IOException {
        LOGGER.trace("cleanUpFiles: LibraryOtherFileAction {}", librarySettings.getLibraryOtherFileAction());
        if (!destination.isDirectory()) {
            throw new IllegalArgumentException("Destination [%s] is not a folder".formatted(destination));
        }

        release.getPath().list().asThrowingStream(IOException.class)
                .filter(p -> (p.isDirectory() && p.fileNameContainsIgnoreCase(sampleDirName))
                        || (p.isRegularFile() && fileFilters.contains(p.getExtension())))
                .forEach(p -> {
                    switch (librarySettings.getLibraryOtherFileAction()) {
                        case MOVE -> move(p, destination);
                        case MOVEANDRENAME -> moveAndRename(p, destination, videoFileName);
                        case REMOVE -> delete(p);
                        case RENAME -> rename(p, destination, videoFileName);
                        case NOTHING -> {}
                        default -> {}
                    }
                });
    }

    private void rename(Path path, Path destinationFolder, String videoFileName) throws IOException {
        if (path.isRegularFile()) {
            String fileName = path.fileNameContainsIgnoreCase(sampleDirName) ? sampleDirName : StringUtils.substringBeforeLast(videoFileName, ".");
            String extension = path.getExtension();
            if (!extension.isBlank()) {
                extension = "." + extension;
            }
            path.move(path.resolveSibling(fileName + extension));
        } else {
            path.moveToDir(destinationFolder);
        }
    }

    private void delete(Path path) throws IOException {
        FileUtils.delete(path);
    }

    private void moveAndRename(Path path, Path destinationFolder, String videoFileName) throws IOException {
        if (path.isRegularFile()) {
            String fileName = path.fileNameContainsIgnoreCase(sampleDirName) ? sampleDirName : StringUtils.substringBeforeLast(videoFileName, ".");
            String extension = path.getExtension();
            if (!extension.isBlank()) {
                extension = "." + extension;
            }
            path.moveToDirAndRename(destinationFolder, fileName + extension);
        } else {
            path.moveToDir(destinationFolder);
        }
    }

    private void move(Path origin, Path destinationFolder) throws IOException {
        FileUtils.moveToDir(origin, destinationFolder, StandardCopyOption.REPLACE_EXISTING);
    }

}
