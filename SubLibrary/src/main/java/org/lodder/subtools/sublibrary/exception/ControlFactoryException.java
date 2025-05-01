package org.lodder.subtools.sublibrary.exception;

import java.io.Serial;

public class ControlFactoryException extends Exception {

    @Serial
    private static final long serialVersionUID = -7387961966699689531L;

    public ControlFactoryException(String string) {
        super(string);
    }
}
