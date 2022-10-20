package org.lodder.subtools.multisubdownloader.listeners;

public interface IndexingProgressListener extends StatusListener {

    void progress(int progress);

    void progress(String directory);

    void completed();

    void reset();

}
