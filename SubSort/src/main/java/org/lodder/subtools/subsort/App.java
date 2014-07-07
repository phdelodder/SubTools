package org.lodder.subtools.subsort;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;

public class App {

  public static void main(String[] args) {
    Logger.instance.setLogLevel(Level.INFO);

    CommandLineParser parser = new GnuParser();
    HelpFormatter formatter = new HelpFormatter();

    CommandLine line = null;

    try {
      line = parser.parse(getCLIOptions(), args);
    } catch (ParseException e) {
      Logger.instance.error(Logger.stack2String(e));
    }

    if (line != null) {
      if (line.hasOption("help")) {
        formatter.printHelp("SubSort", getCLIOptions());
      } else {
        SortSubtitle sortSubtitle = new SortSubtitle();
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
    }
  }

  private static Options getCLIOptions() {
    /**
     * CLI Options
     */
    Options options = new Options();
    options.addOption("help", false, "print this message");
    options.addOption("removefromarchive", true, "remove subtitle from archive");
    options.addOption("remove", false, "remove subtitles when copy to the index folder");
    options.addOption("rebuildindex", false, "rebuild index, ignoring the current index");
    options.addOption("inputfolder", true, "the folder to be sorted");
    options.addOption("outputfolder", true, "the folder containing the index");
    options.addOption("cleanup", false, "clean up none exising subtitles in index");

    return options;
  }
}
