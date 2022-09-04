package org.lodder.subtools.sublibrary.data.thetvdb;

public class TheTVDBException extends Exception {

    private static final long serialVersionUID = 230737234160207201L;

    public TheTVDBException() {
        super();
    }

    public TheTVDBException(String message) {
        super(message);
    }

    public TheTVDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public TheTVDBException(Throwable cause) {
        super(cause);
    }

    protected TheTVDBException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
