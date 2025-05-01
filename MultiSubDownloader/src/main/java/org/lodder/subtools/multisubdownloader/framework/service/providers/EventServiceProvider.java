package org.lodder.subtools.multisubdownloader.framework.service.providers;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.event.Emitter;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;

public class EventServiceProvider implements ServiceProvider {

    @val @override int priority = 0;

    @Override
    public void register(Container app, UserInteractionHandler userInteractionHandler) {
        // Add EventEmitter to container
        app.bind("EventEmitter", new LazySupplier<>(Emitter::new));
    }
}
