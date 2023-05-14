package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.exception;

import java.io.Serial;

import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

import lombok.experimental.StandardException;

@StandardException
public class Addic7edException extends SubtitlesProviderException {

    @Serial
    private static final long serialVersionUID = -9050358290926245586L;

    @Override
    public String getSubtitleProvider() {
        return SubtitleSource.ADDIC7ED.getName();
    }
}
