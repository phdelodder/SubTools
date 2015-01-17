package org.lodder.subtools.multisubdownloader.gui.extra.table;

public enum SearchColumnName {
  SERIE("Serie/Movie", String.class, false), FILENAME("Bestandsnaam", String.class, false), FOUND(
      "# Gevonden/Totaal", String.class, false), SELECT("Selecteer", Boolean.class, true), OBJECT(
      "Episode Object", Object.class, false), SEASON("Seizoen", String.class, false), EPISODE(
      "Aflevering", String.class, false), TYPE("Type", String.class, false), TITLE("Titel",
      String.class, false), SOURCE("Source", String.class, false);

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
