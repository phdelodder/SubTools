package extensions.org.apache.commons.cli.CommandLine;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import org.apache.commons.cli.CommandLine;
import org.lodder.subtools.multisubdownloader.cli.CliOption;

@Extension
@UtilityClass
public class CommandLineExt {
    public static boolean hasCliOption(@This CommandLine line, CliOption cliOption) {
        return line.hasOption(cliOption.value);
    }

    public static String getCliOptionValue(@This CommandLine line, CliOption cliOption) {
        return line.getOptionValue(cliOption.value);
    }
}