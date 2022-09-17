package org.lodder.subtools.multisubdownloader.framework.event;

import java.util.Collection;

import lombok.Getter;

@Getter
public class Event {
    private final String eventName;
    private final EventBag eventBag;

    public Event(String name) {
        this.eventName = name;
        this.eventBag = new EventBag();
    }

    public Event(String name, EventBag bag) {
        this.eventName = name;
        this.eventBag = bag;
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
