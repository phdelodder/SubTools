package org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.exception;

import java.io.Serial;

import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

import lombok.experimental.StandardException;

@StandardException
public class PodnapisiException extends SubtitlesProviderException {

    @Serial
    private static final long serialVersionUID = -2390367212064062005L;

    @Override
    public String getSubtitleProvider() {
        return SubtitleSource.PODNAPISI.getName();
    }
}
