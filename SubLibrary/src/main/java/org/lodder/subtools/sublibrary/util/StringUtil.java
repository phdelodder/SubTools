package org.lodder.subtools.sublibrary.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;

@ExtensionMethod({ StringUtils.class })
@UtilityClass
public class StringUtil {

    public static String removeIllegalFilenameChars(String s) {
        return s.replace("/", "").replace("\0", "");
    }

    public static String removeIllegalWindowsChars(String text) {
        return text.replaceAll("[\\\\/:*?\"<>|]", "").removeEnd(".").trim();
    }

    public static String urlEncode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }
}
