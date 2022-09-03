package org.lodder.subtools.multisubdownloader.gui.extra.table;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class SubtitleTableModel extends DefaultTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 4205143311042280620L;

    private Class<?>[] columnTypes;
    final boolean[] columnEditables;
    private Map<Release, Integer> rowMap = new HashMap<>();

    public SubtitleTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        this.columnTypes = getColumnTypes(columnNames);
        this.columnEditables = getColumnEditables(columnNames);
    }

    private Class<?>[] getColumnTypes(Object[] columnNames) {
        Class<?>[] columnTypes = new Class[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            if (SubtitleTableColumnName.HEARINGIMPAIRED.getColumnName().equals(columnNames[i])) {
                columnTypes[i] = SubtitleTableColumnName.HEARINGIMPAIRED.getC();
            } else if (SubtitleTableColumnName.FILENAME.getColumnName().equals(columnNames[i])) {
                columnTypes[i] = SubtitleTableColumnName.FILENAME.getC();
            } else if (SubtitleTableColumnName.UPLOADER.getColumnName().equals(columnNames[i])) {
                columnTypes[i] = SubtitleTableColumnName.UPLOADER.getC();
            } else if (SubtitleTableColumnName.SELECT.getColumnName().equals(columnNames[i])) {
                columnTypes[i] = SubtitleTableColumnName.SELECT.getC();
            } else if (SubtitleTableColumnName.SOURCE.getColumnName().equals(columnNames[i])) {
                columnTypes[i] = SubtitleTableColumnName.SOURCE.getC();
            } else if (SubtitleTableColumnName.SCORE.getColumnName().equals(columnNames[i])) {
                columnTypes[i] = SubtitleTableColumnName.SCORE.getC();
            } else if (SubtitleTableColumnName.RELEASEGROUP.getColumnName().equals(columnNames[i])) {
                columnTypes[i] = SubtitleTableColumnName.RELEASEGROUP.getC();
            } else if (SubtitleTableColumnName.QUALITY.getColumnName().equals(columnNames[i])) {
                columnTypes[i] = SubtitleTableColumnName.QUALITY.getC();
            }
        }
        return columnTypes;
    }

    private boolean[] getColumnEditables(Object[] columnNames) {
        boolean[] columnEditables = new boolean[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            if (SubtitleTableColumnName.SELECT.getColumnName().equals(columnNames[i])) {
                columnEditables[i] = SubtitleTableColumnName.SELECT.isEditable();
            } else if (SubtitleTableColumnName.FILENAME.getColumnName().equals(columnNames[i])) {
                columnEditables[i] = SubtitleTableColumnName.FILENAME.isEditable();
            } else if (SubtitleTableColumnName.HEARINGIMPAIRED.getColumnName().equals(columnNames[i])) {
                columnEditables[i] = SubtitleTableColumnName.HEARINGIMPAIRED.isEditable();
            } else if (SubtitleTableColumnName.UPLOADER.getColumnName().equals(columnNames[i])) {
                columnEditables[i] = SubtitleTableColumnName.UPLOADER.isEditable();
            } else if (SubtitleTableColumnName.SOURCE.getColumnName().equals(columnNames[i])) {
                columnEditables[i] = SubtitleTableColumnName.SOURCE.isEditable();
            } else if (SubtitleTableColumnName.SCORE.getColumnName().equals(columnNames[i])) {
                columnEditables[i] = SubtitleTableColumnName.SCORE.isEditable();
            } else if (SubtitleTableColumnName.RELEASEGROUP.getColumnName().equals(columnNames[i])) {
                columnEditables[i] = SubtitleTableColumnName.RELEASEGROUP.isEditable();
            } else if (SubtitleTableColumnName.QUALITY.getColumnName().equals(columnNames[i])) {
                columnEditables[i] = SubtitleTableColumnName.QUALITY.isEditable();
            }
        }
        return columnEditables;
    }

    public static SubtitleTableModel getDefaultSubtitleTableModel() {
        return new SubtitleTableModel(new Object[][] {}, new String[] {
                SubtitleTableColumnName.SELECT.getColumnName(),
                SubtitleTableColumnName.SCORE.getColumnName(),
                SubtitleTableColumnName.FILENAME.getColumnName(),
                SubtitleTableColumnName.RELEASEGROUP.getColumnName(),
                SubtitleTableColumnName.QUALITY.getColumnName(),
                SubtitleTableColumnName.SOURCE.getColumnName(),
                SubtitleTableColumnName.UPLOADER.getColumnName(),
                SubtitleTableColumnName.HEARINGIMPAIRED.getColumnName() });
    }

    public void addRow(Subtitle subtitle) {
        int cCount = getColumnCount();
        Object[] row = new Object[cCount];
        String columnName;
        for (int i = 0; i < cCount; i++) {
            columnName = this.getColumnName(i);
            if (SubtitleTableColumnName.FILENAME.getColumnName().equals(columnName)) {
                row[i] = subtitle.getFilename();
            } else if (SubtitleTableColumnName.SELECT.getColumnName().equals(columnName)) {
                row[i] = false;
            } else if (SubtitleTableColumnName.HEARINGIMPAIRED.getColumnName().equals(columnName)) {
                row[i] = subtitle.isHearingImpaired();
            } else if (SubtitleTableColumnName.SOURCE.getColumnName().equals(columnName)) {
                row[i] = subtitle.getSubtitleSource();
            } else if (SubtitleTableColumnName.SCORE.getColumnName().equals(columnName)) {
                row[i] = subtitle.getScore();
            } else if (SubtitleTableColumnName.UPLOADER.getColumnName().equals(columnName)) {
                row[i] = subtitle.getUploader();
            } else if (SubtitleTableColumnName.QUALITY.getColumnName().equals(columnName)) {
                row[i] = subtitle.getQuality();
            } else if (SubtitleTableColumnName.RELEASEGROUP.getColumnName().equals(columnName)) {
                row[i] = subtitle.getReleasegroup();
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

    public void clearTable() {
        while (getRowCount() > 0) {
            removeRow(0);
        }
        rowMap.clear();
    }
}
