package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;

import java.awt.*;
import java.io.File;

public class MemoryFolderChooser {

    private JFileChooser chooser;
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

    public File selectDirectory(Component c, String title, File file) {
        chooser.setDialogTitle(title);
        if (file == null || !"".equals(file.getAbsolutePath())) {
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
            return chooser.getSelectedFile();
        }
        return new File("");
    }

    public File selectDirectory(Component c, String title) {
        return selectDirectory(c, title, memory);
    }

    public void setMemory(File memory) {
        this.memory = memory;
    }

    public File getMemory() {
        return memory;
    }

}
