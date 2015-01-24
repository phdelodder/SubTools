package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.io.File;
import org.lodder.subtools.multisubdownloader.MainWindow;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchFileInputPanel;
import org.lodder.subtools.multisubdownloader.gui.workers.SearchFileWorker;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;

public class FileSearchAction extends SearchAction {

  public FileSearchAction(MainWindow mainWindow, Settings settings) {
    super(mainWindow, settings);
  }

  protected void createSearchWorker() {
    // Todo: extract to factory?
    setWorker(new SearchFileWorker(this.searchPanel.getResultPanel().getTable(),this.settings));

    SearchFileInputPanel inputPanel = getInputPanel();
    SearchFileWorker worker = getWorker();

    Logger.instance.trace(this.getClass().toString(), "createSearchWorker", "Setting parameters for searchWorker");

    String filePath = inputPanel.getIncomingPath();
    String languageCode = getLanguageCode(inputPanel.getSelectedLanguage());
    boolean recursive = inputPanel.isRecursiveSelected();
    boolean overwriteExistingSubtitles = inputPanel.isForceOverwrite();

    if(!filePath.equals("")) {
      Logger.instance.debug("SearchWorker Parameter: dirs => '"+ filePath +"'");
      worker.setDirs(new File(filePath));
    }else if(this.settings.hasDefaultFolders()) {
      Logger.instance.debug("SearchWorker Parameter: dirs => 'defaultFolders'");
      worker.setDirs(this.settings.getDefaultFolders());
    }

    Logger.instance.debug("SearchWorker Parameter: langcode => '"+ languageCode +"'");
    Logger.instance.debug("SearchWorker Parameter: isrecursice => '"+ recursive +"'");
    Logger.instance.debug("SearchWorker Parameter: overwritesubtitles => '"+ overwriteExistingSubtitles +"'");

    worker.setLanguageCode(languageCode);
    worker.setRecursive(recursive);
    worker.setOverwrite(overwriteExistingSubtitles);
  }

  protected void inputCheck() throws SearchSetupException {
    String path = getInputPanel().getIncomingPath();

    if(path.equals("") && !this.settings.hasDefaultFolders())
      throw new SearchSetupException("Geen map geselecteerd");
  }

  private SearchFileInputPanel getInputPanel() {
    return (SearchFileInputPanel) this.searchPanel.getInputPanel();
  }

  private SearchFileWorker getWorker() {
    return (SearchFileWorker) this.worker;
  }

}
