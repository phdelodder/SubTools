package org.lodder.subtools.sublibrary.data.imdb.exception;

import java.io.Serial;

public class ImdbSearchIdException extends Exception {

    @Serial
    private static final long serialVersionUID = 8887410537703318009L;

    public ImdbSearchIdException(String s, String url, Exception e) {
        super(s + " :" + url, e);
    }

}
