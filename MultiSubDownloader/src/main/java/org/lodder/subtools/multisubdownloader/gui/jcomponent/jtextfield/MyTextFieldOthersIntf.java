package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.lodder.subtools.sublibrary.util.function.BooleanConsumer;

public interface MyTextFieldOthersIntf<T, R extends MyTextFieldCommon<T, R>> {
    MyTextFieldOthersIntf<T, R> withValueVerifier(Predicate<String> verifier);

    default MyTextFieldOthersIntf<T, R> requireValue() {
        return requireValue(true);
    }

    MyTextFieldOthersIntf<T, R> requireValue(boolean requireValue);

    MyTextFieldOthersIntf<T, R> withValueChangedCallback(Consumer<T> valueChangedCallbackListener);

    MyTextFieldOthersIntf<T, R> withValidityChangedCallback(BooleanConsumer... validityChangedCallbackListeners);

    R build();
}
