package org.lodder.subtools.sublibrary.data.OMDB;

public class OMDBException extends Exception {
  /**
	 * 
	 */
  private static final long serialVersionUID = 8887410537703318009L;

  public OMDBException(String s, String url, Exception e) {
    super(s + " :" + url, e);
  }

}
