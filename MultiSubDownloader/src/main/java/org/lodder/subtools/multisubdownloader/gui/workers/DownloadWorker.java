package org.lodder.subtools.multisubdownloader.gui.workers;

import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.UserInteractionHandlerGUI;
import org.lodder.subtools.multisubdownloader.actions.DownloadAction;
import org.lodder.subtools.multisubdownloader.actions.UserInteractionHandlerAction;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.extra.table.CustomTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SearchColumnName;
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
 * File | Settings | File Templates.
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
        downloadAction = new DownloadAction(settings, manager, userInteractionHandler);
        userInteractionHandlerAction = new UserInteractionHandlerAction(settings, userInteractionHandler);
    }

    @Override
    protected Void doInBackground() {
        LOGGER.trace("doInBackground: Rows to thread: {} ", table.getModel().getRowCount());
        Info.downloadOptions(settings, false);
        final VideoTableModel model = (VideoTableModel) table.getModel();
        int selectedCount = model.getSelectedCount(table.getColumnIdByName(SearchColumnName.SELECT));
        int progress = 0;
        int k = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Boolean) model.getValueAt(i, table.getColumnIdByName(SearchColumnName.SELECT))) {
                k++;
                if (k > 0) {
                    progress = 100 * k / selectedCount;
                }
                if (progress == 0 && selectedCount > 1) {
                    progress = 1;
                }
                setProgress(progress);
                final Release release =
                        (Release) model.getValueAt(i, table.getColumnIdByName(SearchColumnName.OBJECT));
                publish(release.getFileName());
                List<Subtitle> selection = userInteractionHandlerAction.subtitleSelection(release, true);
                try {
                    for (int j = 0; j < selection.size(); j++) {
                        downloadAction.download(release, release.getMatchingSubs().get(j), selection.size() == 1 ? null : j + 1);
                    }
                    if (!selection.isEmpty()) {
                        model.removeRow(i);
                        i--;
                    }
                } catch (IOException | ManagerException e) {
                    LOGGER.error(e.getMessage(), e);
                    showErrorMessage(e.toString());
                }
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> data) {
        data.forEach(s -> StatusMessenger.instance.message("Ondertitel downloaden voor: " + s));
    }

    private void showErrorMessage(String message) {
        JOptionPane.showConfirmDialog(null, message, "JBierSubDownloader", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
    }

}
