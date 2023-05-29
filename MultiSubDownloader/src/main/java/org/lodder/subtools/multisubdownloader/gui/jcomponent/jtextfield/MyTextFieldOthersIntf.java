package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import java.util.function.Consumer;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

public interface MyTextFieldOthersIntf<T, R extends MyTextFieldCommon<T, R>> {
    MyTextFieldOthersIntf<T, R> withValueVerifier(Predicate<String> verifier);

    MyTextFieldOthersIntf<T, R> requireValue();

    MyTextFieldOthersIntf<T, R> requireValue(boolean requireValue);

    MyTextFieldOthersIntf<T, R> withValueChangedCallback(Consumer<T> valueChangedCalbackListener);

    MyTextFieldOthersIntf<T, R> withValidityChangedCallback(BooleanConsumer... validityChangedCalbackListeners);

    R build();
}
