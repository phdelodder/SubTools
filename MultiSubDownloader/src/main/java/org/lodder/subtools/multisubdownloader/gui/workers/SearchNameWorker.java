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
import org.lodder.subtools.multisubdownloader.lib.Info;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.control.TextualSearchControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.VideoSearchType;

public class SearchNameWorker extends SwingWorker<List<Subtitle>, String> {

  private Settings settings;
  private VideoTable table;
  private TextualSearchControl tsc;
  private String videoText, languagecode, quality;
  private int season, episode;
  private VideoSearchType videoSearchTypeChoice;

  public SearchNameWorker(VideoTable table, Settings settings) {
    this.table = table;
    this.settings = settings;
    tsc = new TextualSearchControl(settings);
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
    Info.subtitleSources(settings);
    Info.subtitleFiltering(settings);

    int progress = 0;
    setProgress(progress);
    List<Subtitle> l = new ArrayList<Subtitle>();
    if (videoSearchTypeChoice.equals(VideoSearchType.EPISODE)) {
      l = tsc.SearchSubtitles(videoText, season, episode, languagecode);
    } else if (videoSearchTypeChoice.equals(VideoSearchType.MOVIE)) {
      l = tsc.SearchSubtitles(videoText, languagecode);
    } else if (videoSearchTypeChoice.equals(VideoSearchType.RELEASE)) {
      Release release =
          ReleaseFactory.get(new File(videoText), new File("/"), settings, languagecode);
      if (release != null) {
        l = release.getMatchingSubs();
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

  public void setReleaseType(VideoSearchType releaseType) {
    this.videoSearchTypeChoice = releaseType;
  }

  public void setReleaseName(String releaseName) {
    this.videoText = releaseName;
  }

  public void setSeason(int season) {
    this.season = season;
  }

  public void setEpisode(int episode) {
    this.episode = episode;
  }

  public void setLanguageCode(String languageCode) {
    this.languagecode = languageCode;
  }

  public void setQuality(String quality) {
    this.quality = quality;
  }
}
