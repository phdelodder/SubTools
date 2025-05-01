package org.lodder.subtools.multisubdownloader.cli.progress;

import dnl.utils.text.table.TextTable;
import org.lodder.subtools.multisubdownloader.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.dialog.progress.search.SearchProgressTableModel;
import org.lodder.subtools.multisubdownloader.listeners.SearchProgressListener;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.model.Release;

public final class CLISearchProgress extends CLIProgress implements SearchProgressListener {

    private final TextTable table;
    private final SearchProgressTableModel tableModel;

    public CLISearchProgress() {
        tableModel = new SearchProgressTableModel();
        table = new TextTable(tableModel);
    }

    @Override
    public void progress(SubtitleProvider provider, int jobsLeft, Release release) {
        this.tableModel.update(provider.name, jobsLeft, release == null ? "Done" : release.fileName);
        this.printProgress();
    }

    @Override
    public void progress(int progress) {
        this.progress = progress;
        this.printProgress();
    }

    @Override
    public void completed() {
        if (!this.enabled) {
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

        /* print table */
        if (verbose) {
            System.out.println();
            table.printTable();
        }

        /* print progressbar */
        this.printProgBar(this.progress);
    }

    // TODO: remove this when https://github.com/manifold-systems/manifold/issues/642 is fixed
    @Override
    public CLISearchProgress verbose(boolean verbose) {
        super.verbose(verbose);
        return this;
    }
}
