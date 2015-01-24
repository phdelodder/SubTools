package org.lodder.subtools.multisubdownloader.gui.actions;

public class ActionException extends Throwable {

  public ActionException() {
    super();
  }

  public ActionException(String message) {
    super(message);
  }

  public ActionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ActionException(Throwable cause) {
    super(cause);
  }

  protected ActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
