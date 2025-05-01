package org.lodder.subtools.multisubdownloader.gui.dialog.progress.search;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.dialog.MultiSubDialog;
import org.lodder.subtools.multisubdownloader.listeners.SearchProgressListener;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.model.Release;

public class SearchProgressDialog extends MultiSubDialog implements SearchProgressListener {

    @Serial
    private static final long serialVersionUID = -1331536352530988442L;
    private final GUI window;
    private final SearchProgressTableModel tableModel;
    private final JProgressBar progressBar;
    private boolean completed;

    public SearchProgressDialog(GUI window, Cancelable searchAction) {
        super(window, getText("SearchProgressDialog.Title"), false);
        this.window = window;
        this.completed = false;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                searchAction.cancel(true);
            }
        });
        setBounds(100, 100, 601, 300);

        JTable table = new JTable(this.tableModel = new SearchProgressTableModel());
        TableColumn column1 = table.getColumnModel().getColumn(0);
        column1.minWidth = 120;
        column1.maxWidth = 150;
        TableColumn column2 = table.getColumnModel().getColumn(1);
        column2.minWidth = 50;
        column2.maxWidth = 50;

        contentPane
            .layout(new MigLayout("", "[grow,fill][]", "[][][]"))
            .addComponent("cell 0 0 2 1", new JScrollPane(table).viewportView(table))
            .addComponent("cell 0 1 2 1,grow", progressBar = new JProgressBar(0, 100).indeterminate(true))
            .addComponent("cell 1 2,alignx left",
                new JButton(getText("SearchProgressDialog.Stop"))
                    .actionListener(_ -> searchAction.cancel(true)));
        setDialogLocation(window);
        repaint();
    }

    @Override
    public void progress(SubtitleProvider provider, int jobsLeft, Release release) {
        this.setVisible();
        this.tableModel.update(provider.name, jobsLeft, release == null ? "Done" : release.fileName);
    }

    @Override
    public void progress(int progress) {
        this.setVisible();
        if (progress == 0) {
            this.progressBar.setIndeterminate(true);
        } else {
            this.progressBar.setIndeterminate(false);
            this.progressBar.setValue(progress);
            this.progressBar.setString(Integer.toString(progress));
        }
    }

    @Override
    public void completed() {
        this.completed = true;
        this.setVisible(false);
    }

    @Override
    public void reset() {
        this.completed = false;
        tableModel.clear();
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
