package org.lodder.subtools.multisubdownloader.framework;

import java.util.HashMap;
import java.util.Map;

import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;

public class Container {

    private final Map<String, LazySupplier<Object>> bindings = new HashMap<>();

    public void bind(String name, LazySupplier<Object> resolver) {
        bindings.put(name, resolver);
    }

    public Object make(String name) {
        return bindings.get(name).get();
    }
}
