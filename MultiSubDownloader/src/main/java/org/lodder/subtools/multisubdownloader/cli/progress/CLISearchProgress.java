package org.lodder.subtools.multisubdownloader.cli.progress;

import org.lodder.subtools.multisubdownloader.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.dialog.progress.search.SearchProgressTableModel;
import org.lodder.subtools.multisubdownloader.listeners.SearchProgressListener;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.model.Release;

import dnl.utils.text.table.TextTable;

public class CLISearchProgress extends CLIProgress implements SearchProgressListener {

    TextTable table;
    SearchProgressTableModel tableModel;

    public CLISearchProgress() {
        tableModel = new SearchProgressTableModel();
        table = new TextTable(tableModel);
    }

    @Override
    public void progress(SubtitleProvider provider, int jobsLeft, Release release) {
        this.tableModel.update(provider.getName(), jobsLeft, release == null ? "Done" : release.getFilename());
        this.printProgress();
    }

    @Override
    public void progress(int progress) {
        setProgress(progress);
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
        if (!isEnabled()) {
            return;
        }
        System.out.println("Error: " + exception.getMessage());
    }

    @Override
    public void onStatus(String message) {
        if (!isEnabled()) {
            return;
        }
        System.out.println(message);
    }

    @Override
    protected void printProgress() {
        if (!isEnabled()) {
            return;
        }

        /* print table */
        if (isVerbose()) {
            System.out.println("");
            table.printTable();
        }

        /* print progressbar */
        this.printProgBar(this.getProgress());
    }
}
