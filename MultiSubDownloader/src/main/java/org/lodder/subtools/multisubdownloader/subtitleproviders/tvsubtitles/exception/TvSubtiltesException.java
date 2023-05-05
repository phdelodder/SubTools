package org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.exception;

import java.io.Serial;

import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

public class TvSubtiltesException extends SubtitlesProviderException {

    @Serial
    private static final long serialVersionUID = -9050358290926245586L;

    public TvSubtiltesException() {
        super();
    }

    public TvSubtiltesException(String message) {
        super(message);
    }

    public TvSubtiltesException(String message, Throwable cause) {
        super(message, cause);
    }

    public TvSubtiltesException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getSubtitleProvider() {
        return SubtitleSource.TVSUBTITLES.getName();
    }
}
