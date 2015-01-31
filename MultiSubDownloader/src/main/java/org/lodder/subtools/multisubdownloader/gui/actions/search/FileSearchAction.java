package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.lodder.subtools.multisubdownloader.MainWindow;
import org.lodder.subtools.multisubdownloader.gui.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchFileInputPanel;
import org.lodder.subtools.multisubdownloader.lib.Actions;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.control.ReleaseParser;
import org.lodder.subtools.sublibrary.exception.VideoFileParseException;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class FileSearchAction extends SearchAction {

  private Actions actions;
  private ReleaseParser releaseParser;

  public FileSearchAction(MainWindow mainWindow, Settings settings, SubtitleProviderStore subtitleProviderStore) {
    super(mainWindow, settings, subtitleProviderStore);
  }

  public void setActions(Actions actions) {
    this.actions = actions;
  }

  public void setReleaseParser(ReleaseParser releaseParser) {
    this.releaseParser = releaseParser;
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
    try {
      for (File file : files) {
        releases.add(this.releaseParser.parse(file, file.getParentFile()));
      }
    } catch (VideoFileParseException e) {
      throw new ActionException(e);
    }

    return releases;
  }

  private List<File> getFiles(String filePath, String languageCode, boolean recursive, boolean overwriteExistingSubtitles) {
    /* Get a list of selected directories */
    List<File> dirs = new ArrayList<>();
    if (!filePath.isEmpty()) {
      dirs.add(new File(filePath));
    } else {
      dirs.addAll(this.settings.getDefaultFolders());
    }

    /* Scan directories for videofiles */
    List<File> files = new ArrayList<>();
    for (File dir : dirs) {
      files.addAll(this.actions.getFileListing(dir, recursive, languageCode, overwriteExistingSubtitles));
    }
    return files;
  }

  @Override
  public void onFoundSubtitles(Release release, List<Subtitle> subtitles) {
    VideoTableModel model = (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();

    release.getMatchingSubs().addAll(subtitles);

    model.addRow(release);
  }

  @Override
  protected void postFound() {

  }

  protected void inputCheck() throws SearchSetupException {
    String path = getInputPanel().getIncomingPath();

    if (path.equals("") && !this.settings.hasDefaultFolders())
      throw new SearchSetupException("Geen map geselecteerd");
  }

  private SearchFileInputPanel getInputPanel() {
    return (SearchFileInputPanel) this.searchPanel.getInputPanel();
  }

}
