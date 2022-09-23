package org.lodder.subtools.sublibrary.data.tvrage;

public class TvrageException extends Exception {

    private static final long serialVersionUID = 230737234160207201L;

    public TvrageException() {
        super();
    }

    public TvrageException(String message) {
        super(message);
    }

    public TvrageException(String message, Throwable cause) {
        super(message, cause);
    }

    public TvrageException(Throwable cause) {
        super(cause);
    }
}
