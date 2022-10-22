package org.lodder.subtools.multisubdownloader.gui.dialog.progress.fileindexer;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.dialog.ProgressDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.listeners.IndexingProgressListener;

public class IndexingProgressDialog extends ProgressDialog implements IndexingProgressListener {

    private static final long serialVersionUID = 2974749335080501254L;
    private final GUI window;
    private boolean completed;

    public IndexingProgressDialog(GUI window, Cancelable sft) {
        super(window, sft);
        this.window = window;
        this.completed = false;
        StatusMessenger.instance.removeListener(this);
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
    public void reset() {
        this.completed = false;
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
        if (this.completed || this.isVisible()) {
            return;
        }
        this.setVisible(true);
    }
}
