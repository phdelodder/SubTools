package org.lodder.subtools.multisubdownloader.gui.dialog.progress.fileindexer;

public interface FileIndexerProgressListener {

  public void progress(int progress);

  public void progress(String directory);

}
