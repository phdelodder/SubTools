package org.lodder.subtools.multisubdownloader.framework.service.providers;

import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.event.Emitter;

public class EventServiceProvider implements ServiceProvider {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void register(Container app) {
        // Aanmaken EvenEmitter
        final Emitter eventEmitter = new Emitter();

        // EventEmitter toevoegen aan container
        app.bind("EventEmitter", () -> eventEmitter);
    }
}
