package org.lodder.subtools.multisubdownloader.gui.actions.search;

import org.lodder.subtools.multisubdownloader.gui.actions.ActionException;

public class SearchSetupException extends ActionException {

  public SearchSetupException() {
    super();
  }

  public SearchSetupException(String message) {
    super(message);
  }

  public SearchSetupException(String message, Throwable cause) {
    super(message, cause);
  }

  public SearchSetupException(Throwable cause) {
    super(cause);
  }

  protected SearchSetupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
