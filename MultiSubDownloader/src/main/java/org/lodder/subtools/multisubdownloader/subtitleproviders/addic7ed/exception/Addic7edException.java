package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.exception;

import java.io.Serial;

import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

public class Addic7edException extends SubtitlesProviderException {

    @Serial
    private static final long serialVersionUID = -9050358290926245586L;

    public Addic7edException() {
        super();
    }

    public Addic7edException(String message) {
        super(message);
    }

    public Addic7edException(String message, Throwable cause) {
        super(message, cause);
    }

    public Addic7edException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getSubtitleProvider() {
        return SubtitleSource.ADDIC7ED.getName();
    }
}
