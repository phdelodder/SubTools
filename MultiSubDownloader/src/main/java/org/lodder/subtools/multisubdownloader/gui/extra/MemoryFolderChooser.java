package org.lodder.subtools.multisubdownloader.gui.extra;

import java.io.File;
import java.util.Optional;

import javax.swing.JFileChooser;

import org.apache.commons.lang3.StringUtils;

import java.awt.Component;

public class MemoryFolderChooser {

    private final JFileChooser chooser;
    private static MemoryFolderChooser instance;
    private File memory;

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

    public Optional<File> selectDirectory(Component c, String title, File file) {
        chooser.setDialogTitle(title);
        if (file == null || !StringUtils.isBlank(file.getAbsolutePath())) {
            if (memory == null) {
                chooser.setCurrentDirectory(new File("."));
            } else {
                chooser.setCurrentDirectory(memory);
            }
        } else {
            chooser.setCurrentDirectory(file);
        }

        int result = chooser.showOpenDialog(c);
        if (result == JFileChooser.APPROVE_OPTION) {
            memory = chooser.getSelectedFile();
            return Optional.of(chooser.getSelectedFile());
        }
        return Optional.empty();
    }

    public Optional<File> selectDirectory(Component c, String title) {
        return selectDirectory(c, title, memory);
    }

    public void setMemory(File memory) {
        this.memory = memory;
    }

    public File getMemory() {
        return memory;
    }

}
