package org.lodder.subtools.sublibrary.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class XmlFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		// Accept all directories and all xml
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		return extension.equals("xml");
	}

	@Override
	public String getDescription() {
		return "xml Files";
	}

	public static String getExtension(File f) {
		String ext = "";
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}
