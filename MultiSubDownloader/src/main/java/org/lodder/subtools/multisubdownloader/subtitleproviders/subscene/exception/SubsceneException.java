package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception;

import java.io.Serial;

import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

public class SubsceneException extends SubtitlesProviderException {

    @Serial
    private static final long serialVersionUID = -9050358290926245586L;

    public SubsceneException() {
        super();
    }

    public SubsceneException(String message) {
        super(message);
    }

    public SubsceneException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubsceneException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getSubtitleProvider() {
        return SubtitleSource.SUBSCENE.getName();
    }
}
