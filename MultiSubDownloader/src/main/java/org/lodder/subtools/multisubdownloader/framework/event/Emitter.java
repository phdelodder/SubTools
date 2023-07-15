package org.lodder.subtools.multisubdownloader.framework.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Emitter {
    private final Map<String, List<Handler>> eventListeners = new HashMap<>();

    public void fire(Event event) {
        List<Handler> handlers = this.eventListeners.get(event.getEventName());
        if (handlers != null) {
            handlers.forEach(handler -> handler.handle(event));
        }
    }

    public void listen(String eventName, Handler handler) {
        eventListeners.computeIfAbsent(eventName, k -> new ArrayList<>()).add(handler);
    }

    public void unlisten(String eventName, Handler handler) {
        List<Handler> handlers = this.eventListeners.get(eventName);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }
}
