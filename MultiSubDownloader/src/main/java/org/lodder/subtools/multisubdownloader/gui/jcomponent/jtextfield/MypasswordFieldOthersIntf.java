package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import java.util.function.Consumer;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

public interface MypasswordFieldOthersIntf {
    MypasswordFieldOthersIntf withValueVerifier(Predicate<String> verifier);

    default MypasswordFieldOthersIntf requireValue() {
        return requireValue(true);
    }

    MypasswordFieldOthersIntf requireValue(boolean requireValue);

    MypasswordFieldOthersIntf withValueChangedCallback(Consumer<String> valueChangedCalbackListener);

    MypasswordFieldOthersIntf withValidityChangedCallback(BooleanConsumer... validityChangedCalbackListeners);

    MyPasswordField build();
}
