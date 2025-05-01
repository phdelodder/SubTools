package org.lodder.subtools.sublibrary.util.filefilter;

import javax.swing.filechooser.*;
import java.io.File;

import manifold.ext.props.rt.api.val;

public abstract sealed class ExtensionFileFilter extends FileFilter permits JsonFileFilter, XmlFileFilter {

    @val abstract String extension;

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || extension.equals(f.toPath().getExtension());
    }
}
