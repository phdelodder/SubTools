package org.lodder.subtools.multisubdownloader;

import java.awt.EventQueue;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lodder.subtools.multisubdownloader.framework.Bootstrapper;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;

public class App {

  private final static SettingsControl prefctrl = new SettingsControl();

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    // Default log level for the program
    Logger.instance.setLogLevel(Level.INFO);

    CommandLineParser parser = new GnuParser();
    HelpFormatter formatter = new HelpFormatter();

    org.apache.commons.cli.CommandLine line = null;
    try {
      line = parser.parse(getCLIOptions(), args);
    } catch (ParseException e) {
      Logger.instance.error(Logger.stack2String(e));
    }

    final Container app = new Container();
    Bootstrapper bootstrapper = new Bootstrapper(app, prefctrl.getSettings());

    if (line != null) {
      if (line.hasOption("help")) { 
        formatter.printHelp(ConfigProperties.getInstance().getProperty("name"), getCLIOptions()); 
      } else {
        if (line.hasOption("debug")) { 
          Logger.instance.setLogLevel(Level.DEBUG);
        }
        if (line.hasOption("trace")) { 
          Logger.instance.setLogLevel(Level.ALL);
        }
        if (line.hasOption("speedy")) { 
          Preferences preferences = Preferences.userRoot();
          preferences.putBoolean("speedy", true); 
        } else {
          Preferences preferences = Preferences.userRoot();
          preferences.putBoolean("speedy", false); 
        }

        if (line.hasOption("importpreferences")) { 
          File file = new File(line.getOptionValue("importpreferences")); 
          try {
            if (file.isFile()) {
              prefctrl.importPreferences(file);
            }
          } catch (Exception e) {
            Logger.instance.error("executeArgs: importPreferences" + Logger.stack2String(e)); 
          }
        } else if (line.hasOption("updatefromonlinemapping")) { 
          try {
            prefctrl.updateMappingFromOnline();
            prefctrl.store();
          } catch (Throwable e) {
            Logger.instance.error("executeArgs: updateFromOnlineMapping" + Logger.stack2String(e)); 
          }
        } else if (line.hasOption("nogui")) { 
          CommandLine cmd = new CommandLine(prefctrl, app);
          cmd.setReleaseFactory(new ReleaseFactory(prefctrl.getSettings()));

          if (line.hasOption("folder")) cmd.setFolder(new File(line.getOptionValue("folder")));  

          if (line.hasOption("language")) { 
            if (line.getOptionValue("language").equals("nl")  
                || line.getOptionValue("language").equals("en")) {  
              cmd.setLanguagecode(line.getOptionValue("language")); 
            } else {
              throw new Exception(Messages.getString("App.NoValidLanguage")); 
            }
          } else {
            Logger.instance.log(Messages.getString("App.NoLanguageUseDefault")); 
            cmd.setLanguagecode("nl"); 
          }
          cmd.setForce(line.hasOption("force")); 
          cmd.setDownloadall(line.hasOption("downloadall")); 
          cmd.setRecursive(line.hasOption("recursive")); 
          cmd.setSubtitleSelectionDialog(line.hasOption("selection")); 

          cmd.CheckUpdate();
          bootstrapper.initialize();
          cmd.Run();
        } else {
          bootstrapper.initialize();
          EventQueue.invokeLater(new Runnable() {
            public void run() {
              try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                MainWindow window = new MainWindow(prefctrl, app);
                window.setVisible(true);
              } catch (Exception e) {
                Logger.instance.error(Logger.stack2String(e));
              }
            }
          });
        }
      }
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
    options.addOption("language", true, 
        Messages.getString("App.OptionOptionLanguageMsg")); 
    options.addOption("debug", false, Messages.getString("App.OptionOptionDebugMsg"));  
    options.addOption("trace", false, Messages.getString("App.OptionOptionTraceMsg"));  
    options.addOption("importpreferences", true, Messages.getString("App.OptionOptionImportPreferencesMsg"));  
    options.addOption("force", false, Messages.getString("App.OptionOptionForceMsg"));  
    options.addOption("folder", true, Messages.getString("App.OptionOptionFolderMsg"));  
    options.addOption("downloadall", false, Messages.getString("App.OptionOptionDownloadAllMsg"));  
    options.addOption("updatefromonlinemapping", false, Messages.getString("App.OptionOptionUpdateMappingMsg"));  
    options.addOption("selection", false, 
        Messages.getString("App.OptionOptionSelectionMsg")); 
    options.addOption("speedy", false, Messages.getString("App.OptionOptionSpeedyMsg"));  

    return options;
  }
}
