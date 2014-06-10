package org.lodder.subtools.multisubdownloader.gui.extra.table;

import java.util.HashMap;

public class VideoTable extends ZebraJTable {

  /**
	 * 
	 */
  private static final long serialVersionUID = -3889524906608098585L;
  private HashMap<SearchColumnName, int[]> columnSettings = new HashMap<SearchColumnName, int[]>();
  private static final int MAX_WIDTH = 2147483647;
  private static final int MIN_WIDTH = 15;
  private static final int PREFERRED_WIDTH = 75;

  public int getColumnIdByName(SearchColumnName searchColumnName) {
    for (int i = 0; i < this.getColumnCount(); i++) {
      if (this.getColumnName(i).equals(searchColumnName.getColumnName())) {
        return i;
      }
    }
    return -1;
  }

  public void hideColumn(SearchColumnName searchColumnName) {
    int columnId = getColumnIdByName(searchColumnName);
    if (columnId > -1) {
      if (columnId == -1)
        columnSettings.put(searchColumnName, new int[] {
            getColumnModel().getColumn(columnId).getMaxWidth(),
            getColumnModel().getColumn(columnId).getMinWidth(),
            getColumnModel().getColumn(columnId).getPreferredWidth()});
      getColumnModel().getColumn(columnId).setMaxWidth(0);
      getColumnModel().getColumn(columnId).setMinWidth(0);
      getColumnModel().getColumn(columnId).setPreferredWidth(0);
    }
  }

  public void unhideColumn(SearchColumnName searchColumnName) {
    int columnId = getColumnIdByName(searchColumnName);
    if (columnId > -1) {
      if (columnSettings.containsKey(searchColumnName)) {
        getColumnModel().getColumn(columnId).setMaxWidth(columnSettings.get(searchColumnName)[0]);
        getColumnModel().getColumn(columnId).setMinWidth(columnSettings.get(searchColumnName)[1]);
        getColumnModel().getColumn(columnId).setPreferredWidth(
            columnSettings.get(searchColumnName)[2]);
      } else {
        getColumnModel().getColumn(columnId).setMaxWidth(MAX_WIDTH);
        getColumnModel().getColumn(columnId).setMinWidth(MIN_WIDTH);
        getColumnModel().getColumn(columnId).setPreferredWidth(PREFERRED_WIDTH);
      }
    }
  }

  public boolean isHideColumn(SearchColumnName searchColumnName) {
    int columnId = getColumnIdByName(searchColumnName);
    if (columnId > -1) {
      // TableColumn tc = getColumnModel().getColumn(columnId);
      if (getColumnModel().getColumn(columnId).getMinWidth() == 0
          && getColumnModel().getColumn(columnId).getPreferredWidth() == 0) {
        return true;
      } else {
        return false;
      }
    }
    return true;
  }

}
