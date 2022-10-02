package org.lodder.subtools.multisubdownloader.util;

import org.apache.commons.cli.CommandLine;
import org.lodder.subtools.multisubdownloader.cli.CliOption;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CLIExtension {
    public static boolean hasCliOption(CommandLine line, CliOption cliOption) {
        return line.hasOption(cliOption.getValue());
    }

    public static String getCliOptionValue(CommandLine line, CliOption cliOption) {
        return line.getOptionValue(cliOption.getValue());
    }
}