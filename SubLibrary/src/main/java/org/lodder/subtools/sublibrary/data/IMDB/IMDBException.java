package org.lodder.subtools.sublibrary.data.IMDB;

public class IMDBException  extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8887410537703318009L;

	public IMDBException(String s, String url) {
        super(s + " :" + url);
    }

}
