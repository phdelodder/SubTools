package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import manifold.ext.props.rt.api.var;
import org.apache.commons.lang3.StringUtils;

public class MemoryFolderChooser {

    private static MemoryFolderChooser instance;
    private final JFileChooser chooser;
    @var Path memory;

    private MemoryFolderChooser() {
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
    }

    public static MemoryFolderChooser getInstance() {
        if (instance == null) {
            instance = new MemoryFolderChooser();
        }
        return instance;
    }

    public Optional<Path> selectDirectory(Component c, String title, Path path) {
        return selectDirectory(c, title, path.toFile());
    }

    public Optional<Path> selectDirectory(Component c, String title, File file) {
        chooser.setDialogTitle(title);
        if (file == null || !StringUtils.isBlank(file.getAbsolutePath())) {
            chooser.setCurrentDirectory(Objects.requireNonNullElseGet(memory.toFile(), () -> new File(".")));
        } else {
            chooser.setCurrentDirectory(file);
        }

        int result = chooser.showOpenDialog(c);
        if (result == JFileChooser.APPROVE_OPTION) {
            memory = chooser.getSelectedFile().toPath();
            return Optional.of(chooser.getSelectedFile().toPath());
        }
        return Optional.empty();
    }

    public Optional<Path> selectDirectory(Component c, String title) {
        return selectDirectory(c, title, memory);
    }
}
