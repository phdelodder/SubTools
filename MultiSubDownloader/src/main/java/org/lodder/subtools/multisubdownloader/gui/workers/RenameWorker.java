package org.lodder.subtools.multisubdownloader.gui.workers;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import org.lodder.subtools.multisubdownloader.actions.RenameAction;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SearchColumnName;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.VideoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA. User: lodder Date: 4/12/11 Time: 8:52 AM To change this template use
 * File | Settings | File Templates.
 */
public class RenameWorker extends SwingWorker<Void, String> implements Cancelable {

  private VideoTable table;
  private Settings settings;
  private RenameAction renameAction;
  private Manager manager;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(RenameWorker.class);

  public RenameWorker(VideoTable table, Settings settings, Manager manager) {
    this.table = table;
    this.settings = settings;
    this.manager = manager;
  }

  protected Void doInBackground() throws Exception {
    final VideoTableModel model = (VideoTableModel) table.getModel();
    int selectedCount = model.getSelectedCount(table.getColumnIdByName(SearchColumnName.SELECT));
    int progress = 0;
    int k = 0;
    for (int i = 0; i < model.getRowCount(); i++) {
      if ((Boolean) model.getValueAt(i, table.getColumnIdByName(SearchColumnName.SELECT))) {
        k++;
        if (k > 0) progress = 100 * k / selectedCount;
        if (progress == 0 && selectedCount > 1) progress = 1;
        setProgress(progress);
        final Release release =
            (Release) model.getValueAt(i, table.getColumnIdByName(SearchColumnName.OBJECT));
        if (release.getVideoType() == VideoType.EPISODE) {
          LOGGER.debug("Treat as EPISODE");
          renameAction = new RenameAction(settings.getEpisodeLibrarySettings(), manager);
        } else if (release.getVideoType() == VideoType.MOVIE) {
          LOGGER.debug("Treat as MOVIE");
          renameAction = new RenameAction(settings.getMovieLibrarySettings(), manager);
        }
        if (renameAction != null)
          renameAction.rename(new File(release.getPath(), release.getFilename()), release);
        model.removeRow(i);
        i--;
      }
    }
    return null;
  }

  protected void process(List<String> data) {
    for (String s : data)
      StatusMessenger.instance.message("Bestand hernoemen: " + s);
  }
}
