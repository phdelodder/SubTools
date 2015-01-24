package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;
import org.lodder.subtools.multisubdownloader.MainWindow;
import org.lodder.subtools.multisubdownloader.gui.actions.ActionException;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchPanel;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;

public abstract class SearchAction {

  protected MainWindow mainWindow;
  protected Settings settings;
  protected SearchPanel searchPanel;
  protected SwingWorker<?, ?> worker;

  public SearchAction(MainWindow mainWindow, Settings settings) {
    this.mainWindow = mainWindow;
    this.settings = settings;
  }

  public void setSearchPanel(SearchPanel searchPanel){
    this.searchPanel = searchPanel;
  }

  public void setWorker(final SwingWorker<?, ?> worker) {
    this.worker = worker;
    this.worker.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (worker.isDone()) {
          searchCompleted();
        } else {
          searchProgressed();
        }
      }
    });
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

    showProgressDialog();

    this.searchPanel.getInputPanel().disableSearchButton();

    setStatusMessage("Zoeken...");

    this.searchPanel.getResultPanel().clearTable();

    createSearchWorker();
    this.worker.execute();
  }

  protected void setStatusMessage(String message) {
    this.mainWindow.setStatusMessage(message);
  }

  protected void showProgressDialog() {
    this.mainWindow.setProgressDialog(this.worker);
    this.mainWindow.showProgressDialog();
  }

  protected String getLanguageCode(String language) {
    if (language.equals("Nederlands")) {
      return "nl";
    } else if (language.equals("Engels")) {
      return "en";
    }
    return null;
  }

  protected abstract void createSearchWorker();

  protected abstract void inputCheck() throws SearchSetupException;

  protected void searchCompleted() {
    Logger.instance.trace(this.getClass().toString(), "searchCompleted", "SearchWorker has completed");

    int foundSubtitles = searchPanel.getResultPanel().getTable().getModel().getRowCount();
    Logger.instance.debug("Found " + foundSubtitles + " subtitles");

    if (foundSubtitles > 0) {
      searchPanel.getResultPanel().enableButtons();
    }

    setStatusMessage("Found " + foundSubtitles + " subtitles");

    mainWindow.hideProgressDialog();
    searchPanel.getInputPanel().enableSearchButton();
  }

  protected void searchProgressed() {
    Logger.instance.trace(this.getClass().toString(), "searchProgressed", "SearchWorker is busy");

    int progress = worker.getProgress();
    Logger.instance.debug("Progress: "+progress);

    mainWindow.updateProgressDialog(progress);

    if (progress == 0) {
      setStatusMessage("Bestanden lijst aan het opbouwen");
    } else {
      setStatusMessage("Bestanden aan het verwerken");
    }
  }
}
