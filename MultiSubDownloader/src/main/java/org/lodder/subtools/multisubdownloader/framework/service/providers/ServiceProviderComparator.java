package org.lodder.subtools.multisubdownloader.framework.service.providers;

import java.io.Serializable;
import java.util.Comparator;

public class ServiceProviderComparator implements Comparator<ServiceProvider>, Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 7236933845444427431L;

  @Override
  public int compare(ServiceProvider a, ServiceProvider b) {
    return ((Integer)a.getPriority()).compareTo(b.getPriority());
  }
}
