package org.lodder.subtools.multisubdownloader.framework.event;

import java.util.Collection;

public class Event {
    private String eventName;
    private EventBag eventBag;

    public Event(String name) {
        this.eventName = name;
        this.eventBag = new EventBag();
    }

    public Event(String name, EventBag bag) {
        this.eventName = name;
        this.eventBag = bag;
    }

    public String getEventName() {
        return this.eventName;
    }

    public EventBag getBag() {
        return this.eventBag;
    }

    public Object getAttribute(String name) {
        return this.eventBag.getAttribute(name);
    }

    public Collection<String> getAttributeNames() {
        return this.eventBag.getAttributeNames();
    }

    public void setAttribute(String name, Object object) {
        this.eventBag.setAttribute(name, object);
    }

}
