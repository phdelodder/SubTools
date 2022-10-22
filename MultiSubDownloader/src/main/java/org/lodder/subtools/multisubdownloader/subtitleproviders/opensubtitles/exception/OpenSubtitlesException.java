package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.exception;

import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

public class OpenSubtitlesException extends SubtitlesProviderException {

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

    @Override
    public String getSubtitleProvider() {
        return SubtitleSource.OPENSUBTITLES.getName();
    }
}
