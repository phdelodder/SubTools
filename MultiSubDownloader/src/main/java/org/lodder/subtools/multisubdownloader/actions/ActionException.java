package org.lodder.subtools.multisubdownloader.actions;

import java.io.Serial;

import lombok.experimental.StandardException;

@StandardException
public class ActionException extends Exception {

    @Serial
    private static final long serialVersionUID = -7453153452045851404L;

    protected ActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
