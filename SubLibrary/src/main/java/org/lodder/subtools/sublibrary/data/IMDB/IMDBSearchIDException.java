package org.lodder.subtools.sublibrary.data.IMDB;

public class IMDBSearchIDException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 8887410537703318009L;

    public IMDBSearchIDException(String s, String url, Exception e) {
        super(s + " :" + url, e);
    }

}
