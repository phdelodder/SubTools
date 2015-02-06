package org.lodder.subtools.multisubdownloader.gui.dialog.progress.fileindexer;

public class CLIFileindexerProgressDialog implements FileIndexerProgressListener {

  String currentFile;
  int    progress;
  boolean isEnabled = true;
  boolean isVerbose;

  public CLIFileindexerProgressDialog() {
    isEnabled = true;
    isVerbose = false;
    progress = 0;
    currentFile = "";
  }

  @Override
  public void progress(int progress) {
    this.progress = progress;
    this.printProgress();
  }

  @Override
  public void progress(String directory) {
    this.currentFile = directory;
    this.printProgress();
  }

  private void printProgress() {
    if (!isEnabled) return;

    if (isVerbose) {
      /* newlines to counter the return carriage from printProgBar() */
      System.out.println("");
      System.out.println(this.currentFile);
      System.out.println("");
    }

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
