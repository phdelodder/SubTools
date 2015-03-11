package org.lodder.subtools.multisubdownloader.lib;

import java.io.Console;

import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.model.Release;

import dnl.utils.text.table.TextTable;

public class SubtitleSelectionCLI extends SubtitleSelection {

  public SubtitleSelectionCLI(Settings settings) {
    super(settings);
  }

  @Override
  public int getUserInput(Release release) {
    System.out.println("\nSelect best subtitle for : " + release.getFilename());
    String[] columnNames = {"Score", "Subtitle name", "Source", "Uploader", "Hearing impaired"};

    Object[][] dataTable = new Object[release.getMatchingSubs().size()][columnNames.length];
    for (int i = 0; i < release.getMatchingSubs().size(); i++) {
      dataTable[i] =
          new Object[] {release.getMatchingSubs().get(i).getScore(),
              release.getMatchingSubs().get(i).getFilename(),
              release.getMatchingSubs().get(i).getSubtitleSource(),
              release.getMatchingSubs().get(i).getUploader(),
              release.getMatchingSubs().get(i).isHearingImpaired()};
    }

    TextTable tt = new TextTable(columnNames, dataTable);
    // this adds the numbering on the left
    tt.setAddRowNumbering(true);
    tt.printTable();

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
