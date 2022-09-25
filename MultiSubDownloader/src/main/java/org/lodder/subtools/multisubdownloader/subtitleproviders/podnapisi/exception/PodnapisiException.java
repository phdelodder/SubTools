package org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.exception;

import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

public class PodnapisiException extends SubtitlesProviderException {

    private static final long serialVersionUID = -2390367212064062005L;

    public PodnapisiException() {
        super();
    }

    public PodnapisiException(String message) {
        super(message);
    }

    public PodnapisiException(String message, Throwable cause) {
        super(message, cause);
    }

    public PodnapisiException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getSubtitleProvider() {
        return SubtitleSource.PODNAPISI.getName();
    }
}
