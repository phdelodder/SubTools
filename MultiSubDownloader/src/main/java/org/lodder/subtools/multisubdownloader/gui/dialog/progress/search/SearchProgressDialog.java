package org.lodder.subtools.multisubdownloader.gui.dialog.progress.search;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.dialog.MultiSubDialog;
import org.lodder.subtools.multisubdownloader.listeners.SearchProgressListener;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.sublibrary.model.Release;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.miginfocom.swing.MigLayout;

public class SearchProgressDialog extends MultiSubDialog implements SearchProgressListener {

    private static final long serialVersionUID = -1331536352530988442L;
    private final Cancelable searchAction;
    private final GUI window;
    private SearchProgressTableModel tableModel;
    private JProgressBar progressBar;
    private boolean completed;

    public SearchProgressDialog(GUI window, Cancelable searchAction) {
        super(window, Messages.getString("SearchProgressDialog.Title"), false);
        this.searchAction = searchAction;
        this.window = window;
        this.completed = false;

        initialize_ui();
        setDialogLocation(window);
        repaint();
    }

    @Override
    public void progress(SubtitleProvider provider, int jobsLeft, Release release) {
        this.setVisible();
        this.tableModel.update(provider.getName(), jobsLeft,
                release == null ? "Done" : release.getFileName());
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

    private void initialize_ui() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                searchAction.cancel(true);
            }
        });
        setBounds(100, 100, 601, 300);
        getContentPane().setLayout(new MigLayout("", "[grow,fill][]", "[][][]"));

        this.tableModel = new SearchProgressTableModel();
        JTable table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setMinWidth(120);
        table.getColumnModel().getColumn(0).setMaxWidth(150);
        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(50);

        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setViewportView(table);
        getContentPane().add(tablePane, "cell 0 0 2 1");

        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        getContentPane().add(progressBar, "cell 0 1 2 1,grow");

        JButton btnStop = new JButton(Messages.getString("SearchProgressDialog.Stop"));
        btnStop.addActionListener(arg0 -> searchAction.cancel(true));
        getContentPane().add(btnStop, "cell 1 2,alignx left");
    }

    private void setVisible() {
        if (this.completed || this.isVisible()) {
            return;
        }
        this.setVisible(true);
    }
}
