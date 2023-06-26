package org.lodder.subtools.sublibrary.util.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.lodder.subtools.sublibrary.util.FileUtils;

public abstract class ExtensionFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        return getExtension().equals(FileUtils.getExtension(f.toPath()));
    }

    public abstract String getExtension();
}
