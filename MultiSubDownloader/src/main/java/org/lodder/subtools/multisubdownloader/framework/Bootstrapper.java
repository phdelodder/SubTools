package org.lodder.subtools.multisubdownloader.framework;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import lombok.RequiredArgsConstructor;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProvider;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProviderComparator;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.util.lazy.LazySupplier;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class Bootstrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrapper.class);

    private final Container app;
    private final Settings settings;
    private final Preferences preferences;
    private final Manager manager;

    public void initialize(UserInteractionHandler userInteractionHandler) {
        /* Bind Settings to IoC Container */
        this.app.bind("Settings", new LazySupplier<>(() -> settings));

        /* Bind Preferences to IoC Container */
        this.app.bind("Preferences", new LazySupplier<>(() -> preferences));

        /* Bind Manager to IoC Container */
        this.app.bind("Manager", new LazySupplier<>(() -> manager));

        // Collect ServiceProviders
        List<ServiceProvider> providers = this.getProviders();

        // Sort according to priority
        providers.sort(new ServiceProviderComparator());

        // Register ServiceProviders
        this.registerProviders(providers, userInteractionHandler);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<ServiceProvider> getProviders() {

        Reflections reflections = new Reflections("org.lodder.subtools.multisubdownloader");
        Set<Class<? extends ServiceProvider>> providerClasses = reflections.getSubTypesOf(ServiceProvider.class);

        List<ServiceProvider> providers = new ArrayList<>();

        // Instantiate all service providers
        for (Class serviceProviderClass : providerClasses) {
            try {
                Constructor constructor = serviceProviderClass.getConstructor();
                ServiceProvider serviceProvider = (ServiceProvider) constructor.newInstance();
                providers.add(serviceProvider);
            } catch (Exception e) {
                LOGGER.error("ServiceProvider: '{}' failed to create instance.", serviceProviderClass.getName());
            }
        }
        return providers;
    }

    public void registerProviders(List<ServiceProvider> providers, UserInteractionHandler userInteractionHandler) {
        // Register service providers
        for (ServiceProvider provider : providers) {
            provider.register(this.app, userInteractionHandler);
            LOGGER.debug("ServiceProvider: '{}' registered.", provider.getClass().getName());
        }
    }
}
