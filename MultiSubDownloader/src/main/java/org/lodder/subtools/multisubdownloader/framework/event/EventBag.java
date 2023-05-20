package org.lodder.subtools.multisubdownloader.framework.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EventBag {
    protected final Map<String, Object> attributes = new HashMap<>();

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public Collection<String> getAttributeNames() {
        return this.attributes.keySet();
    }

    public void setAttribute(String name, Object object) {
        this.attributes.put(name, object);
    }
}
