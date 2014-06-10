package org.lodder.subtools.multisubdownloader;
import java.awt.EventQueue;

import javax.swing.UIManager;

import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;

public class App {

  private final static SettingsControl prefctrl = new SettingsControl();

  /**
   * @param args
   */
  public static void main(String[] args) {
    // Default log level for the program
    Logger.instance.setLogLevel(Level.INFO);

    try {
      if (prefctrl.getSettings().isAutoUpdateMapping()) {
        Logger.instance.log("Auto updating mapping ....");
        prefctrl.updateMappingFromOnline();
      }
    } catch (Throwable e) {
      Logger.instance.error(Logger.stack2String(e));
    }

    CommandLine cmd = new CommandLine(prefctrl);
    try {
      cmd.Parse(args);
    } catch (Exception e) {
      Logger.instance.error(e.getMessage());
    }
    if (cmd.isGui()) {
      EventQueue.invokeLater(new Runnable() {
        public void run() {
          try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            MainWindow window = new MainWindow(prefctrl);
            window.setVisible(true);
          } catch (Exception e) {
            Logger.instance.error(Logger.stack2String(e));
          }
        }
      });
    } else {
      cmd.CheckUpdate();
      cmd.executeArgs();
    }
  }
}
