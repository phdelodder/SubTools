package org.lodder.subtools.sublibrary.data.omdb.exception;

public class OmdbException extends Exception {

    private static final long serialVersionUID = 8887410537703318009L;

    public OmdbException(String s, String url, Exception e) {
        super(s + " :" + url, e);
    }

}
