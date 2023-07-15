package org.lodder.subtools.multisubdownloader.gui.workers;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.UserInteractionHandlerGUI;
import org.lodder.subtools.multisubdownloader.actions.DownloadAction;
import org.lodder.subtools.multisubdownloader.actions.UserInteractionHandlerAction;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.extra.table.CustomTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.lib.Info;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA. User: lodder Date: 4/12/11 Time: 8:52 AM To change this template use
 * Path | Settings | Path Templates.
 */
public class DownloadWorker extends SwingWorker<Void, String> implements Cancelable {

    private final CustomTable table;
    private final Settings settings;
    private final DownloadAction downloadAction;
    private final UserInteractionHandlerAction userInteractionHandlerAction;

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadWorker.class);

    public DownloadWorker(CustomTable table, Settings settings, Manager manager, GUI gui) {
        this.table = table;
        this.settings = settings;
        UserInteractionHandlerGUI userInteractionHandler = new UserInteractionHandlerGUI(settings, gui);
        this.downloadAction = new DownloadAction(settings, manager, userInteractionHandler);
        this.userInteractionHandlerAction = new UserInteractionHandlerAction(settings, userInteractionHandler);
    }

    @Override
    protected Void doInBackground() {
        VideoTableModel model = (VideoTableModel) table.getModel();
        LOGGER.trace("doInBackground: Rows to treat: {} ", model.getRowCount());
        Info.downloadOptions(settings, false);

        model.executedSynchronized(() -> {
            List<Release> selectedShows = model.getSelectedShows();
            int selectedCount = selectedShows.size();
            int progress = 0;
            int k = 0;
            for (Release selectedShow : selectedShows) {
                k++;
                if (k > 0) {
                    progress = 100 * k / selectedCount;
                }
                if (progress == 0 && selectedCount > 1) {
                    progress = 1;
                }
                setProgress(progress);
                publish(selectedShow.getFileName());
                List<Subtitle> selection = userInteractionHandlerAction.subtitleSelection(selectedShow, true);
                try {
                    for (int j = 0; j < selection.size(); j++) {
                        downloadAction.download(selectedShow, selection.get(j), selection.size() == 1 ? null : j + 1);
                    }
                    if (!selection.isEmpty()) {
                        model.removeShow(selectedShow);
                    }
                } catch (IOException | ManagerException e) {
                    LOGGER.error(e.getMessage(), e);
                    showErrorMessage(e.toString());
                }
            }
        });
        return null;
    }

    @Override
    protected void process(List<String> data) {
        data.forEach(s -> StatusMessenger.instance.message(Messages.getString("MainWindow.DownloadingSubtitle", s)));
    }

    private void showErrorMessage(String message) {
        JOptionPane.showConfirmDialog(null, message, "JBierSubDownloader", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
    }

}
