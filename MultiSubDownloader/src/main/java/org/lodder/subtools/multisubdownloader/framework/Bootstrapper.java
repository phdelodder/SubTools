package org.lodder.subtools.multisubdownloader.framework;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProvider;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProviderComparator;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrapper.class);

    private final Container app;
    private final Settings settings;
    private final Preferences preferences;
    private final Manager manager;

    public Bootstrapper(Container app, Settings settings, Preferences preferences, Manager manager) {
        this.app = app;
        this.settings = settings;
        this.preferences = preferences;
        this.manager = manager;
    }

    public void initialize() {
        /* Bind Settings to IoC Container */
        this.app.bind("Settings", () -> settings);

        /* Bind Preferences to IoC Container */
        this.app.bind("Preferences", () -> preferences);

        /* Bind Manager to IoC Container */
        this.app.bind("Manager", () -> manager);

        // Collect ServiceProviders
        List<ServiceProvider> providers = this.getProviders();

        // Sort according to priority
        Collections.sort(providers, new ServiceProviderComparator());

        // Register ServiceProviders
        this.registerProviders(providers);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<ServiceProvider> getProviders() {

        Reflections reflections = new Reflections("org.lodder.subtools.multisubdownloader");
        Set<Class<? extends ServiceProvider>> providerClasses =
                reflections.getSubTypesOf(ServiceProvider.class);

        List<ServiceProvider> providers = new ArrayList<>();

        // Intantieer alle serviceproviders
        for (Class serviceProviderClass : providerClasses) {
            ServiceProvider serviceProvider = null;

            try {
                Constructor constructor = serviceProviderClass.getConstructor();
                serviceProvider = (ServiceProvider) constructor.newInstance();
            } catch (Exception e) {
                LOGGER.error("ServiceProvider: '{}' failed to create instance.", serviceProviderClass
                        .getClass().getName());
            }

            if (serviceProvider == null) {
                continue;
            }

            providers.add(serviceProvider);
        }
        return providers;
    }

    public void registerProviders(List<ServiceProvider> providers) {
        // Register serviceproviders
        for (ServiceProvider provider : providers) {
            provider.register(this.app);
            LOGGER.debug("ServiceProvider: '{}' registered.", provider.getClass().getName());
        }
    }
}
