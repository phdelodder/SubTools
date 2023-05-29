package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import java.io.Serial;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

public class MyTextFieldInteger extends MyTextFieldCommon<Integer, MyTextFieldInteger> {
    @Serial
    private static final long serialVersionUID = -8526638589445703452L;

    private static final Function<Integer, String> TO_STRING_MAPPER = i -> i == null ? null : String.valueOf(i);
    private static final Function<String, Integer> TO_OBJECT_MAPPER = s -> s == null ? null : Integer.parseInt(s);
    public static final Predicate<String> INT_VERIFIER = text -> {
        try {
            if (StringUtils.isBlank(text)) {
                return true;
            }
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    };

    private MyTextFieldInteger() {

    }

    public static <T> MyTextFieldOthersIntf<Integer, MyTextFieldInteger> builder() {
        return new MyTextFieldInteger()
                .withToStringMapper(TO_STRING_MAPPER)
                .withToObjectMapper(TO_OBJECT_MAPPER)
                .withValueVerifier(INT_VERIFIER);
    }
}
