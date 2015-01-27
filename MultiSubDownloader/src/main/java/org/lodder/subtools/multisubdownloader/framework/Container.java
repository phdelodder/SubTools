package org.lodder.subtools.multisubdownloader.framework;

import java.util.HashMap;
import org.lodder.subtools.multisubdownloader.framework.service.ServiceResolver;

public class Container {

  private HashMap<String, ServiceResolver> bindings = new HashMap<>();

  public void bind(String name, ServiceResolver resolver) {
    bindings.put(name, resolver);
  }

  public Object make(String name) {
    return bindings.get(name).resolve();
  }
}
