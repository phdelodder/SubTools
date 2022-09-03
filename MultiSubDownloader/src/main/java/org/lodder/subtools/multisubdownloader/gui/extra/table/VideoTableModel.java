package org.lodder.subtools.multisubdownloader.gui.extra.table;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import org.lodder.subtools.multisubdownloader.lib.SubtitleSelection;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.model.MovieRelease;
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
    private Map<Release, Integer> rowMap = new HashMap<>();
    private SubtitleSelection subtitleSelection;

    public VideoTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        this.columnTypes = getColumnTypes(columnNames);
        this.columnEditables = getColumnEditables(columnNames);
    }

    public void setSubtitleSelection(SubtitleSelection subtitleSeletion) {
        this.subtitleSelection = subtitleSeletion;
    }

    private Class<?>[] getColumnTypes(Object[] columnNames) {
        Class<?>[] columnTypes = new Class[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            if (SearchColumnName.RELEASE.getColumnName().equals(columnNames[i])) {
                columnTypes[i] = SearchColumnName.RELEASE.getC();
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
            } else if (SearchColumnName.SCORE.getColumnName().equals(columnNames[i])) {
                columnTypes[i] = SearchColumnName.SCORE.getC();
            }
        }
        return columnTypes;
    }

    private boolean[] getColumnEditables(Object[] columnNames) {
        boolean[] columnEditables = new boolean[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            if (SearchColumnName.RELEASE.getColumnName().equals(columnNames[i])) {
                columnEditables[i] = SearchColumnName.RELEASE.isEditable();
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
            } else if (SearchColumnName.SCORE.getColumnName().equals(columnNames[i])) {
                columnEditables[i] = SearchColumnName.SCORE.isEditable();
            }
        }
        return columnEditables;
    }

    public static VideoTableModel getDefaultVideoTableModel() {
        return new VideoTableModel(new Object[][] {}, new String[] {
                SearchColumnName.TYPE.getColumnName(), SearchColumnName.RELEASE.getColumnName(),
                SearchColumnName.FILENAME.getColumnName(), SearchColumnName.TITLE.getColumnName(),
                SearchColumnName.SEASON.getColumnName(), SearchColumnName.EPISODE.getColumnName(),
                SearchColumnName.FOUND.getColumnName(), SearchColumnName.SELECT.getColumnName(),
                SearchColumnName.OBJECT.getColumnName() });
    }

    public static VideoTableModel getDefaultSubtitleTableModel() {
        return new VideoTableModel(new Object[][] {}, new String[] {
                SearchColumnName.FILENAME.getColumnName(), SearchColumnName.SOURCE.getColumnName(),
                SearchColumnName.SCORE.getColumnName(), SearchColumnName.SELECT.getColumnName(),
                SearchColumnName.OBJECT.getColumnName() });
    }

    public void addRows(List<Release> l) {
        for (Release e : l) {
            addRow(e);
        }
    }

    public void addRow(Release release) {
        /* If we try to add an existing release, we just have to update that row */
        if (rowMap.containsKey(release)) {
            updateRow(release);
            return;
        }

        if (showOnlyFound && release.getMatchingSubs().size() > 0 || !showOnlyFound) {
            rowMap.put(release, this.getRowCount());

            Object[] row = createRow(release);
            this.addRow(row);
        }
    }

    private Object[] createRow(Release release) {
        int cCount = getColumnCount();
        Object[] row = new Object[cCount];
        String columnName;
        for (int i = 0; i < cCount; i++) {
            columnName = this.getColumnName(i);
            if (SearchColumnName.RELEASE.getColumnName().equals(columnName)) {
                if (release instanceof TvRelease) {
                    row[i] = ((TvRelease) release).getShow();
                } else if (release instanceof MovieRelease) {
                    row[i] = ((MovieRelease) release).getTitle();
                }
            } else if (SearchColumnName.FILENAME.getColumnName().equals(columnName)) {
                row[i] = release.getFilename();
            } else if (SearchColumnName.FOUND.getColumnName().equals(columnName)) {
                int selectionSize = release.getMatchingSubs().size();
                if (subtitleSelection != null) {
                    selectionSize = subtitleSelection.getAutomaticSelection(release.getMatchingSubs()).size();
                }
                if (selectionSize == release.getMatchingSubs().size()) {
                    row[i] = release.getMatchingSubs().size();
                } else {
                    row[i] = selectionSize;
                }
            } else if (SearchColumnName.SELECT.getColumnName().equals(columnName)) {
                row[i] = false;
            } else if (SearchColumnName.OBJECT.getColumnName().equals(columnName)) {
                row[i] = release;
            } else if (SearchColumnName.SEASON.getColumnName().equals(columnName)) {
                if (release instanceof TvRelease) {
                    row[i] = ((TvRelease) release).getSeason();
                }
            } else if (SearchColumnName.EPISODE.getColumnName().equals(columnName)) {
                if (release instanceof TvRelease) {
                    row[i] = ((TvRelease) release).getEpisodeNumbers().get(0);
                }
            } else if (SearchColumnName.TYPE.getColumnName().equals(columnName)) {
                row[i] = release.getVideoType();
            } else if (SearchColumnName.TITLE.getColumnName().equals(columnName) && (release instanceof TvRelease)) {
                row[i] = ((TvRelease) release).getTitle();
            }
        }
        return row;
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
            } else if (SearchColumnName.SCORE.getColumnName().equals(columnName)) {
                row[i] = subtitle.getScore();
            }
        }
        this.addRow(row);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return columnEditables[column];
    }

    public Integer getSelectedCount(int column) {
        int k = 0;
        for (int i = 0; i < getRowCount(); i++) {
            if ((Boolean) getValueAt(i, column)) {
                k++;
            }
        }
        return k;
    }

    private void updateTable() {
        List<Release> newRowList = new ArrayList<>(this.rowMap.keySet());
        clearTable();
        addRows(newRowList);
    }

    private void updateRow(Release release) {
        int rowNr = this.rowMap.get(release);
        Object[] row = this.createRow(release);

        for (int columnNr = 0; columnNr < row.length; columnNr++) {
            Object rowData = row[columnNr];
            if (rowData == null) {
                continue;
            }

            this.setValueAt(rowData, rowNr, columnNr);
        }
    }

    public void clearTable() {
        while (getRowCount() > 0) {
            removeRow(0);
        }
        rowMap.clear();
    }

    public void setShowOnlyFound(boolean showOnlyFound) {
        this.showOnlyFound = showOnlyFound;
        updateTable();
    }

    public boolean isShowOnlyFound() {
        return this.showOnlyFound;
    }
}
