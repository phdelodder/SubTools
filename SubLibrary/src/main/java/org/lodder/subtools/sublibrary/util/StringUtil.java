package org.lodder.subtools.sublibrary.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

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

    public static String urlEncode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }
}
