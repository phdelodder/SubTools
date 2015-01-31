package org.lodder.subtools.multisubdownloader.framework.service.providers;

import java.util.Comparator;

public class ServiceProviderComparator implements Comparator<ServiceProvider> {
  @Override
  public int compare(ServiceProvider a, ServiceProvider b) {
    return ((Integer)a.getPriority()).compareTo(b.getPriority());
  }
}
