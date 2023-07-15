package org.lodder.subtools.sublibrary.exception;

import java.io.Serial;

import lombok.experimental.StandardException;

@StandardException
public abstract class SubtitlesProviderException extends Exception {

    @Serial
    private static final long serialVersionUID = -2959483164333075297L;

    public abstract String getSubtitleProvider();
}
