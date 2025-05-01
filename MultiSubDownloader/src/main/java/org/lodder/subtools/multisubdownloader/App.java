package org.lodder.subtools.multisubdownloader;

import static manifold.science.util.UnitConstants.*;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.prefs.Preferences;

import ch.qos.logback.classic.Level;
import lombok.experimental.ExtensionMethod;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lodder.subtools.multisubdownloader.cli.CliOption;
import org.lodder.subtools.multisubdownloader.exceptions.CliException;
import org.lodder.subtools.multisubdownloader.framework.Bootstrapper;
import org.lodder.subtools.multisubdownloader.framework.Container;
import org.lodder.subtools.multisubdownloader.gui.Splash;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.ConfigProperties.Property;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.cache.DiskCache;
import org.lodder.subtools.sublibrary.cache.InMemoryCache;
import org.lodder.subtools.sublibrary.util.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtensionMethod({Files.class})
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static SettingsControl prefCtrl;
    private static Splash splash;

    public static void main(String[] args) throws ReflectiveOperationException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine line;
        try {
            line = parser.parse(getCLIOptions(), args);
        } catch (ParseException e) {
            LOGGER.error("Unable to parse cli options", e);
            return;
        }

        if (!line.hasCliOption(CliOption.NO_GUI)) {
            splash = new Splash().showSplash();
        }

        Preferences preferences = Preferences.userRoot();
        preferences.putBoolean(CliOption.SPEEDY.value, line.hasCliOption(CliOption.SPEEDY));
        preferences.putBoolean(CliOption.CONFIRM_PROVIDER_MAPPING.value,
            line.hasCliOption(CliOption.CONFIRM_PROVIDER_MAPPING));

        final Container app = new Container();
        final Manager manager = createManager(!line.hasCliOption(CliOption.NO_GUI));
        prefCtrl = new SettingsControl(manager);
        Messages.language = prefCtrl.settings.language;
        Bootstrapper bootstrapper = new Bootstrapper(app, prefCtrl.settings, preferences, manager);

        if (line.hasCliOption(CliOption.TRACE)) {
            setLogLevel(Level.ALL);
        } else if (line.hasCliOption(CliOption.DEBUG)) {
            setLogLevel(Level.DEBUG);
        }

        if (line.hasCliOption(CliOption.NO_GUI)) {
            bootstrapper.initialize(new UserInteractionHandlerCLI(prefCtrl.settings));
            CLI cmd = new CLI(prefCtrl, app);

            /* Defined here so there is output on console */
            importPreferences(line);

            try {
                cmd.setUp(line);
                if (line.hasCliOption(CliOption.HELP)) {
                    formatter.printHelp(ConfigProperties.getProperty(Property.NAME), getCLIOptions());
                    return;
                }
            } catch (CliException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }
            cmd.run();
        } else {
            /* Defined here so there is output in the splash */
            importPreferences(line);

            bootstrapper.initialize(new UserInteractionHandlerGUI(prefCtrl.settings, null));
            EventQueue.invokeLater(() -> {
                try {
                    JFrame window = new GUI(prefCtrl, app);
                    window.setVisible(true);
                    splash.setVisible(false);
                    splash.dispose();
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            });
        }
        new Thread(() -> {
            List<String> providerNames =
                app.makeSubtitleProviderStore().getAllProviders().stream().map(SubtitleProvider::getProviderName)
                    .map(providerName -> providerName.contains("-") ? providerName.split("-")[0] : providerName)
                    .map(providerName -> providerName + "-").toList();
            manager.getCache(CacheType.DISK, key -> providerNames.stream().noneMatch(key::startsWith))
                .clearExpiredCache();
        }).start();

    }

    private static void setLogLevel(Level level) {
        ch.qos.logback.classic.Logger root =
            (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(level);
    }

    private static void importPreferences(CommandLine line) {
        if (!line.hasCliOption(CliOption.IMPORT_PREFERENCES)) {
            return;
        }
        Path file = Path.of(line.getCliOptionValue(CliOption.IMPORT_PREFERENCES));
        try {
            if (file.isRegularFile()) {
                prefCtrl.importPreferences(file);
            }
        } catch (Exception e) {
            LOGGER.error("executeArgs: importPreferences", e);
        }
    }

    public static Options getCLIOptions() {
        Options options = new Options();
        CliOption.values().forEach(cliOption -> options.addOption(cliOption.value, cliOption.longValue,
            cliOption.hasArg, cliOption.description));
        return options;
    }

    private static Manager createManager(boolean useGui) {
        if (splash != null) {
            splash.progressMsg = Messages.getText("App.Starting");
        }
        DiskCache<String, Serializable> diskCache =
            new DiskCache<>(
                String.class,
                Serializable.class,
                500 day,
                2500);

        InMemoryCache<String, String> inMemoryCache =
            new InMemoryCache<>(
                String.class,
                String.class,
                10 min,
                100 ms,
                500);

        return new Manager(new HttpClient(), inMemoryCache, diskCache);
    }
}
