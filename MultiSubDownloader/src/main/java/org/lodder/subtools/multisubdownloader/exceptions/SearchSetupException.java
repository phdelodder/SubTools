package org.lodder.subtools.multisubdownloader.exceptions;

import java.io.Serial;

import org.lodder.subtools.multisubdownloader.actions.ActionException;

import lombok.experimental.StandardException;

@StandardException
public class SearchSetupException extends ActionException {

    @Serial
    private static final long serialVersionUID = 7068109511106327509L;


    protected SearchSetupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
