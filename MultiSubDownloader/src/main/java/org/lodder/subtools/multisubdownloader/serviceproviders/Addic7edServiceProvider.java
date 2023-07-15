package org.lodder.subtools.multisubdownloader.serviceproviders;

import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.event.Emitter;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProvider;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.multisubdownloader.subtitleproviders.adapters.JAddic7edAdapter;
import org.lodder.subtools.multisubdownloader.subtitleproviders.adapters.JAddic7edViaProxyAdapter;
import org.lodder.subtools.sublibrary.Manager;

public class Addic7edServiceProvider implements ServiceProvider {

    protected Container app;
    protected SubtitleProvider subtitleProvider;

    @Override
    public int getPriority() {
        /* We define a priority lower than SubtitleServiceProvider */
        return 1;
    }

    @Override
    public void register(Container app, UserInteractionHandler userInteractionHandler) {
        this.app = app;

        /* Resolve the SubtitleProviderStore from the IoC Container */
        final SubtitleProviderStore subtitleProviderStore = (SubtitleProviderStore) app.make("SubtitleProviderStore");

        /* Create the SubtitleProvider */
        subtitleProvider = createProvider(userInteractionHandler);

        /* Add the SubtitleProvider to the store */
        subtitleProviderStore.addProvider(subtitleProvider);

        /* Listen for settings-change event */
        this.registerListener(subtitleProviderStore, userInteractionHandler);
    }

    private SubtitleProvider createProvider(UserInteractionHandler userInteractionHandler) {
        Settings settings = (Settings) this.app.make("Settings");
        Preferences preferences = (Preferences) this.app.make("Preferences");
        Manager manager = (Manager) this.app.make("Manager");

        boolean loginEnabled = false;
        String username = "";
        String password = "";
        if (settings.isLoginAddic7edEnabled()) {
            username = StringUtils.trim(settings.getLoginAddic7edUsername());
            password = StringUtils.trim(settings.getLoginAddic7edPassword());
            /* Protect against empty login */
            loginEnabled = !username.isEmpty() && !password.isEmpty();
        }

        if (settings.isSerieSourceAddic7edProxy()) {
            return new JAddic7edViaProxyAdapter(manager, userInteractionHandler);
        } else {
            return new JAddic7edAdapter(loginEnabled, username, password, preferences.getBoolean("speedy", false), manager, userInteractionHandler);
        }
    }

    // TODO is this still needed?
    private void registerListener(SubtitleProviderStore subtitleProviderStore, UserInteractionHandler userInteractionHandler) {
        /* Resolve the EventEmitter from the IoC Container */
        Emitter emitter = (Emitter) app.make("EventEmitter");

        /* Listen for settings-change */
        emitter.listen("providers.settings.change", event -> {
            /* Change occurred, delete outdated provider from store */
            subtitleProviderStore.deleteProvider(subtitleProvider);

            /* Re-create subtitle provider */
            subtitleProvider = createProvider(userInteractionHandler);

            /* Re-add provider to store */
            subtitleProviderStore.addProvider(subtitleProvider);
        });
    }
}
