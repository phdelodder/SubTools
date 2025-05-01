package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import java.io.Serial;
import java.util.function.Function;
import java.util.function.Predicate;

public final class MyTextFieldString extends MyTextFieldCommon<String, MyTextFieldString> {
    @Serial
    private static final long serialVersionUID = -8526638589445703452L;

    private static final Function<String, String> TO_STRING_MAPPER = Function.identity();
    private static final Function<String, String> TO_OBJECT_MAPPER = Function.identity();
    public static final Predicate<String> VERIFIER = _ -> true;

    private MyTextFieldString() {

    }

    public static MyTextFieldOthersIntf<String, MyTextFieldString> builder() {
        return new MyTextFieldString()
            .withToStringMapper(TO_STRING_MAPPER)
            .withToObjectMapper(TO_OBJECT_MAPPER)
            .withValueVerifier(VERIFIER);
    }

    public static MyTextFieldOthersIntf<String, MyTextFieldString> create() {
        return builder().build();
    }
}
