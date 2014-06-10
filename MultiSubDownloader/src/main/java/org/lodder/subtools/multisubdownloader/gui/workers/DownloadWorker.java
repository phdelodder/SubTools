package org.lodder.subtools.multisubdownloader.gui.workers;

import javax.swing.*;

import org.lodder.subtools.multisubdownloader.gui.dialog.SelectDialog;
import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SearchColumnName;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.lib.Actions;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.VideoFile;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: lodder Date: 4/12/11 Time: 8:52 AM To change this template use
 * File | Settings | File Templates.
 */
public class DownloadWorker extends SwingWorker<Void, String> {

  private final VideoTable table;
  private final Actions actions;

  public DownloadWorker(VideoTable table, Settings settings) {
    this.table = table;
    actions = new Actions(settings, false);
  }

  protected Void doInBackground() throws Exception {
    Logger.instance.trace(DownloadWorker.class.toString(), "doInBackground", "Rows to thread: " + table.getModel().getRowCount());
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
        final VideoFile videoFile =
            (VideoFile) model.getValueAt(i, table.getColumnIdByName(SearchColumnName.OBJECT));
        publish(videoFile.getFilename());
        int selection = actions.determineWhatSubtitleDownload(videoFile, true);
        if (selection >= 0) {
          try {
            if (selection == SelectDialog.SelectionType.ALL.getSelectionCode()) {
              Logger.instance.debug("Downloaded ALL subs for episode: " + videoFile.getFilename());
              for (int j = 0; j < videoFile.getMatchingSubs().size(); j++) {
                actions.download(videoFile, videoFile.getMatchingSubs().get(j), j + 1);
              }
            } else {
              Logger.instance.debug("Downloaded subs for episode: " + videoFile.getFilename()
                  + " using these subs: " + videoFile.getMatchingSubs().get(0).getFilename());
              actions.download(videoFile, videoFile.getMatchingSubs().get(selection));
            }
            model.removeRow(i);
            i--;
          } catch (final Exception e) {
            Logger.instance.log(Logger.stack2String(e));
            showErrorMessage(Logger.stack2String(e));
          }
        }
      }
    }
    return null;
  }

  protected void process(List<String> data) {
    for (String s : data)
      StatusMessenger.instance.message("Ondertitel downloaden voor: " + s);
  }

  private void showErrorMessage(String message) {
    JOptionPane.showConfirmDialog(null, message, "JBierSubDownloader", JOptionPane.CLOSED_OPTION,
        JOptionPane.ERROR_MESSAGE);
  }

}
