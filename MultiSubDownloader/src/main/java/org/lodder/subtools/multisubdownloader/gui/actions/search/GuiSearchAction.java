package org.lodder.subtools.multisubdownloader.gui.actions.search;

import java.util.List;

import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;
import org.lodder.subtools.multisubdownloader.gui.panels.SearchPanel;
import org.lodder.subtools.multisubdownloader.lib.control.subtitles.Filtering;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public abstract class GuiSearchAction extends SearchAction {

  protected SearchPanel searchPanel;
  protected Filtering filtering;

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
    VideoTableModel
        model = (VideoTableModel) this.searchPanel.getResultPanel().getTable().getModel();

    if (model.getRowCount() > 0) {
      searchPanel.getResultPanel().enableButtons();
    }

    if (this.searchManager.getProgress() == 100) {
      this.searchProgressListener.completed();
      searchPanel.getInputPanel().enableSearchButton();
    }
  }
}
