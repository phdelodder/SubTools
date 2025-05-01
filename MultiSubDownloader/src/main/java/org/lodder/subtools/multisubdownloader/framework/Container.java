package org.lodder.subtools.multisubdownloader.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.lodder.subtools.multisubdownloader.framework.event.Emitter;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;

public class Container {

    private final Map<String, LazySupplier<Object>> bindings = new HashMap<>();

    public void bind(String name, LazySupplier<Object> resolver) {
        bindings.put(name, resolver);
    }

    public Object make(String name) {
        return bindings.get(name).get();
    }

    public SubtitleProviderStore makeSubtitleProviderStore() {
        return (SubtitleProviderStore) make("SubtitleProviderStore");
    }

    public Manager makeManager() {
        return (Manager) make("Manager");
    }

    public Settings makeSettings() {
        return (Settings) make("Settings");
    }

    public Preferences makePreferences() {
        return (Preferences) make("Preferences");
    }

    public Emitter makeEventEmitter() {
        return (Emitter) make("EventEmitter");
    }
}
