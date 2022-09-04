package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.exception;

public class OpenSubtitlesException extends Exception {

    private static final long serialVersionUID = -9050358290926245586L;

    public OpenSubtitlesException() {
        super();
    }

    public OpenSubtitlesException(String message) {
        super(message);
    }

    public OpenSubtitlesException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenSubtitlesException(Throwable cause) {
        super(cause);
    }
}
