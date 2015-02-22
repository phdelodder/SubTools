package org.lodder.subtools.multisubdownloader.framework;

import java.util.HashMap;

public class Container {

  private HashMap<String, Resolver> bindings = new HashMap<>();

  public void bind(String name, Resolver resolver) {
    bindings.put(name, resolver);
  }

  public Object make(String name) {
    return bindings.get(name).resolve();
  }
}
