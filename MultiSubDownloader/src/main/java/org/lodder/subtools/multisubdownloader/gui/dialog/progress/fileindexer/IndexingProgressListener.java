package org.lodder.subtools.multisubdownloader.gui.dialog.progress.fileindexer;

import org.lodder.subtools.multisubdownloader.gui.dialog.progress.StatusListener;

public interface IndexingProgressListener extends StatusListener {

  public void progress(int progress);

  public void progress(String directory);

  public void completed();

}
