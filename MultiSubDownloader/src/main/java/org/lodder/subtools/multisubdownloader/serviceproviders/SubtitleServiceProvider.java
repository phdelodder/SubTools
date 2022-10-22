package org.lodder.subtools.multisubdownloader.serviceproviders;

import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;

public class SubtitleServiceProvider implements ServiceProvider {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void register(Container app, UserInteractionHandler userInteractionHandler) {
        app.bind("SubtitleProviderStore", new LazySupplier<>(SubtitleProviderStore::new));
    }
}
