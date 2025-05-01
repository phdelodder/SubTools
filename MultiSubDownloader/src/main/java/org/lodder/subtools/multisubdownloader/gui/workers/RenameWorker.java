package org.lodder.subtools.multisubdownloader.gui.workers;

import javax.swing.*;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.actions.RenameAction;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.extra.table.CustomTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.userinteraction.UserInteractionHandler;

/**
 * Created by IntelliJ IDEA. User: lodder Date: 4/12/11 Time: 8:52 AM To change this template use Path | Settings | Path
 * Templates.
 */
@RequiredArgsConstructor
public class RenameWorker extends SwingWorker<Void, String> implements Cancelable {

    private final CustomTable table;
    private final Settings settings;
    private final Manager manager;
    private final UserInteractionHandler userInteractionHandler;

    @Override
    protected Void doInBackground() {
        final VideoTableModel model = (VideoTableModel) table.getModel();

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

                RenameAction renameAction = switch (selectedShow.videoType) {
                    case EPISODE -> new RenameAction(settings.episodeLibrarySettings, manager, userInteractionHandler);
                    case MOVIE -> new RenameAction(settings.movieLibrarySettings, manager, userInteractionHandler);
                };
                renameAction.rename(selectedShow.getPath().resolve(selectedShow.fileName), selectedShow);
                model.removeShow(selectedShow);
            }
        });
        return null;
    }

    @Override
    protected void process(List<String> data) {
        data.forEach(s -> StatusMessenger.instance.message(Messages.getText("MainWindow.RenamingFile", s)));
    }
}
