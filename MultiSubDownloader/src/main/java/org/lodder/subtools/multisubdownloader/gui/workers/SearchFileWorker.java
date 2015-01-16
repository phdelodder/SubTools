package org.lodder.subtools.multisubdownloader.gui.workers;

import javax.swing.*;

import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.lib.Actions;
import org.lodder.subtools.multisubdownloader.lib.Info;
import org.lodder.subtools.multisubdownloader.lib.control.VideoFileFactory;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.VideoFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchFileWorker extends SwingWorker<List<VideoFile>, String> {

  private boolean recursieve;
  private String languagecode;
  private List<File> dirs;
  private final Actions actions;
  private Settings settings;
  private VideoTable table;
  private boolean forceSubtitleOverwrite;
  private List<VideoFile> list;

  public SearchFileWorker(VideoTable table, Settings settings) {
    this.table = table;
    this.settings = settings;
    actions = new Actions(settings, false);
  }

  @SuppressWarnings("serial")
  public void setParameters(final File dir, String languagecode, boolean recursieve,
      boolean forceSubtitleOverwrite) {
    this.dirs = new ArrayList<File>() {
      {
        add(dir);
      }
    };
    this.languagecode = languagecode;
    this.recursieve = recursieve;
    this.forceSubtitleOverwrite = forceSubtitleOverwrite;
  }

  public void setParameters(List<File> dirs, String languagecode, boolean recursieve,
      boolean forceSubtitleOverwrite) {
    this.dirs = dirs;
    this.languagecode = languagecode;
    this.recursieve = recursieve;
    this.forceSubtitleOverwrite = forceSubtitleOverwrite;
  }

  @Override
  protected List<VideoFile> doInBackground() throws Exception {
    Info.subtitleFiltering(settings);

    int progress = 0;
    setProgress(progress);
    list = new ArrayList<VideoFile>();
    for (File dir : dirs) {
      List<File> files =
          actions.getFileListing(dir, recursieve, languagecode, forceSubtitleOverwrite);
      for (int i = 0; i < files.size(); i++) {
        if (i > 0) progress = 100 * i / files.size();
        if (progress == 0 && files.size() > 1) progress = 1;
        setProgress(progress);
        publish(files.get(i).getName());
        try {
          VideoFile videoFile = VideoFileFactory.get(files.get(i), dir, settings, languagecode);
          if (videoFile != null) list.add(videoFile);
        } catch (Exception e) {
          Logger.instance.log("Error processing file " + Logger.stack2String(e));
          if (settings.isOptionsStopOnSearchError())
            throw new Exception("Fout opgetreden met 'Stop zoeken na fout' aangevinked");
        }
      }
    }
    return list;
  }

  protected void process(List<String> data) {
    for (String s : data)
      StatusMessenger.instance.message("Bezig aan het verwerken: " + s);
  }

  @Override
  protected void done() {
    List<VideoFile> l;
    try {
      final VideoTableModel model = (VideoTableModel) table.getModel();
      if (isCancelled()) {
        if (list != null) model.addRows(list);
      } else {
        l = get();
        model.addRows(l);
      }
    } catch (Exception e) {
      Logger.instance.error(e.getMessage());
    }
  }
}
