package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import java.util.function.Function;

public interface MyTextFieldToObjectMapperIntf<T, R extends MyTextFieldCommon<T, R>> {
    MyTextFieldOthersIntf<T, R> withToObjectMapper(Function<String, T> toObjectMapper);
}
