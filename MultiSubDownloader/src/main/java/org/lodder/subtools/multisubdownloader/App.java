package org.lodder.subtools.multisubdownloader;

import java.awt.EventQueue;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lodder.subtools.multisubdownloader.framework.Bootstrapper;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.gui.Splash;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.DiskCache;
import org.lodder.subtools.sublibrary.cache.InMemoryCache;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.util.http.CookieManager;
import org.lodder.subtools.sublibrary.util.http.HttpClient;

public class App {

  private final static SettingsControl prefctrl = new SettingsControl();
  private static Splash splash;

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    // Default log level for the program
    Logger.instance.setLogLevel(Level.INFO);

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    CommandLineParser parser = new GnuParser();
    HelpFormatter formatter = new HelpFormatter();

    CommandLine line = null;
    try {
      line = parser.parse(getCLIOptions(), args);
    } catch (ParseException e) {
      Logger.instance.error(Logger.stack2String(e));
    }

    if (line == null) {
      return;
    }

    if (!line.hasOption("nogui")) {
      splash = new Splash();
      splash.showSplash();
    }

    Preferences preferences = Preferences.userRoot();
    preferences.putBoolean("speedy", line.hasOption("speedy"));

    final Container app = new Container();
    final Manager manager = createManager();
    Bootstrapper bootstrapper = new Bootstrapper(app, prefctrl.getSettings(), preferences, manager);

    if (line.hasOption("help")) {
      formatter.printHelp(ConfigProperties.getInstance().getProperty("name"), getCLIOptions());
      return;
    }

    if (line.hasOption("trace")) {
      Logger.instance.setLogLevel(Level.ALL);
    } else if (line.hasOption("debug")) {
      Logger.instance.setLogLevel(Level.DEBUG);
    }



    if (line.hasOption("nogui")) {
      bootstrapper.initialize();
      CLI cmd = new CLI(prefctrl.getSettings(), app);

      /* Defined here so there is output on console */
      checkUpdate(manager);
      importPreferences(line);
      updateMapping(line);

      try {
        cmd.setUp(line);
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
        return;
      }

      cmd.run();
    } else {
      /* Defined here so there is output in the splash */
      checkUpdate(manager);
      importPreferences(line);
      updateMapping(line);

      bootstrapper.initialize();
      EventQueue.invokeLater(new Runnable() {
        public void run() {
          try {
            GUI window = new GUI(prefctrl, app);
            window.setVisible(true);
            splash.setVisible(false);
            splash.dispose();
          } catch (Exception e) {
            Logger.instance.error(Logger.stack2String(e));
          }
        }
      });
    }
  }

  private static void updateMapping(CommandLine line) {
    Settings settings = prefctrl.getSettings();
    if (!line.hasOption("updatefromonlinemapping") && !settings.isAutoUpdateMapping()) {
      return;
    }
    try {
      prefctrl.updateMappingFromOnline();
      prefctrl.store();
    } catch (Throwable e) {
      Logger.instance.error("executeArgs: updateFromOnlineMapping" + Logger.stack2String(e));
    }
  }

  private static void importPreferences(CommandLine line) {
    if (!line.hasOption("importpreferences")) {
      return;
    }
    File file = new File(line.getOptionValue("importpreferences"));
    try {
      if (file.isFile()) {
        prefctrl.importPreferences(file);
      }
    } catch (Exception e) {
      Logger.instance.error("executeArgs: importPreferences" + Logger.stack2String(e));
    }
  }

  public static Options getCLIOptions() {
    /**
     * CLI Options
     */
    Options options = new Options();
    options.addOption("help", false, Messages.getString("App.OptionHelpMsg"));
    options.addOption("nogui", false, Messages.getString("App.OptionNoGuiMsg"));
    options.addOption("R", "recursive", false, Messages.getString("App.OptionOptionRecursiveMsg"));
    options.addOption("language", true, Messages.getString("App.OptionOptionLanguageMsg"));
    options.addOption("debug", false, Messages.getString("App.OptionOptionDebugMsg"));
    options.addOption("trace", false, Messages.getString("App.OptionOptionTraceMsg"));
    options.addOption("importpreferences", true,
        Messages.getString("App.OptionOptionImportPreferencesMsg"));
    options.addOption("force", false, Messages.getString("App.OptionOptionForceMsg"));
    options.addOption("folder", true, Messages.getString("App.OptionOptionFolderMsg"));
    options.addOption("downloadall", false, Messages.getString("App.OptionOptionDownloadAllMsg"));
    options.addOption("updatefromonlinemapping", false,
        Messages.getString("App.OptionOptionUpdateMappingMsg"));
    options.addOption("selection", false, Messages.getString("App.OptionOptionSelectionMsg"));
    options.addOption("speedy", false, Messages.getString("App.OptionOptionSpeedyMsg"));
    options.addOption("verboseprogress", false, Messages.getString("App.OptionVerboseProgressCLI"));

    return options;
  }

  public static void checkUpdate(Manager manager) {
    UpdateAvailableDropbox u = new UpdateAvailableDropbox(manager);
    if (u.checkProgram(prefctrl.getSettings().getUpdateCheckPeriod())) {
      Logger.instance.log(Messages.getString("UpdateAppAvailable") + ": " + u.getUpdateUrl());
    }
  }

  private static Manager createManager() {
    Logger.instance.log("Creating manager");
    Manager manager = new Manager();
    DiskCache<String, String> diskCache =
        new DiskCache<String, String>(TimeUnit.SECONDS.convert(5, TimeUnit.DAYS), 100, 500, "user", "pass");
    manager.setDiskCache(diskCache);
    InMemoryCache<String, String> inMemoryCache =
        new InMemoryCache<String, String>(TimeUnit.SECONDS.convert(10, TimeUnit.MINUTES), 10, 500);
    manager.setInMemoryCache(inMemoryCache);
    HttpClient httpClient = new HttpClient();
    httpClient.setCookieManager(new CookieManager());
    manager.setHttpClient(httpClient);
    return manager;
  }
}
