package org.lodder.subtools.multisubdownloader.framework.service.providers;

import org.lodder.subtools.multisubdownloader.framework.Container;

public interface ServiceProvider {
  public int getPriority();
  public void register(Container app);
}
