package org.lodder.subtools.multisubdownloader.listeners;

import org.lodder.subtools.multisubdownloader.actions.ActionException;

public interface StatusListener {

    public void onError(ActionException exception);

    public void onStatus(String message);

}
