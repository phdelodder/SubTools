package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.MainWindow;
import org.lodder.subtools.multisubdownloader.gui.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchTextInputPanel;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.SubtitleSelection;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.Filtering;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.VideoSearchType;

public class TextSearchAction extends SearchAction {

  private ReleaseFactory releaseFactory;
  private Filtering filtering;
  private SubtitleSelection subtitleSelection;

  public TextSearchAction(MainWindow mainWindow, Settings settings,
      SubtitleProviderStore subtitleProviderStore) {
    super(mainWindow, settings, subtitleProviderStore);
  }

  public void setReleaseFactory(ReleaseFactory releaseFactory) {
    this.releaseFactory = releaseFactory;
  }

  public void setFiltering(Filtering filtering) {
    this.filtering = filtering;
  }

  public void setSubtitleSelection(SubtitleSelection subtitleSelection) {
    this.subtitleSelection = subtitleSelection;
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
      release = releaseFactory.createRelease(new File(name));
    }

    List<Release> releases = new ArrayList<>();
    if (release != null) releases.add(release);

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
    VideoTableModel model =
        (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();

    if (filtering != null) subtitles = filtering.getFiltered(subtitles, release);

    release.getMatchingSubs().addAll(subtitles);

    // use automatic selection to reduce the selection for the user
    if (subtitleSelection != null)
      subtitles = subtitleSelection.getAutomaticSelection(release.getMatchingSubs());

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
