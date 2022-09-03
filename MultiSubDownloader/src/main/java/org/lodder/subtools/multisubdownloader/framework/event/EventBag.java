package org.lodder.subtools.multisubdownloader.framework.event;

import java.util.Collection;
import java.util.HashMap;

public class EventBag {
    protected HashMap<String, Object> attributes = new HashMap<>();

    public Object getAttribute(String name) {
        Object attribute = null;
        if (this.attributes.containsKey(name)) {
            attribute = this.attributes.get(name);
        }

        return attribute;
    }

    public Collection<String> getAttributeNames() {
        return this.attributes.keySet();
    }

    public void setAttribute(String name, Object object) {
        this.attributes.put(name, object);
    }
}
