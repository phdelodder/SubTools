package org.lodder.subtools.multisubdownloader.gui.actions.search;

import org.lodder.subtools.multisubdownloader.MainWindow;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchTextInputPanel;
import org.lodder.subtools.multisubdownloader.gui.workers.SearchNameWorker;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.model.VideoSearchType;

public class TextSearchAction extends SearchAction {

  public TextSearchAction(MainWindow mainWindow, Settings settings) {
    super(mainWindow, settings);
  }

  @Override
  protected void createSearchWorker() {
    // Todo: extract to factory?
    setWorker(new SearchNameWorker(searchPanel.getResultPanel().getTable(),this.settings));

    SearchTextInputPanel inputPanel = getInputPanel();
    SearchNameWorker worker = getWorker();

    Logger.instance.trace(this.getClass().toString(), "createSearchWorker", "Setting parameters for searchWorker");

    VideoSearchType type = inputPanel.getType();
    String releaseName = inputPanel.getReleaseName();
    int season = inputPanel.getSeason();
    int episode = inputPanel.getEpisode();
    String languageCode = getLanguageCode(inputPanel.getSelectedLanguage());
    String quality = inputPanel.getQuality();

    Logger.instance.debug("SearchWorker Parameter: releasetype => '"+ type +"'");
    Logger.instance.debug("SearchWorker Parameter: releasename => '"+ releaseName +"'");
    Logger.instance.debug("SearchWorker Parameter: season => '"+ season +"'");
    Logger.instance.debug("SearchWorker Parameter: episode => '"+ episode +"'");
    Logger.instance.debug("SearchWorker Parameter: langcode => '"+ languageCode +"'");
    Logger.instance.debug("SearchWorker Parameter: quality => '"+ quality +"'");

    worker.setReleaseType(type);
    worker.setReleaseName(releaseName);
    worker.setSeason(season);
    worker.setEpisode(episode);
    worker.setLanguageCode(languageCode);
    worker.setQuality(quality);
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

  private SearchNameWorker getWorker() {
    return (SearchNameWorker) this.worker;
  }
}
