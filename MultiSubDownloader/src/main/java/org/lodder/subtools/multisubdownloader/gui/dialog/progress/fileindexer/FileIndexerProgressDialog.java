package org.lodder.subtools.multisubdownloader.gui.dialog.progress.fileindexer;

import javax.swing.JFrame;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.dialog.ProgressDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;

public class FileIndexerProgressDialog extends ProgressDialog implements FileIndexerProgressListener {

  public FileIndexerProgressDialog(JFrame frame, Cancelable sft) {
    super(frame, sft);
    StatusMessenger.instance.removeListener(this);
  }

  @Override
  public void progress(int progress) {
    updateProgress(progress);
  }

  @Override
  public void progress(String directory) {
    setMessage(directory);
  }
}
