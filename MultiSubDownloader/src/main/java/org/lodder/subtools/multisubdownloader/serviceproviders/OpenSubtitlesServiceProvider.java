package org.lodder.subtools.multisubdownloader.serviceproviders;

import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.framework.event.Emitter;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProvider;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.multisubdownloader.subtitleproviders.adapters.JOpenSubAdapter;
import org.lodder.subtools.sublibrary.Manager;

public class OpenSubtitlesServiceProvider implements ServiceProvider {

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
        SubtitleProviderStore subtitleProviderStore = (SubtitleProviderStore) app.make("SubtitleProviderStore");

        /* Create the SubtitleProvider */
        subtitleProvider = createProvider(userInteractionHandler);

        /* Add the SubtitleProvider to the store */
        subtitleProviderStore.addProvider(subtitleProvider);

        /* Listen for settings-change event */
        this.registerListener(subtitleProviderStore, userInteractionHandler);
    }

    private SubtitleProvider createProvider(UserInteractionHandler userInteractionHandler) {
        Settings settings = (Settings) this.app.make("Settings");
        Manager manager = (Manager) this.app.make("Manager");

        boolean loginEnabled = false;
        String username = "";
        String password = "";
        if (settings.isLoginOpenSubtitlesEnabled()) {
            loginEnabled = true;
            username = settings.getLoginOpenSubtitlesUsername();
            password = settings.getLoginOpenSubtitlesPassword();
        }

        /* Nullpointer safety */
        username = username == null ? "" : username.trim();
        password = password == null ? "" : password.trim();

        /* Protect against empty login */
        if (loginEnabled && (username.isEmpty() || password.isEmpty())) {
            loginEnabled = false;
        }

        return new JOpenSubAdapter(loginEnabled, username, password, manager, userInteractionHandler);
    }

    private void registerListener(SubtitleProviderStore subtitleProviderStore, UserInteractionHandler userInteractionHandler) {
        /* Resolve the EventEmitter from the IoC Container */
        Emitter emitter = (Emitter) app.make("EventEmitter");

        /* Listen for settings-change */
        emitter.listen("providers.settings.change", event -> {
            /* Change occured, delete outdated provider from store */
            subtitleProviderStore.deleteProvider(subtitleProvider);

            /* Re-create subtitleprovider */
            subtitleProvider = createProvider(userInteractionHandler);

            /* Re-add provider to store */
            subtitleProviderStore.addProvider(subtitleProvider);
        });
    }
}
