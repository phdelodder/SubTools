package org.lodder.subtools.multisubdownloader.serviceproviders;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProvider;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.multisubdownloader.subtitleproviders.adapters.JOpenSubAdapter;
import org.lodder.subtools.sublibrary.Credentials;

public class OpenSubtitlesServiceProvider implements ServiceProvider {

    protected Container app;
    protected SubtitleProvider subtitleProvider;
    /* We define a priority lower than SubtitleServiceProvider */
    @val @override int priority = 1;

    @Override
    public void register(Container app, UserInteractionHandler userInteractionHandler) {
        this.app = app;

        /* Resolve the SubtitleProviderStore from the IoC Container */
        SubtitleProviderStore subtitleProviderStore = app.makeSubtitleProviderStore();

        /* Create the SubtitleProvider */
        subtitleProvider = createProvider(userInteractionHandler);

        /* Add the SubtitleProvider to the store */
        subtitleProviderStore.addProvider(subtitleProvider);

        /* Listen for settings-change event */
        this.registerListener(subtitleProviderStore, userInteractionHandler);
    }

    private SubtitleProvider createProvider(UserInteractionHandler userInteractionHandler) {
        Settings settings = app.makeSettings();

        Credentials credentials = null;
        if (settings.loginOpenSubtitlesEnabled) {
            String username = StringUtils.trim(settings.loginOpenSubtitlesUsername);
            String password = StringUtils.trim(settings.loginOpenSubtitlesPassword);
            /* Protect against empty login */
            if (!username.isEmpty() && !password.isEmpty()) {
                credentials = new Credentials(username, password);
            }
        }
        return new JOpenSubAdapter(app.makeManager(), credentials, userInteractionHandler);
    }

    private void registerListener(SubtitleProviderStore subtitleProviderStore,
        UserInteractionHandler userInteractionHandler) {

        /* Listen for settings-change */
        app.makeEventEmitter().listen("providers.settings.change", _ -> {
            /* Change occurred, delete outdated provider from store */
            subtitleProviderStore.deleteProvider(subtitleProvider);

            /* Re-create subtitle provider */
            subtitleProvider = createProvider(userInteractionHandler);

            /* Re-add provider to store */
            subtitleProviderStore.addProvider(subtitleProvider);
        });
    }
}
