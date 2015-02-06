package org.lodder.subtools.multisubdownloader.gui.dialog.progress.search;

import dnl.utils.text.table.TextTable;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.model.Release;

public class CLISearchProgress implements SearchProgressListener {

  TextTable table;
  SearchProgressTableModel tableModel;
  int progress;
  boolean isEnabled;
  boolean isVerbose;

  public CLISearchProgress() {
    tableModel = new SearchProgressTableModel();
    table = new TextTable(tableModel);
    isEnabled = true;
    isVerbose = false;
    progress = 0;
  }

  @Override
  public void progress(SubtitleProvider provider, int jobsLeft, Release release) {
    this.tableModel.update(provider.getName(), jobsLeft, (release == null ? "Done" : release.getFilename()));
    this.printProgress();
  }

  @Override
  public void progress(int progress) {
    this.progress = progress;
    this.printProgress();
  }

  private void printProgress() {
    if (!isEnabled)
      return;

    /* print table */
    if (isVerbose) {
      System.out.println("");
      table.printTable();
    }

    /* print progressbar */
    this.printProgBar(this.progress);
  }

  public void printProgBar(int percent) {
    // http://nakkaya.com/2009/11/08/command-line-progress-bar/
    StringBuilder bar = new StringBuilder("[");

    for (int i = 0; i < 50; i++) {
      if (i < (percent / 2)) {
        bar.append("=");
      } else if (i == (percent / 2)) {
        bar.append(">");
      } else {
        bar.append(" ");
      }
    }

    bar.append("]   " + percent + "%     ");
    System.out.print("\r" + bar.toString());
  }

  public void disable() {
    this.isEnabled = false;
    /* Print a line */
    System.out.println("");
  }

  public void setVerbose(boolean isVerbose) {
    this.isVerbose = isVerbose;
  }
}
