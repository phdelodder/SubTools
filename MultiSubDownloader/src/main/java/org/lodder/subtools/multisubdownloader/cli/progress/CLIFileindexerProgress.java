package org.lodder.subtools.multisubdownloader.cli.progress;

import org.lodder.subtools.multisubdownloader.actions.ActionException;
import org.lodder.subtools.multisubdownloader.listeners.IndexingProgressListener;

public class CLIFileindexerProgress extends CLIProgress implements IndexingProgressListener {

    String currentFile;

    public CLIFileindexerProgress() {
        super();
        currentFile = "";
    }

    @Override
    public void progress(int progress) {
        setProgress(progress);
        this.printProgress();
    }

    @Override
    public void progress(String directory) {
        this.currentFile = directory;
        this.printProgress();
    }

    @Override
    public void completed() {
        if (!this.isEnabled()) {
            return;
        }
        this.disable();
    }

    @Override
    public void onError(ActionException exception) {
        if (!this.isEnabled()) {
            return;
        }
        System.out.println("Error: " + exception.getMessage());
    }

    @Override
    public void onStatus(String message) {
        if (!this.isEnabled()) {
            return;
        }
        System.out.println(message);
    }

    @Override
    protected void printProgress() {
        if (!isEnabled()) {
            return;
        }

        if (isVerbose()) {
            /* newlines to counter the return carriage from printProgBar() */
            System.out.println("");
            System.out.println(this.currentFile);
            System.out.println("");
        }

        this.printProgBar(this.getProgress());
    }
}
