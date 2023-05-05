package org.lodder.subtools.sublibrary;

import java.io.Serial;

public class ManagerSetupException extends Exception {

    @Serial
    private static final long serialVersionUID = -7937716379280570736L;

    public ManagerSetupException() {
        super();
    }

    public ManagerSetupException(String message) {
        super(message);
    }

    public ManagerSetupException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerSetupException(Throwable cause) {
        super(cause);
    }

    protected ManagerSetupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
