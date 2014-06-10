package org.lodder.subtools.subsort;
import java.io.File;

import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;

public class App {

  public static void main(String[] args) {
    Logger.instance.setLogLevel(Level.INFO);
    boolean remove = false;
    boolean cleanup = false;
    boolean rebuildindex = false;
    boolean removefromarchive = false;
    String inputfolder = "";
    String outputfolder = "";
    String previousarg = "";
    String toRemove = "";

    for (String arg : args) {
      if (arg.equals("--removefromarchive")) {
        removefromarchive = true;
      }
      if (previousarg.equals("--removefromarchive")) {
        toRemove = arg;
      }
      if (arg.equals("--remove")) {
        remove = true;
      }
      if (arg.equals("--rebuildindex")) {
        rebuildindex = true;
      }
      if (arg.equals("--inputfolder")) {

      }
      if (arg.equals("--outputfolder")) {

      }
      if (arg.equals("--cleanup")) {
        cleanup = true;
      }
      if (previousarg.equals("--inputfolder")) {
        inputfolder = arg;
      }
      if (previousarg.equals("--outputfolder")) {
        outputfolder = arg;
      }
      previousarg = arg;
    }

    SortSubtitle sortSubtitle = new SortSubtitle();
    if (rebuildindex) {
      sortSubtitle.reBuildIndex(new File(outputfolder));
    } else if (removefromarchive) {
      sortSubtitle.removeFromRepo(new File(toRemove), new File(outputfolder));
    } else {
      sortSubtitle.run(remove, new File(inputfolder), new File(outputfolder), cleanup);
    }

  }
}
