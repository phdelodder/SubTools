package org.lodder.subtools.multisubdownloader.lib;

import java.io.Console;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;

public class SubtitleSelectionCLI extends SubtitleSelection {

  public SubtitleSelectionCLI(Settings settings) {
    super(settings);
  }

  @Override
  public int getUserInput(Release release) {
    System.out.println("Select best subtitle for : " + release.getFilename());
    for (int i = 0; i < release.getMatchingSubs().size(); i++) {
      System.out.println("(" + i + ")" + Actions.buildDisplayLine(release.getMatchingSubs().get(i)));
    }
    System.out.println("(-1) To skip download and/or move!");
    Console c = System.console();
    String selectedSubtitle = c.readLine("Enter number of selected subtitle: ");
    try {
      Integer.parseInt(selectedSubtitle);
    } catch (Exception e) {
      return -1;
    }
    return Integer.parseInt(selectedSubtitle);
  }
}
