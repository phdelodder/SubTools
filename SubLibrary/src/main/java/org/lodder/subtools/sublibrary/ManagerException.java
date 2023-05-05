package org.lodder.subtools.sublibrary;

import java.io.Serial;

public class ManagerException extends Exception {

    @Serial
    private static final long serialVersionUID = -7937716379280570736L;

    public ManagerException() {
        super();
    }

    public ManagerException(String message) {
        super(message);
    }

    public ManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerException(Throwable cause) {
        super(cause);
    }

    protected ManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
