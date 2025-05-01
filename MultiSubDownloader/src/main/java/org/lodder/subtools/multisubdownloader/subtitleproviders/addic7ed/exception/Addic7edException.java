package org.lodder.subtools.multisubdownloader.subtitleproviders.addic7ed.exception;

import java.io.Serial;

import lombok.experimental.StandardException;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

@StandardException
public class Addic7edException extends SubtitlesProviderException {

    @Serial
    private static final long serialVersionUID = -9050358290926245586L;

    @Override
    public String getSubtitleProvider() {
        return SubtitleSource.ADDIC7ED.name;
    }
}
