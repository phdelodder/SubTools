package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lodder.subtools.multisubdownloader.gui.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchFileInputPanel;
import org.lodder.subtools.multisubdownloader.lib.Actions;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.Filtering;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class FileSearchAction extends GuiSearchAction {

  private Actions actions;
  private ReleaseFactory releaseFactory;
  private Filtering filtering;

  public FileSearchAction(Settings settings, SubtitleProviderStore subtitleProviderStore) {
    super();
    this.setSettings(settings);
    this.setProviderStore(subtitleProviderStore);
  }

  public void setActions(Actions actions) {
    this.actions = actions;
  }

  public void setReleaseFactory(ReleaseFactory releaseFactory) {
    this.releaseFactory = releaseFactory;
  }

  public void setFiltering(Filtering filtering){
    this.filtering = filtering;
  }

  @Override
  public void onFound(Release release, List<Subtitle> subtitles) {
    VideoTableModel model =
        (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();

    if (filtering != null) {
      subtitles = filtering.getFiltered(subtitles, release);
    }

    release.getMatchingSubs().addAll(subtitles);

    model.addRow(release);

    /* Let GuiSearchAction also make some decisions */
    super.onFound(release, subtitles);
  }

  @Override
  protected List<Release> createReleases() throws ActionException {
    SearchFileInputPanel inputPanel = getInputPanel();
    String filePath = inputPanel.getIncomingPath();
    String languageCode = getLanguageCode(inputPanel.getSelectedLanguage());
    boolean recursive = inputPanel.isRecursiveSelected();
    boolean overwriteExistingSubtitles = inputPanel.isForceOverwrite();

    /* get a list of videofiles */
    List<File> files = getFiles(filePath, languageCode, recursive, overwriteExistingSubtitles);

    /* create a list of releases from videofiles */
    return createReleases(files);
  }

  private List<Release> createReleases(List<File> files) throws ActionException {
    /* parse every videofile */
    List<Release> releases = new ArrayList<>();

    int total = files.size();
    int index = 0;
    int progress = 0;

    this.indexingProgressListener.progress(progress);

    for (File file : files) {
      index++;
      progress = (int) Math.floor((float) index / total * 100);

      /* Tell progressListener which file we are processing */
      this.indexingProgressListener.progress(file.getName());

      Release r = releaseFactory.createRelease(file);
      if (r != null) {
        releases.add(r);
      }

      /* Update progressListener */
      this.indexingProgressListener.progress(progress);
    }

    return releases;
  }

  private List<File> getFiles(String filePath, String languageCode, boolean recursive,
                              boolean overwriteExistingSubtitles) {
    /* Get a list of selected directories */
    List<File> dirs = new ArrayList<>();
    if (!filePath.isEmpty()) {
      dirs.add(new File(filePath));
    } else {
      dirs.addAll(this.settings.getDefaultFolders());
    }

    /* Scan directories for videofiles */
    List<File> files = new ArrayList<>();

    /* Tell Action where to send progressUpdates */
    this.actions.setIndexingProgressListener(this.indexingProgressListener);

    /* Start the getFileListing Action */
    for (File dir : dirs) {
      files.addAll(
          this.actions.getFileListing(dir, recursive, languageCode, overwriteExistingSubtitles));
    }
    return files;
  }

  protected void validate() throws SearchSetupException {
    super.validate();
    String path = getInputPanel().getIncomingPath();

    if (path.equals("") && !this.settings.hasDefaultFolders()) {
      throw new SearchSetupException("Geen map geselecteerd");
    }
  }

  private SearchFileInputPanel getInputPanel() {
    return (SearchFileInputPanel) this.searchPanel.getInputPanel();
  }

}
