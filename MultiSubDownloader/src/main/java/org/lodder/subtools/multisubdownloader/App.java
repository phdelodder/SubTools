package org.lodder.subtools.multisubdownloader;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lodder.subtools.multisubdownloader.exceptions.CliException;
import org.lodder.subtools.multisubdownloader.framework.Bootstrapper;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.gui.Splash;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.DiskCache;
import org.lodder.subtools.sublibrary.cache.InMemoryCache;
import org.lodder.subtools.sublibrary.cache.SerializableDiskCache;
import org.lodder.subtools.sublibrary.util.http.CookieManager;
import org.lodder.subtools.sublibrary.util.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.EventQueue;

import ch.qos.logback.classic.Level;

public class App {

    private static SettingsControl prefctrl;
    private static Splash splash;
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws ReflectiveOperationException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        CommandLineParser parser = new GnuParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine line = null;
        try {
            line = parser.parse(getCLIOptions(), args);
        } catch (ParseException e) {
            LOGGER.error("Unable to parse cli options", e);
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
        prefctrl = new SettingsControl(manager);
        Bootstrapper bootstrapper = new Bootstrapper(app, prefctrl.getSettings(), preferences, manager);

        if (line.hasOption("help")) {
            formatter.printHelp(ConfigProperties.getInstance().getProperty("name"), getCLIOptions());
            return;
        }

        if (line.hasOption("trace")) {
            setLogLevel(Level.ALL);
        } else if (line.hasOption("debug")) {
            setLogLevel(Level.DEBUG);
        }

        if (line.hasOption("nogui")) {
            bootstrapper.initialize();
            CLI cmd = new CLI(prefctrl, app);

            /* Defined here so there is output on console */
            importPreferences(line);

            try {
                cmd.setUp(line);
            } catch (CliException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }

            cmd.run();
        } else {
            /* Defined here so there is output in the splash */
            importPreferences(line);

            bootstrapper.initialize();
            EventQueue.invokeLater(() -> {
                try {
                    GUI window = new GUI(prefctrl, app);
                    window.setVisible(true);
                    splash.setVisible(false);
                    splash.dispose();
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            });
        }
    }

    private static void setLogLevel(Level level) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(level);
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
            LOGGER.error("executeArgs: importPreferences", e);
        }
    }

    public static Options getCLIOptions() {
        Options options = new Options();
        options.addOption("help", false, Messages.getString("App.OptionHelpMsg"));
        options.addOption("nogui", false, Messages.getString("App.OptionNoGuiMsg"));
        options.addOption("R", "recursive", false, Messages.getString("App.OptionOptionRecursiveMsg"));
        options.addOption("language", true, Messages.getString("App.OptionOptionLanguageMsg"));
        options.addOption("debug", false, Messages.getString("App.OptionOptionDebugMsg"));
        options.addOption("trace", false, Messages.getString("App.OptionOptionTraceMsg"));
        options.addOption("importpreferences", true, Messages.getString("App.OptionOptionImportPreferencesMsg"));
        options.addOption("force", false, Messages.getString("App.OptionOptionForceMsg"));
        options.addOption("folder", true, Messages.getString("App.OptionOptionFolderMsg"));
        options.addOption("downloadall", false, Messages.getString("App.OptionOptionDownloadAllMsg"));
        options.addOption("selection", false, Messages.getString("App.OptionOptionSelectionMsg"));
        options.addOption("speedy", false, Messages.getString("App.OptionOptionSpeedyMsg"));
        options.addOption("verboseprogress", false, Messages.getString("App.OptionVerboseProgressCLI"));
        options.addOption("dryrun", false, Messages.getString("App.OptionDryRun"));

        return options;
    }

    private static Manager createManager() {
        if (splash != null) {
            splash.setProgressMsg("Creating Manager");
        }
        DiskCache<String, String> diskCache =
                SerializableDiskCache.cacheBuilder().keyType(String.class).valueType(String.class)
                        .timeToLive(TimeUnit.SECONDS.convert(500, TimeUnit.DAYS))
                        .timerInterval(TimeUnit.SECONDS.convert(1, TimeUnit.DAYS))
                        .maxItems(1500)
                        .build();

        InMemoryCache<String, String> inMemoryCache =
                InMemoryCache.builder().keyType(String.class).valueType(String.class)
                        .timeToLive(TimeUnit.SECONDS.convert(10, TimeUnit.MINUTES))
                        .timerInterval(100L)
                        .maxItems(500)
                        .build();

        HttpClient httpClient = new HttpClient();
        httpClient.setCookieManager(new CookieManager());

        return new Manager(httpClient, inMemoryCache, diskCache);
    }
}
