package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.util.List;

import org.lodder.subtools.multisubdownloader.GUI;
import org.lodder.subtools.multisubdownloader.actions.SearchAction;
import org.lodder.subtools.multisubdownloader.exceptions.SearchSetupException;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchPanel;
import org.lodder.subtools.multisubdownloader.lib.ReleaseFactory;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.Filtering;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public abstract class GuiSearchAction extends SearchAction {

  protected GUI mainwindow;
  protected SearchPanel searchPanel;
  protected Filtering filtering;
  protected ReleaseFactory releaseFactory;

  public void setGUI(GUI mainwindow) {
    this.mainwindow = mainwindow;
  }

  public void setReleaseFactory(ReleaseFactory releaseFactory) {
    this.releaseFactory = releaseFactory;
  }

  public void setSearchPanel(SearchPanel searchPanel) {
    this.searchPanel = searchPanel;
  }

  public void setFiltering(Filtering filtering) {
    this.filtering = filtering;
  }

  @Override
  protected String getLanguageCode() {
    String language = this.searchPanel.getInputPanel().getSelectedLanguage();
    return this.getLanguageCode(language);
  }

  @Override
  public void onFound(Release release, List<Subtitle> subtitles) {
    if (Thread.currentThread().isInterrupted()) return;

    VideoTableModel model =
        (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();

    if (model.getRowCount() > 0) {
      searchPanel.getResultPanel().enableButtons();
    }

    if (this.searchManager.getProgress() == 100) {
      this.searchProgressListener.completed();
      searchPanel.getInputPanel().enableSearchButton();
    }
  }

  @Override
  protected void validate() throws SearchSetupException {
    if (this.mainwindow == null) {
      throw new SearchSetupException("GUI must be set.");
    }

    /* Create ProgressListeners */
    /*
     * The progressDialogs were re-used after the completed()-call and thus not shown. A
     * reset()-method might get implemented. But for now the GuiSearchAction will get a reference to
     * GUI and creates the listeners.
     */
    this.setSearchProgressListener(this.mainwindow.createSearchProgressDialog(this));
    this.setIndexingProgressListener(this.mainwindow.createFileIndexerProgressDialog(this));
    
    this.setStatusListener(this.indexingProgressListener);

    if (this.searchPanel == null) {
      throw new SearchSetupException("SearchPanel must be set.");
    }
    if (this.releaseFactory == null) {
      throw new SearchSetupException("ReleaseFactory must be set.");
    }

    super.validate();
  }
}
