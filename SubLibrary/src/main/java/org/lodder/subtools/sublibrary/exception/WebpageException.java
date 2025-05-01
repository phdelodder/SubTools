package org.lodder.subtools.sublibrary.exception;

import java.io.IOException;
import java.io.Serial;

import lombok.experimental.StandardException;

@StandardException
public class WebpageException extends IOException {
    @Serial
    private static final long serialVersionUID = 1L;
}
