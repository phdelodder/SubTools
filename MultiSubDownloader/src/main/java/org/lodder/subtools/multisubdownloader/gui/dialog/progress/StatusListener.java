package org.lodder.subtools.multisubdownloader.gui.dialog.progress;

import org.lodder.subtools.multisubdownloader.actions.ActionException;

public interface StatusListener {

  public void onError(ActionException exception);

  public void onStatus(String message);

}
