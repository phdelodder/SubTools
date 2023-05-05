package org.lodder.subtools.sublibrary.exception;

import java.io.Serial;

import org.lodder.subtools.sublibrary.model.Release;

public class ReleaseControlException extends Exception {

    public ReleaseControlException(String string, Release release) {
        super(string + ": " + release.toString());
    }

    @Serial
    private static final long serialVersionUID = 1958337660409078923L;

}
