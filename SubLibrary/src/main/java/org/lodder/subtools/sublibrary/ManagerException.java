package org.lodder.subtools.sublibrary;

import java.io.Serial;

import lombok.experimental.StandardException;

@StandardException
public class ManagerException extends Exception {

    @Serial
    private static final long serialVersionUID = -7937716379280570736L;

    protected ManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
