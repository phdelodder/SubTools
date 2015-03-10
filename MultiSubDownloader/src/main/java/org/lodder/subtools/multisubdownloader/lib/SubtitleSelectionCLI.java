package org.lodder.subtools.multisubdownloader.lib;

import java.io.Console;
import java.util.List;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;

public class SubtitleSelectionCLI extends SubtitleSelection {

  public SubtitleSelectionCLI(Settings settings) {
    super(settings);
  }

  @Override
  public int getUserInput(Release release) {
    System.out.println("\nSelect best subtitle for : " + release.getFilename());
    List<String> lines = this.buildDisplayLines(release);
    for (int i = 0; i < lines.size(); i++) {
      System.out.println("(" + i + ") " + lines.get(i));
    }
    System.out.println("(-1) To skip download and/or move!");
    Console c = System.console();
    String selectedSubtitle = c.readLine("Enter number of selected subtitle: ");
    System.out.println("");
    try {
      Integer.parseInt(selectedSubtitle);
    } catch (Exception e) {
      return -1;
    }
    return Integer.parseInt(selectedSubtitle);
  }
}
