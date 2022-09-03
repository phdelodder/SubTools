package org.lodder.subtools.sublibrary.util.http;

public class HttpClientSetupException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 7350679372823335661L;

    public HttpClientSetupException() {
        super();
    }

    public HttpClientSetupException(String message) {
        super(message);
    }

    public HttpClientSetupException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientSetupException(Throwable cause) {
        super(cause);
    }

    protected HttpClientSetupException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
