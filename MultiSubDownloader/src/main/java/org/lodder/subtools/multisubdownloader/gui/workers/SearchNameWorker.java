package org.lodder.subtools.multisubdownloader.gui.workers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import org.lodder.subtools.multisubdownloader.gui.extra.progress.StatusMessenger;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.lib.control.NameSearchControl;
import org.lodder.subtools.multisubdownloader.lib.control.VideoFileFactory;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.VideoFile;
import org.lodder.subtools.sublibrary.model.VideoSearchType;

public class SearchNameWorker extends SwingWorker<List<Subtitle>, String> {

  private Settings settings;
  private VideoTable table;
  private NameSearchControl tsc;
  private String videoText, languagecode, quality;
  private int season, episode;
  private VideoSearchType videoSearchTypeChoice;

  public SearchNameWorker(VideoTable table, Settings settings) {
    this.table = table;
    this.settings = settings;
    tsc = new NameSearchControl(settings);
  }

  public void setParameters(VideoSearchType videoTypeChoice, String videoText, int season,
      int episode, String languagecode, String quality) {
    this.videoSearchTypeChoice = videoTypeChoice;
    this.videoText = videoText;
    this.languagecode = languagecode;
    this.season = season;
    this.episode = episode;
    this.quality = quality;
  }

  @Override
  protected List<Subtitle> doInBackground() throws Exception {
    Logger.instance.debug("----- Subtitle Filtering ------");
    Logger.instance.debug(" - OptionSubtitleExactMatch : " + settings.isOptionSubtitleExactMatch());
    Logger.instance.debug(" - OptionSubtitleKeywordMatch : "
        + settings.isOptionSubtitleKeywordMatch());
    Logger.instance.debug("-------------------------------");

    int progress = 0;
    setProgress(progress);
    List<Subtitle> l = new ArrayList<Subtitle>();
    if (videoSearchTypeChoice.equals(VideoSearchType.EPISODE)) {
      l = tsc.SearchSubtitles(videoText, season, episode, languagecode);
    } else if (videoSearchTypeChoice.equals(VideoSearchType.MOVIE)) {
      l = tsc.SearchSubtitles(videoText, languagecode);
    } else if (videoSearchTypeChoice.equals(VideoSearchType.RELEASE)) {
      VideoFile videoFile =
          VideoFileFactory.get(new File(videoText), new File("/"), settings, languagecode);
      if (videoFile != null) {
        l = videoFile.getMatchingSubs();
      }
    }
    return l;
  }

  protected void process(List<String> data) {
    for (String s : data)
      StatusMessenger.instance.message("Bezig aan het zoeken naar titel: " + s);
  }

  @Override
  protected void done() {
    List<Subtitle> l;
    try {
      l = get();
      VideoTableModel model = (VideoTableModel) table.getModel();
      Pattern p = Pattern.compile(quality, Pattern.CASE_INSENSITIVE);
      for (Subtitle aL : l) {

        Matcher m = p.matcher(aL.getFilename());
        if (m.find()) {
          model.addRow(aL);
        }
      }
    } catch (Exception e) {
      Logger.instance.error(e.getMessage());
    }
  }
}
