package org.lodder.subtools.sublibrary.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.TreeSet;

public class FilenameContainsFilter implements FilenameFilter {
    private final TreeSet<String> contains = new TreeSet<>();

    public FilenameContainsFilter(String s) {
        contains.add(s.trim());
    }

    public FilenameContainsFilter(String[] sarray) {
        Arrays.stream(sarray).map(String::trim).filter(""::equals).forEach(contains::add);
    }

    @Override
    public boolean accept(File dir, String name) {
        return contains.stream().anyMatch(name::contains);
    }
}
