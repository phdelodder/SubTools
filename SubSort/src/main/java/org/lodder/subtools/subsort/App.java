package org.lodder.subtools.subsort;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lodder.subtools.sublibrary.data.UserInteractionSettings;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandlerCLI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setEncoder(ple);
        consoleAppender.setContext(lc);
        consoleAppender.start();

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(consoleAppender);
        root.setLevel(Level.INFO);

        CommandLineParser parser = new GnuParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine line = parser.parse(getCLIOptions(), args);
            if (line.hasOption("help")) {
                formatter.printHelp("SubSort", getCLIOptions());
            } else {

                boolean optionsConfirmProviderMapping = line.hasOption("confirmProviderMapping");

                UserInteractionSettings userInteractionSettings =
                        new UserInteractionSettings(false, false, 0, false, null, optionsConfirmProviderMapping);
                SortSubtitle sortSubtitle = new SortSubtitle(new UserInteractionHandlerCLI(userInteractionSettings));
                if (line.hasOption("rebuildindex")) {
                    sortSubtitle.reBuildIndex(new File(line.getOptionValue("outputfolder")));
                } else if (line.hasOption("removefromarchive")) {
                    sortSubtitle.removeFromRepo(new File(line.getOptionValue("removefromarchive")), new File(
                            line.getOptionValue("outputfolder")));
                } else {
                    sortSubtitle.run(line.hasOption("remove"), new File(line.getOptionValue("inputfolder")),
                            new File(line.getOptionValue("outputfolder")), line.hasOption("cleanup"));
                }
            }
        } catch (ParseException e) {
            LOGGER.error("Unable to parse cli options", e);
        }
    }

    private static Options getCLIOptions() {
        Options options = new Options();
        options.addOption("help", false, "print this message");
        options.addOption("removefromarchive", true, "remove subtitle from archive");
        options.addOption("remove", false, "remove subtitles when copy to the index folder");
        options.addOption("rebuildindex", false, "rebuild index, ignoring the current index");
        options.addOption("inputfolder", true, "the folder to be sorted");
        options.addOption("outputfolder", true, "the folder containing the index");
        options.addOption("cleanup", false, "clean up none exising subtitles in index");
        options.addOption("confirmProviderMapping", false, "Always confirm the provider mapping");

        return options;
    }
}
