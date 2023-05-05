package org.lodder.subtools.sublibrary.exception;

import java.io.Serial;

public class ReleaseParseException extends Exception {

    @Serial
    private static final long serialVersionUID = 9931814260806718L;

    public ReleaseParseException(String exception) {
        super(exception);
    }
}
