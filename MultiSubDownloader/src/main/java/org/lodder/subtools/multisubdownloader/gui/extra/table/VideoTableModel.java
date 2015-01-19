package org.lodder.subtools.multisubdownloader.gui.extra.table;

import javax.swing.table.DefaultTableModel;

import org.lodder.subtools.sublibrary.model.EpisodeFile;
import org.lodder.subtools.sublibrary.model.MovieFile;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.Release;

import java.util.ArrayList;
import java.util.List;

public class VideoTableModel extends DefaultTableModel {

  /**
     *
     */
  private static final long serialVersionUID = 4205143311042280620L;

  private Class<?>[] columnTypes;
  final boolean[] columnEditables;
  private boolean showOnlyFound = false;
  private List<Release> rowList = new ArrayList<Release>();

  public VideoTableModel(Object[][] data, Object[] columnNames) {
    super(data, columnNames);
    this.columnTypes = getColumnTypes(columnNames);
    this.columnEditables = getColumnEditables(columnNames);
  }

  private Class<?>[] getColumnTypes(Object[] columnNames) {
    Class<?>[] columnTypes = new Class[columnNames.length];
    for (int i = 0; i < columnNames.length; i++) {
      if (SearchColumnName.SERIE.getColumnName().equals(columnNames[i])) {
        columnTypes[i] = SearchColumnName.SERIE.getC();
      } else if (SearchColumnName.FILENAME.getColumnName().equals(columnNames[i])) {
        columnTypes[i] = SearchColumnName.FILENAME.getC();
      } else if (SearchColumnName.FOUND.getColumnName().equals(columnNames[i])) {
        columnTypes[i] = SearchColumnName.FOUND.getC();
      } else if (SearchColumnName.SELECT.getColumnName().equals(columnNames[i])) {
        columnTypes[i] = SearchColumnName.SELECT.getC();
      } else if (SearchColumnName.OBJECT.getColumnName().equals(columnNames[i])) {
        columnTypes[i] = SearchColumnName.OBJECT.getC();
      } else if (SearchColumnName.SEASON.getColumnName().equals(columnNames[i])) {
        columnTypes[i] = SearchColumnName.SEASON.getC();
      } else if (SearchColumnName.EPISODE.getColumnName().equals(columnNames[i])) {
        columnTypes[i] = SearchColumnName.EPISODE.getC();
      } else if (SearchColumnName.TYPE.getColumnName().equals(columnNames[i])) {
        columnTypes[i] = SearchColumnName.TYPE.getC();
      } else if (SearchColumnName.TITLE.getColumnName().equals(columnNames[i])) {
        columnTypes[i] = SearchColumnName.TITLE.getC();
      } else if (SearchColumnName.SOURCE.getColumnName().equals(columnNames[i])) {
        columnTypes[i] = SearchColumnName.SOURCE.getC();
      }
    }
    return columnTypes;
  }

  private boolean[] getColumnEditables(Object[] columnNames) {
    boolean[] columnEditables = new boolean[columnNames.length];
    for (int i = 0; i < columnNames.length; i++) {
      if (SearchColumnName.SERIE.getColumnName().equals(columnNames[i])) {
        columnEditables[i] = SearchColumnName.SERIE.isEditable();
      } else if (SearchColumnName.FILENAME.getColumnName().equals(columnNames[i])) {
        columnEditables[i] = SearchColumnName.FILENAME.isEditable();
      } else if (SearchColumnName.FOUND.getColumnName().equals(columnNames[i])) {
        columnEditables[i] = SearchColumnName.FOUND.isEditable();
      } else if (SearchColumnName.SELECT.getColumnName().equals(columnNames[i])) {
        columnEditables[i] = SearchColumnName.SELECT.isEditable();
      } else if (SearchColumnName.OBJECT.getColumnName().equals(columnNames[i])) {
        columnEditables[i] = SearchColumnName.OBJECT.isEditable();
      } else if (SearchColumnName.SEASON.getColumnName().equals(columnNames[i])) {
        columnEditables[i] = SearchColumnName.SEASON.isEditable();
      } else if (SearchColumnName.EPISODE.getColumnName().equals(columnNames[i])) {
        columnEditables[i] = SearchColumnName.EPISODE.isEditable();
      } else if (SearchColumnName.TYPE.getColumnName().equals(columnNames[i])) {
        columnEditables[i] = SearchColumnName.TYPE.isEditable();
      } else if (SearchColumnName.TITLE.getColumnName().equals(columnNames[i])) {
        columnEditables[i] = SearchColumnName.TITLE.isEditable();
      } else if (SearchColumnName.SOURCE.getColumnName().equals(columnNames[i])) {
        columnEditables[i] = SearchColumnName.SOURCE.isEditable();
      }
    }
    return columnEditables;
  }

  public static VideoTableModel getDefaultVideoTableModel() {
    return new VideoTableModel(new Object[][] {}, new String[] {
        SearchColumnName.TYPE.getColumnName(), SearchColumnName.SERIE.getColumnName(),
        SearchColumnName.FILENAME.getColumnName(), SearchColumnName.TITLE.getColumnName(),
        SearchColumnName.SEASON.getColumnName(), SearchColumnName.EPISODE.getColumnName(),
        SearchColumnName.FOUND.getColumnName(), SearchColumnName.SELECT.getColumnName(),
        SearchColumnName.OBJECT.getColumnName()});
  }

  public static VideoTableModel getDefaultSubtitleTableModel() {
    return new VideoTableModel(new Object[][] {}, new String[] {
        SearchColumnName.FILENAME.getColumnName(), SearchColumnName.SOURCE.getColumnName(),
        SearchColumnName.SELECT.getColumnName(), SearchColumnName.OBJECT.getColumnName()});
  }

  public void addRows(List<Release> l) {
    for (Release e : l) {
      addRow(e);
    }
  }

  public void addRow(Release release) {
    if (!rowList.contains(release)) {
      rowList.add(release);
    }

    if ((showOnlyFound && release.getMatchingSubs().size() > 0) || (!showOnlyFound)) {
      int cCount = getColumnCount();
      Object[] row = new Object[cCount];
      String columnName;
      for (int i = 0; i < cCount; i++) {
        columnName = this.getColumnName(i);
        if (SearchColumnName.SERIE.getColumnName().equals(columnName)) {
          if (release instanceof EpisodeFile) {
            row[i] = ((EpisodeFile) release).getShow();
          } else if (release instanceof MovieFile) {
            row[i] = ((MovieFile) release).getTitle();
          }
        } else if (SearchColumnName.FILENAME.getColumnName().equals(columnName)) {
          row[i] = release.getFilename();
        } else if (SearchColumnName.FOUND.getColumnName().equals(columnName)) {
          row[i] = release.getMatchingSubs().size();
        } else if (SearchColumnName.SELECT.getColumnName().equals(columnName)) {
          row[i] = false;
        } else if (SearchColumnName.OBJECT.getColumnName().equals(columnName)) {
          row[i] = release;
        } else if (SearchColumnName.SEASON.getColumnName().equals(columnName)) {
          if (release instanceof EpisodeFile) row[i] = ((EpisodeFile) release).getSeason();
        } else if (SearchColumnName.EPISODE.getColumnName().equals(columnName)) {
          if (release instanceof EpisodeFile)
            row[i] = ((EpisodeFile) release).getEpisodeNumbers().get(0);
        } else if (SearchColumnName.TYPE.getColumnName().equals(columnName)) {
          row[i] = release.getVideoType();
        } else if (SearchColumnName.TITLE.getColumnName().equals(columnName)) {
          if (release instanceof EpisodeFile) row[i] = ((EpisodeFile) release).getTitle();
        }
      }
      this.addRow(row);
    }
  }

  public void addRow(Subtitle subtitle) {
    int cCount = getColumnCount();
    Object[] row = new Object[cCount];
    String columnName;
    for (int i = 0; i < cCount; i++) {
      columnName = this.getColumnName(i);
      if (SearchColumnName.FILENAME.getColumnName().equals(columnName)) {
        row[i] = subtitle.getFilename();
      } else if (SearchColumnName.SELECT.getColumnName().equals(columnName)) {
        row[i] = false;
      } else if (SearchColumnName.OBJECT.getColumnName().equals(columnName)) {
        row[i] = subtitle;
      } else if (SearchColumnName.SOURCE.getColumnName().equals(columnName)) {
        row[i] = subtitle.getSubtitleSource();
      }
    }
    this.addRow(row);
  }

  public Class<?> getColumnClass(int columnIndex) {
    return columnTypes[columnIndex];
  }

  public boolean isCellEditable(int row, int column) {
    return columnEditables[column];
  }

  public Integer getSelectedCount(int column) {
    Integer k = 0;
    for (int i = 0; i < getRowCount(); i++) {
      if ((Boolean) getValueAt(i, column)) {
        k++;
      }
    }
    return k;
  }

  private void updateTable() {
    List<Release> newRowList = new ArrayList<Release>();
    newRowList.addAll(rowList);
    clearTable();
    addRows(newRowList);
  }

  public void clearTable() {
    while (getRowCount() > 0) {
      removeRow(0);
    }
    rowList.clear();
  }

  public void setShowOnlyFound(boolean showOnlyFound) {
    this.showOnlyFound = showOnlyFound;
    updateTable();
  }

  public boolean isShowOnlyFound() {
    return this.showOnlyFound;
  }
}
