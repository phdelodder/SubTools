package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;

import org.lodder.subtools.sublibrary.logging.Listener;


public class LogTextArea extends JTextArea implements Listener {
  public LogTextArea() {}

  /**
     *
     */
  private static final long serialVersionUID = -9073347109399803897L;
  private boolean autoScroll = false;

  @Override
  public void log(String log) {
    this.append(log + "\n\r");
    doAutoScroll();
  }

  public void doAutoScroll() {
    if (isAutoScroll()) this.setCaretPosition(this.getDocument().getLength());
  }

  public void setAutoScroll(boolean autoScroll) {
    this.autoScroll = autoScroll;
  }

  public boolean isAutoScroll() {
    return autoScroll;
  }

}
