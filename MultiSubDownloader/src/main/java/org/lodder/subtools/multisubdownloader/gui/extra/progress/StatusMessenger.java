package org.lodder.subtools.multisubdownloader.gui.extra.progress;

import java.util.LinkedList;
import java.util.List;

public enum StatusMessenger implements Messenger {
    instance;

    private final List<Messenger> statusMessengers = new LinkedList<>();

    public void addListener(Messenger sm) {
        synchronized (statusMessengers) {
            statusMessengers.add(sm);
        }
    }

    public void removeListener(Messenger sm) {
        synchronized (statusMessengers) {
            statusMessengers.remove(sm);
        }
    }

    @Override
    public void message(String message) {
        synchronized (statusMessengers) {
            statusMessengers.forEach(sm ->  sm.message(message));
        }
    }
}
