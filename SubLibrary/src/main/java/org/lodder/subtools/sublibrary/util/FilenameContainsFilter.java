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
        for (String s : Arrays.asList(sarray)) {
            contains.add(s.trim());
        }
        contains.remove("");
    }

    @Override
    public boolean accept(File dir, String name) {
        for (String contain : contains) {
            if (name.contains(contain)) {
                return true;
            }
        }
        return false;
    }
}
