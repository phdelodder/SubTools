package org.lodder.subtools.multisubdownloader.gui.extra.progress;

import java.util.LinkedList;
import java.util.List;

import manifold.ext.props.rt.api.val;

public class StatusMessenger implements Messenger {
    @val static StatusMessenger instance = new StatusMessenger();

    private final List<Messenger> statusMessengers = new LinkedList<>();

    private StatusMessenger() {
        // private constructor to prevent instantiation
    }

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
            statusMessengers.forEach(sm -> sm.message(message));
        }
    }
}
