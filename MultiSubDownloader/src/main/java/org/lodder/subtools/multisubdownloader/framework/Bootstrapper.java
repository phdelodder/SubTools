package org.lodder.subtools.multisubdownloader.framework;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProvider;
import org.lodder.subtools.multisubdownloader.framework.service.providers.ServiceProviderComparator;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.reflections.Reflections;

public class Bootstrapper {

  private Container app;

  public Bootstrapper(Container app){
    this.app = app;
  }

  public void initialize() {
    // TODO: laad settings

    // Verzamel alle serviceproviders
    List<ServiceProvider> providers = this.getProviders();

    // Sorteer volgens prioriteit
    Collections.sort(providers, new ServiceProviderComparator());

    // Registreer de serviceproviders
    this.registerProviders(providers);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public List<ServiceProvider> getProviders() {

    Reflections reflections = new Reflections("org.lodder.subtools.multisubdownloader");
    Set<Class<? extends ServiceProvider>> providerClasses = reflections.getSubTypesOf(ServiceProvider.class);

    List<ServiceProvider> providers = new ArrayList<>();

    // Intantieer alle serviceproviders
    for (Class serviceProviderClass : providerClasses) {
      ServiceProvider serviceProvider = null;

      try {
        Constructor constructor = serviceProviderClass.getConstructor();
        serviceProvider = (ServiceProvider) constructor.newInstance();
      } catch (Exception e) {
        Logger.instance.error("ServiceProvider: '" + serviceProviderClass.getClass().getName() + "' failed to create instance.");
      }

      if (serviceProvider == null)
        continue;

      providers.add(serviceProvider);
    }
    return providers;
  }

  public void registerProviders(List<ServiceProvider> providers) {
    // Register serviceproviders
    for (ServiceProvider provider : providers) {
      provider.register(this.app);

      Logger.instance.log("ServiceProvider: '"+ provider.getClass().getName() + "' registered.");
    }
  }
}
