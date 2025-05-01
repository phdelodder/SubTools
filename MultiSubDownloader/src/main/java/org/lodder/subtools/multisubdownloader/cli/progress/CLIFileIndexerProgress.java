package org.lodder.subtools.multisubdownloader.cli.progress;

import org.lodder.subtools.multisubdownloader.actions.ActionException;
import org.lodder.subtools.multisubdownloader.listeners.IndexingProgressListener;

public final class CLIFileIndexerProgress extends CLIProgress implements IndexingProgressListener {

    private String currentFile;

    public CLIFileIndexerProgress() {
        super();
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

    @Override
    public void completed() {
        if (!enabled) {
            return;
        }
        this.disable();
    }

    @Override
    public void reset() {
        this.enabled = true;
    }

    @Override
    public void onError(ActionException exception) {
        if (!enabled) {
            return;
        }
        System.out.println("Error: " + exception.getMessage());
    }

    @Override
    public void onStatus(String message) {
        if (!enabled) {
            return;
        }
        System.out.println(message);
    }

    @Override
    protected void printProgress() {
        if (!enabled) {
            return;
        }

        if (verbose) {
            /* newlines to counter the return carriage from printProgBar() */
            System.out.println();
            System.out.println(this.currentFile);
            System.out.println();
        }

        this.printProgBar(this.progress);
    }

    // TODO: remove this when https://github.com/manifold-systems/manifold/issues/642 is fixed
    @Override
    public CLIFileIndexerProgress verbose(boolean verbose) {
        super.verbose(verbose);
        return this;
    }
}
