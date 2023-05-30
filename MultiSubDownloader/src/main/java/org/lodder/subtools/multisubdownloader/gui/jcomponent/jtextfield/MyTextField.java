package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import java.io.Serial;

public class MyTextField<T> extends MyTextFieldCommon<T, MyTextField<T>> {

    @Serial
    private static final long serialVersionUID = 1580566911085697756L;

    private MyTextField() {
        super();
    }

    public static <T> MyTextFieldToStringMapperIntf<T, MyTextField<T>> buildForType(Class<T> type) {
        return new MyTextField<>();
    }
}
