package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.lodder.subtools.multisubdownloader.MainWindow;
import org.lodder.subtools.multisubdownloader.gui.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchTextInputPanel;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.VideoFileParseException;
import org.lodder.subtools.sublibrary.model.*;

public class TextSearchAction extends SearchAction {

  private ReleaseParser releaseParser;

  public TextSearchAction(MainWindow mainWindow, Settings settings, SubtitleProviderStore subtitleProviderStore) {
    super(mainWindow, settings, subtitleProviderStore);
  }

  public void setReleaseParser(ReleaseParser releaseParser) {
    this.releaseParser = releaseParser;
  }

  @Override
  protected List<Release> createReleases() throws ActionException {
    String name = getInputPanel().getReleaseName();
    VideoSearchType type = getInputPanel().getType();

    // TODO: Redefine what a "release" is.
    Release release;
    if (type.equals(VideoSearchType.EPISODE)) {
      int season = getInputPanel().getSeason();
      int episode = getInputPanel().getEpisode();
      String quality = getInputPanel().getQuality();

      release = createTvRelease(name, season, episode, quality);
    } else if (type.equals(VideoSearchType.MOVIE)) {
      String quality = getInputPanel().getQuality();

      release = createMovieRelease(name, quality);
    } else {
      try {
        release = releaseParser.parse(new File(name), new File("/"));
      } catch (VideoFileParseException e) {
        throw new ActionException(e);
      }
    }

    List<Release> releases = new ArrayList<>();
    releases.add(release);

    return releases;
  }

  private Release createMovieRelease(String name, String quality) {
    Release release;
    release = new MovieRelease();
    ((MovieRelease) release).setTitle(name);
    release.setQuality(quality);
    return release;
  }

  private Release createTvRelease(String name, int season, int episode, String quality) {
    Release release;
    List<Integer> episodes = new ArrayList<>();
    episodes.add(episode);

    release = new TvRelease();
    ((TvRelease) release).setShow(name);
    ((TvRelease) release).setSeason(season);
    ((TvRelease) release).setEpisodeNumbers(episodes);
    release.setQuality(quality);
    return release;
  }

  @Override
  protected void onFoundSubtitles(Release release, List<Subtitle> subtitles) {
    VideoTableModel model = (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();

    release.getMatchingSubs().addAll(subtitles);

    // TODO: re-sort table without losing states ( selected/unselected )
    /* add the found subtitles to the table */
    for (Subtitle subtitle : subtitles) {
      model.addRow(subtitle);
    }
  }

  @Override
  protected void postFound() {
    /* never show the getSelected button for text-based search */
    this.searchPanel.getResultPanel().hideSelectFoundSubtitlesButton();
  }

  @Override
  protected void inputCheck() throws SearchSetupException {
    if (getInputPanel().getReleaseName().isEmpty()) {
      throw new SearchSetupException("Geen Movie/Episode/Release opgegeven");
    }
  }

  private SearchTextInputPanel getInputPanel() {
    return (SearchTextInputPanel) this.searchPanel.getInputPanel();
  }
}
