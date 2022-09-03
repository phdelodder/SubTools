package org.lodder.subtools.multisubdownloader.framework.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Emitter {
    private final Map<String, ArrayList<Handler>> eventListeners = new HashMap<>();

    public void fire(Event event) {
        String eventName = event.getEventName();
        List<Handler> handlers = this.eventListeners.get(eventName);
        if (handlers == null) {
            return;
        }
        for (Handler handler : handlers) {
            handler.handle(event);
        }

    }

    public void listen(String eventName, Handler handler) {

        ArrayList<Handler> handlers;

        if (this.eventListeners.containsKey(eventName)) {
            handlers = this.eventListeners.get(eventName);
        } else {
            handlers = new ArrayList<>();
            this.eventListeners.put(eventName, handlers);
        }

        handlers.add(handler);
    }

    public void unlisten(String eventName, Handler handler) {
        if (!this.eventListeners.containsKey(eventName)) {
            return;
        }

        ArrayList<Handler> handlers = this.eventListeners.get(eventName);
        handlers.remove(handler);
    }
}
