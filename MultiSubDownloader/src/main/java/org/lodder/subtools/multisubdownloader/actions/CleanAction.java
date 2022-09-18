package org.lodder.subtools.multisubdownloader.actions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.lodder.subtools.multisubdownloader.settings.model.LibrarySettings;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.util.FilenameContainsFilter;
import org.lodder.subtools.sublibrary.util.FilenameExtensionFilter;
import org.lodder.subtools.sublibrary.util.Files;
import org.lodder.subtools.sublibrary.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanAction.class);

    private final LibrarySettings librarySettings;
    private final String[] fileFilters = { "nfo", "jpg", "sfv", "srr", "srs", "nzb", "torrent", "txt" };
    private final String[] folderFilters = { "sample", "Sample" };
    private final static String sampleDirName = "sample";

    public CleanAction(LibrarySettings librarySettings) {
        this.librarySettings = librarySettings;
    }

    public void cleanUpFiles(Release release, File path, String videoFileName) throws IOException {
        LOGGER.trace("cleanUpFiles: LibraryOtherFileAction", librarySettings.getLibraryOtherFileAction());

        String[] files = release.getPath().list(new FilenameExtensionFilter(fileFilters));
        if (files == null) {
            files = new String[] {};
        }

        String[] folders = release.getPath().list(new FilenameContainsFilter(folderFilters));
        if (folders == null) {
            folders = new String[] {};
        }

        // remove duplicates using set
        final Set<String> list = new LinkedHashSet<>(Arrays.asList(StringUtils.join(files, folders)));

        switch (librarySettings.getLibraryOtherFileAction()) {
            case MOVE -> doMove(release, list, path);
            case MOVEANDRENAME -> doMoveAndRename(release, list, path, videoFileName);
            case REMOVE -> doRemove(release, list);
            case RENAME -> doRename(release, path, videoFileName, files);
            case NOTHING -> {}
            default -> {}
        }
    }

    private void doRename(Release release, File path, String videoFileName, String[] files) throws IOException {
        for (String s : files) {
            String extension = ReleaseParser.extractFileNameExtension(s);

            File f = new File(release.getPath(), s);

            if (s.contains(sampleDirName) && !f.isDirectory()) {
                extension = sampleDirName + "." + extension;
            }

            if (f.isFile()) {
                final String filename = videoFileName.substring(0, videoFileName.lastIndexOf(".")).concat("." + extension);
                Files.move(f, new File(release.getPath(), filename));
            } else {
                Files.move(f, new File(path, s));
            }
        }
    }

    private void doRemove(Release release, Set<String> list) throws IOException {
        for (String s : list) {
            File file = new File(release.getPath(), s);
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            } else {
                file.delete();
            }
        }
    }

    private void doMoveAndRename(Release release, Set<String> list, File path, String videoFileName) throws IOException {
        for (String s : list) {
            String extension = ReleaseParser.extractFileNameExtension(s);

            File f = new File(release.getPath(), s);

            if (s.contains(sampleDirName) && !f.isDirectory()) {
                extension = sampleDirName + "." + extension;
            }

            if (f.isFile()) {
                final String filename = videoFileName.substring(0, videoFileName.lastIndexOf(".")).concat("." + extension);
                Files.move(f, new File(path, filename));
            } else {
                Files.move(f, new File(path, s));
            }
        }
    }

    private void doMove(Release release, Set<String> list, File path) throws IOException {
        for (String s : list) {
            Files.move(new File(release.getPath(), s), new File(path, s));
        }
    }
}
