package org.lodder.subtools.multisubdownloader.serviceproviders;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.Local;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;

public class LocalServiceProvider implements ServiceProvider {

    private UserInteractionHandler userInteractionHandler;
    private Container app;
    private SubtitleProvider subtitleProvider;
    /* We define a priority lower than SubtitleServiceProvider */
    @val @override int priority = 1;

    @Override
    public void register(Container app, UserInteractionHandler userInteractionHandler) {
        this.app = app;
        this.userInteractionHandler = userInteractionHandler;

        /* Resolve the SubtitleProviderStore from the IoC Container */
        final SubtitleProviderStore subtitleProviderStore = app.makeSubtitleProviderStore();

        /* Create the SubtitleProvider */
        subtitleProvider = createProvider();

        /* Add the SubtitleProvider to the store */
        subtitleProviderStore.addProvider(subtitleProvider);

        /* Listen for settings-change event */
        this.registerListener(subtitleProviderStore);
    }

    private SubtitleProvider createProvider() {
        return new Local(app.makeSettings(), app.makeManager(), userInteractionHandler);
    }

    private void registerListener(final SubtitleProviderStore subtitleProviderStore) {
        /* Listen for settings-change */
        app.makeEventEmitter().listen("providers.settings.change", _ -> {
            /* Change occurred, delete outdated provider from store */
            subtitleProviderStore.deleteProvider(subtitleProvider);

            /* Re-create subtitle provider */
            subtitleProvider = createProvider();

            /* Re-add provider to store */
            subtitleProviderStore.addProvider(subtitleProvider);
        });
    }
}
