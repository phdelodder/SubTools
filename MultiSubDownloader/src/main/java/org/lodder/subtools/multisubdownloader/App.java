package org.lodder.subtools.multisubdownloader;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lodder.subtools.multisubdownloader.cli.CliOption;
import org.lodder.subtools.multisubdownloader.exceptions.CliException;
import org.lodder.subtools.multisubdownloader.framework.Bootstrapper;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.gui.Splash;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.util.CLIExtension;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.DiskCache;
import org.lodder.subtools.sublibrary.cache.InMemoryCache;
import org.lodder.subtools.sublibrary.cache.SerializableDiskCache;
import org.lodder.subtools.sublibrary.util.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.EventQueue;

import ch.qos.logback.classic.Level;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ CLIExtension.class })
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
            return;
        }

        if (!line.hasCliOption(CliOption.NO_GUI)) {
            splash = new Splash();
            splash.showSplash();
        }

        Preferences preferences = Preferences.userRoot();
        preferences.putBoolean("speedy", line.hasCliOption(CliOption.SPEEDY));
        preferences.putBoolean("confirmProviderMapping", line.hasCliOption(CliOption.CONFIRM_PROVIDER_MAPPING));

        final Container app = new Container();
        final Manager manager = createManager(!line.hasCliOption(CliOption.NO_GUI));
        prefctrl = new SettingsControl(manager);
        Bootstrapper bootstrapper = new Bootstrapper(app, prefctrl.getSettings(), preferences, manager);

        if (line.hasCliOption(CliOption.HELP)) {
            formatter.printHelp(ConfigProperties.getInstance().getProperty("name"), getCLIOptions());
            return;
        }

        if (line.hasCliOption(CliOption.TRACE)) {
            setLogLevel(Level.ALL);
        } else if (line.hasCliOption(CliOption.DEBUG)) {
            setLogLevel(Level.DEBUG);
        }

        if (line.hasCliOption(CliOption.NO_GUI)) {
            bootstrapper.initialize(new UserInteractionHandlerCLI(prefctrl.getSettings()));
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

            bootstrapper.initialize(new UserInteractionHandlerGUI(prefctrl.getSettings(), null));
            EventQueue.invokeLater(() -> {
                try {
                    JFrame window = new GUI(prefctrl, app);
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
        if (!line.hasCliOption(CliOption.IMPORT_PREFERENCES)) {
            return;
        }
        File file = new File(line.getCliOptionValue(CliOption.IMPORT_PREFERENCES));
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
        Arrays.stream(CliOption.values()).forEach(
                cliOption -> options.addOption(cliOption.getValue(), cliOption.getLongValue(), cliOption.isHasArg(), cliOption.getDescription()));
        return options;
    }

    private static Manager createManager(boolean useGui) {
        if (splash != null) {
            splash.setProgressMsg("Creating Manager");
        }
        DiskCache<String, Serializable> diskCache =
                SerializableDiskCache.cacheBuilder().keyType(String.class).valueType(Serializable.class)
                        .timeToLive(TimeUnit.SECONDS.convert(500, TimeUnit.DAYS))
                        .timerInterval(TimeUnit.SECONDS.convert(1, TimeUnit.DAYS))
                        .maxItems(5000)
                        .build();

        InMemoryCache<String, Serializable> inMemoryCache =
                InMemoryCache.builder().keyType(String.class).valueType(Serializable.class)
                        .timeToLive(TimeUnit.SECONDS.convert(10, TimeUnit.MINUTES))
                        .timerInterval(100L)
                        .maxItems(500)
                        .build();

        return new Manager(new HttpClient(), inMemoryCache, diskCache);
    }
}
