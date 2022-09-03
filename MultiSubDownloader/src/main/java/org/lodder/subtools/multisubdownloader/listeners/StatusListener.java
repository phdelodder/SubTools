package org.lodder.subtools.multisubdownloader.listeners;

import org.lodder.subtools.multisubdownloader.actions.ActionException;

public interface StatusListener {

    void onError(ActionException exception);

    void onStatus(String message);

}
