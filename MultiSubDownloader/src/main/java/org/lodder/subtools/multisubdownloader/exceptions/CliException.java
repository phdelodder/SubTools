package org.lodder.subtools.multisubdownloader.exceptions;

public class CliException extends Exception {

    private static final long serialVersionUID = -9050358290926245586L;

    public CliException() {
        super();
    }

    public CliException(String message) {
        super(message);
    }

    public CliException(String message, Throwable cause) {
        super(message, cause);
    }

    public CliException(Throwable cause) {
        super(cause);
    }
}
