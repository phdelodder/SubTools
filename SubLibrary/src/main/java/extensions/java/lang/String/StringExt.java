package extensions.java.lang.String;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

@UtilityClass
@Extension
public class StringExt {

    public static String removeIllegalFilenameChars(@This String s) {
        return s.replace("/", "").replace("\0", "");
    }

    public static String removeIllegalWindowsChars(@This String text) {
        return StringUtils.removeEnd(text.replaceAll("[\\\\/:*?\"<>|]", ""), ".").trim();
    }

    public static String urlEncode(@This String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

    public static InputStream toInputStream(@This String text, Charset charset) {
        return new ByteArrayInputStream(text.getBytes(charset));
    }

    public static <T extends Number> Optional<T> parseAsNumber(@This @Nullable String text,
        Function<String, T> mapper) {
        if (text == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(mapper.apply(text));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
