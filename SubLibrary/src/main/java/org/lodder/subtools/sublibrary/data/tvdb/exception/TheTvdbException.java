package org.lodder.subtools.sublibrary.data.tvdb.exception;

import java.io.Serial;

public class TheTvdbException extends Exception {

    @Serial
    private static final long serialVersionUID = 230737234160207201L;

    public TheTvdbException() {
        super();
    }

    public TheTvdbException(String message) {
        super(message);
    }

    public TheTvdbException(String message, Throwable cause) {
        super(message, cause);
    }

    public TheTvdbException(Throwable cause) {
        super(cause);
    }

    protected TheTvdbException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
