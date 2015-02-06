package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.util.List;

import org.lodder.subtools.multisubdownloader.MainWindow;
import org.lodder.subtools.multisubdownloader.gui.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.dialog.Cancelable;
import org.lodder.subtools.multisubdownloader.gui.dialog.progress.fileindexer.FileIndexerProgressListener;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchPanel;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.multisubdownloader.workers.SearchHandler;
import org.lodder.subtools.multisubdownloader.workers.SearchManager;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public abstract class SearchAction implements Cancelable, SearchHandler {

  protected MainWindow mainWindow;
  protected Settings settings;
  protected SubtitleProviderStore subtitleProviderStore;
  protected SearchPanel searchPanel;
  protected SearchManager searchManager;
  protected List<Release> releases;
  protected FileIndexerProgressListener fileIndexerProgressListener;

  public SearchAction(MainWindow mainWindow, Settings settings, SubtitleProviderStore subtitleProviderStore) {
    this.mainWindow = mainWindow;
    this.settings = settings;
    this.subtitleProviderStore = subtitleProviderStore;
  }

  public void setSearchPanel(SearchPanel searchPanel) {
    this.searchPanel = searchPanel;
  }

  public void execute() {
    Logger.instance.trace(this.getClass().toString(), "execute", "SearchAction is being executed");
    try {
      this.search();
    } catch (ActionException e) {
      this.mainWindow.showErrorMessage(e.getMessage());
    }
  }

  public void search() throws ActionException {
    inputCheck();

    String languageCode = this.getLanguageCode(this.searchPanel.getInputPanel().getSelectedLanguage());

    this.fileIndexerProgressListener = this.mainWindow.createFileIndexerProgressDialog(this);

    setStatusMessage("Indexing...");

    this.releases = createReleases();

    this.mainWindow.hideFileIndexerProgressDialog();

    if (this.releases.size() <= 0)
      return;

    /* Create a new SearchManager. */
    this.searchManager = new SearchManager(this.settings);

    /* Tell the manager which language we want */
    this.searchManager.setLanguage(languageCode);

    /* Tell the manager which providers to use */
    for (SubtitleProvider subtitleProvider : this.subtitleProviderStore.getAllProviders()) {
      if (!settings.isSerieSource(subtitleProvider.getName())) continue;
      
      this.searchManager.addProvider(subtitleProvider);
    }

    /* Tell the manager which releases to search. */
    for (Release release : this.releases) {
      this.searchManager.addRelease(release);
    }

    /* Listen for when the manager tells us Subtitles are found */
    this.searchManager.onFound(this);

    /* Tell the manager where to push progressUpdates */
    this.searchManager.setProgressListener(this.mainWindow.createSearchProgressDialog(this));

    this.searchPanel.getInputPanel().disableSearchButton();

    setStatusMessage("Zoeken...");

    this.searchPanel.getResultPanel().clearTable();

    /* Tell the manager to start searching */
    this.searchManager.start();
  }

  protected abstract List<Release> createReleases() throws ActionException;

  protected abstract void onFoundSubtitles(Release release, List<Subtitle> subtitles);

  protected void setStatusMessage(String message) {
    this.mainWindow.setStatusMessage(message);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    mainWindow.hideFileIndexerProgressDialog();
    mainWindow.hideSearchProgressDialog();
    searchPanel.getInputPanel().enableSearchButton();
    return this.searchManager.cancel(mayInterruptIfRunning);
  }

  @Override
  public void onFound(Release release, List<Subtitle> subtitles) {
    this.onFoundSubtitles(release, subtitles);

    VideoTableModel model = (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();

    if(model.getRowCount() > 0) {
      searchPanel.getResultPanel().enableButtons();
    }

    if (this.searchManager.getProgress() == 100) {
      this.mainWindow.hideSearchProgressDialog();
      searchPanel.getInputPanel().enableSearchButton();
    }

    this.postFound();
  }

  protected abstract void postFound();

  protected String getLanguageCode(String language) {
    if (language.equals("Nederlands")) {
      return "nl";
    } else if (language.equals("Engels")) {
      return "en";
    }
    return null;
  }

  protected abstract void inputCheck() throws SearchSetupException;

}
