package org.lodder.subtools.multisubdownloader.framework.service.providers;

import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.event.Emitter;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;

public class EventServiceProvider implements ServiceProvider {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void register(Container app, UserInteractionHandler userInteractionHandler) {
        // EventEmitter toevoegen aan container
        app.bind("EventEmitter", new LazySupplier<>(Emitter::new));
    }
}
