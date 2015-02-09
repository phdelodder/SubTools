package org.lodder.subtools.multisubdownloader.gui.dialog.progress.fileindexer;

import java.awt.Color;

import org.lodder.subtools.multisubdownloader.MainWindow;
import org.lodder.subtools.multisubdownloader.gui.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.dialog.ProgressDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;

public class IndexingProgressDialog extends ProgressDialog implements IndexingProgressListener {

  private final MainWindow window;
  private boolean completed;
  private Color bg;

  public IndexingProgressDialog(MainWindow window, Cancelable sft) {
    super(window, sft);
    this.window = window;
    this.completed = false;
    StatusMessenger.instance.removeListener(this);
    // workaround for not showing correct contents of dialog (works only in  java 1.8)
    bg = this.getBackground();
  }

  @Override
  public void progress(int progress) {
    this.setVisible();
    updateProgress(progress);
  }

  @Override
  public void progress(String directory) {
    this.setVisible();
    setMessage(directory);
  }

  @Override
  public void completed() {
    this.setVisible(false);
  }

  @Override
  public void onError(ActionException exception) {
    this.setVisible(false);
    this.window.showErrorMessage(exception.getMessage());
  }

  @Override
  public void onStatus(String message) {
    this.window.setStatusMessage(message);
  }

  private void setVisible() {
    // workaround for not showing correct contents of dialog (works only in  java 1.8)
    this.setBackground(Color.gray);
    this.setBackground(bg);
    if (this.completed) {
      return;
    }
    this.setVisible(true);
  }
}
