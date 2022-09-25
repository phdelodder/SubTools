package org.lodder.subtools.sublibrary.data.imdb.exception;

public class ImdbException extends Exception {

    private static final long serialVersionUID = 8887410537703318009L;

    public ImdbException(String s, String url, Exception e) {
        super(s + " :" + url, e);
    }

}
