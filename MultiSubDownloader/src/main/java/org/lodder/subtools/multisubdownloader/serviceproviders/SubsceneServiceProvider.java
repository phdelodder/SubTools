package org.lodder.subtools.multisubdownloader.serviceproviders;

import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.multisubdownloader.subtitleproviders.adapters.JSubsceneAdapter;
import org.lodder.subtools.sublibrary.Manager;

public class SubsceneServiceProvider implements ServiceProvider {

    protected Container app;
    protected SubtitleProvider subtitleProvider;

    @Override
    public int getPriority() {
        /* We define a priority lower than SubtitleServiceProvider */
        return 1;
    }

    @Override
    public void register(Container app, UserInteractionHandler userInteractionHandler) {
        /* Resolve the SubtitleProviderStore from the IoC Container */
        SubtitleProviderStore subtitleProviderStore = (SubtitleProviderStore) app.make("SubtitleProviderStore");

        /* Create the SubtitleProvider */
        Manager manager = (Manager) app.make("Manager");
        JSubsceneAdapter subsceneAdapter = new JSubsceneAdapter(manager, userInteractionHandler);

        /* Add the SubtitleProvider to the store */
        subtitleProviderStore.addProvider(subsceneAdapter);
    }
}
