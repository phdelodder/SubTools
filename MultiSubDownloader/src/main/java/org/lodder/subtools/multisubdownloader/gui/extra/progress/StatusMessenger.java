package org.lodder.subtools.multisubdownloader.gui.extra.progress;

import java.util.LinkedList;
import java.util.List;

public enum StatusMessenger implements Messenger {
    instance;

    private final List<Messenger> statusMessagers = new LinkedList<>();

    public void addListener(Messenger sm) {
        synchronized (statusMessagers) {
            statusMessagers.add(sm);
        }
    }

    public void removeListener(Messenger sm) {
        synchronized (statusMessagers) {
            statusMessagers.remove(sm);
        }
    }

    @Override
    public void message(String message) {
        synchronized (statusMessagers) {
            for (Messenger sm : statusMessagers) {
                sm.message(message);
            }
        }
    }
}
