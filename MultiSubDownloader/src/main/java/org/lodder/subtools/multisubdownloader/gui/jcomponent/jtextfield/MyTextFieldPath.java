package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import java.io.Serial;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

public class MyTextFieldPath extends MyTextFieldCommon<Path, MyTextFieldPath> {
    @Serial
    private static final long serialVersionUID = -8526638589445703452L;

    private static final Function<Path, String> TO_STRING_MAPPER = path -> path == null ? null : path.toAbsolutePath().toString();
    private static final Function<String, Path> TO_OBJECT_MAPPER = s -> s == null ? null : Path.of(s);
    public static final Predicate<String> ABSOLUTE_PATH_VERIFIER = text -> {
        try {
            return StringUtils.isBlank(text) || Path.of(text).isAbsolute();
        } catch (InvalidPathException e) {
            return false;
        }
    };

    private MyTextFieldPath() {

    }

    public static <T> MyTextFieldOthersIntf<Path, MyTextFieldPath> builder() {
        return new MyTextFieldPath()
                .withToStringMapper(TO_STRING_MAPPER)
                .withToObjectMapper(TO_OBJECT_MAPPER)
                .withValueVerifier(ABSOLUTE_PATH_VERIFIER);
    }
}
