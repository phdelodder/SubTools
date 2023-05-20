package org.lodder.subtools.sublibrary.exception;

import java.io.Serial;

public abstract class SubtitlesProviderException extends Exception {

    @Serial
    private static final long serialVersionUID = -2959483164333075297L;

    public SubtitlesProviderException() {
        super();
    }

    public SubtitlesProviderException(String message) {
        super(message);
    }

    public SubtitlesProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubtitlesProviderException(Throwable cause) {
        super(cause);
    }

    public abstract String getSubtitleProvider();
}
