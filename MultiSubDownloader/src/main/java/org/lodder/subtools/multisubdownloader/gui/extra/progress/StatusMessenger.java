package org.lodder.subtools.multisubdownloader.gui.extra.progress;

import java.util.LinkedList;
import java.util.List;

public enum StatusMessenger implements Messenger {
  instance;

  private final List<Messenger> statusmessagers = new LinkedList<Messenger>();

  public void addListener(Messenger sm) {
    synchronized (statusmessagers) {
      statusmessagers.add(sm);
    }
  }

  public void removeListener(Messenger sm) {
    synchronized (statusmessagers) {
      statusmessagers.remove(sm);
    }
  }

  public void message(String message) {
    synchronized (statusmessagers) {
      for (Messenger sm : statusmessagers)
        sm.message(message);
    }
  }
}
