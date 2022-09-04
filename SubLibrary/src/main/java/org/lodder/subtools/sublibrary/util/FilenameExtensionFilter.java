/**
 * found add http://www.rgagnon.com/javadetails/java-0055.html
 */

package org.lodder.subtools.sublibrary.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * <CODE>
 * GenericFileFilter xml = new GenericFileFilter ("xml");
 * // GenericFileFilter xmlandpdf = new GenericFileFilter (new String [] { "xml", "pdf" });
 * File dir = new File (".");
 * String[] strs = dir.list(xml);
 * for (int i = 0; i < strs.length; i++) {
 * // strs[i]
 * }
 * </CODE>
 */

public class FilenameExtensionFilter implements FilenameFilter {
    private final TreeSet<String> exts = new TreeSet<>();

    public FilenameExtensionFilter(String ext) {
        exts.add("." + ext.toLowerCase().trim());
    }

    public FilenameExtensionFilter(String[] extensions) {
        Arrays.stream(extensions).map(s -> s.toLowerCase().trim()).filter(""::equals).forEach(exts::add);
    }

    @Override
    public boolean accept(File dir, String name) {
        return exts.stream().anyMatch(name.toLowerCase()::endsWith);
    }
}
