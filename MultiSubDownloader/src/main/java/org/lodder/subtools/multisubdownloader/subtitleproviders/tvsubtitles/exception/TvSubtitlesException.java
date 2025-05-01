package org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.exception;

import java.io.Serial;

import lombok.experimental.StandardException;
import org.lodder.subtools.sublibrary.exception.SubtitlesProviderException;
import org.lodder.subtools.sublibrary.model.SubtitleSource;

@StandardException
public class TvSubtitlesException extends SubtitlesProviderException {

    @Serial
    private static final long serialVersionUID = -9050358290926245586L;

    @Override
    public String getSubtitleProvider() {
        return SubtitleSource.TVSUBTITLES.name;
    }
}
