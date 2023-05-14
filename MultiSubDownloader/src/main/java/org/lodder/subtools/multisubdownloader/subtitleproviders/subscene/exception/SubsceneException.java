package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.exception;

import java.io.Serial;

import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

import lombok.experimental.StandardException;

@StandardException
public class SubsceneException extends SubtitlesProviderException {

    @Serial
    private static final long serialVersionUID = -9050358290926245586L;

    @Override
    public String getSubtitleProvider() {
        return SubtitleSource.SUBSCENE.getName();
    }
}
