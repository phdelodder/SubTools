package org.lodder.subtools.multisubdownloader.gui.extra.table;

import org.lodder.subtools.multisubdownloader.Messages;

public enum SearchColumnName implements CustomColumnName{
  RELEASE(Messages.getString("SearchColumnName.Release"), String.class, false), FILENAME(Messages
      .getString("SearchColumnName.Filename"), String.class, false), FOUND(Messages
      .getString("SearchColumnName.NumberFound"), Integer.class, false), SELECT(Messages
      .getString("SearchColumnName.Select"), Boolean.class, true), OBJECT(Messages
      .getString("SearchColumnName.EpisodeObject"), Object.class, false), SEASON(Messages
      .getString("SearchColumnName.Season"), String.class, false), EPISODE(Messages
      .getString("SearchColumnName.Episode"), String.class, false), TYPE(Messages
      .getString("SearchColumnName.Type"), String.class, false), TITLE(Messages
      .getString("SearchColumnName.Title"), String.class, false), SOURCE(Messages
      .getString("SearchColumnName.Source"), String.class, false), SCORE(Messages
      .getString("SearchColumnName.Score"), Integer.class, false);
  
  private final String columnName;
  private final Class<?> c;
  private final boolean editable;

  private SearchColumnName(String columnName, Class<?> c, boolean editable) {
    this.columnName = columnName;
    this.c = c;
    this.editable = editable;
  }

  public String getColumnName() {
    return columnName;
  }

  public boolean isEditable() {
    return editable;
  }

  public Class<?> getC() {
    return c;
  }
}
