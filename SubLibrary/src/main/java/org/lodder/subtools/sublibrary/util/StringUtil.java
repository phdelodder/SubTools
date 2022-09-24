package org.lodder.subtools.sublibrary.util;

public class StringUtil {

    public static String[] join(String[]... arrays) {
        // calculate size of target array
        int size = 0;
        for (String[] array : arrays) {
            size += array.length;
        }

        // create list of appropriate size
        java.util.List<String> list = new java.util.ArrayList<>(size);

        // add arrays
        for (String[] array : arrays) {
            list.addAll(java.util.Arrays.asList(array));
        }

        // create and return final array
        return list.toArray(new String[size]);
    }

    public static String removeIllegalFilenameChars(String s) {
        s = s.replace("/", "");
        return s.replace("\0", "");
    }

    public static String removeIllegalWindowsChars(String text) {
        text = text.replace("|", "");
        text = text.replace("\"", "");
        text = text.replace("<", "");
        text = text.replace(">", "");
        text = text.replace("?", "");
        text = text.replace("*", "");
        text = text.replace(":", "");
        text = text.replace("/", "");
        text = text.replace("\\", "");
        if (text.length() > 0 && ".".equals(text.substring(text.length() - 1))) {
            text = text.substring(0, text.length() - 1);
        }
        return text.trim();
    }
}
