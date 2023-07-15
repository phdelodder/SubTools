package org.lodder.subtools.sublibrary.util.filefilter;

import javax.swing.filechooser.*;
import java.io.File;

import org.lodder.subtools.sublibrary.util.FileUtils;

public abstract class ExtensionFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || getExtension().equals(FileUtils.getExtension(f.toPath()));
    }

    public abstract String getExtension();
}
