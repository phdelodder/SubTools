package org.lodder.subtools.multisubdownloader.serviceproviders;

import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;

public class SubtitleServiceProvider implements ServiceProvider {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void register(Container app) {
        final SubtitleProviderStore store = new SubtitleProviderStore();
        app.bind("SubtitleProviderStore", () -> store);
    }
}
