package org.lodder.subtools.multisubdownloader.listeners;

public interface IndexingProgressListener extends StatusListener {

    public void progress(int progress);

    public void progress(String directory);

    public void completed();

}
