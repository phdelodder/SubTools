package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import java.util.function.Function;

public interface MyTextFieldToStringMapperIntf<T, R extends MyTextFieldCommon<T, R>> {
    MyTextFieldToObjectMapperIntf<T, R> withToStringMapper(Function<T, String> toStringMapper);
}
