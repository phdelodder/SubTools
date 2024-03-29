package org.lodder.subtools.multisubdownloader.gui.extra.table;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import java.awt.event.MouseEvent;

public class CustomTable extends ZebraJTable {

    @Serial
    private static final long serialVersionUID = -3889524906608098585L;
    private final Map<SearchColumnName, int[]> columnSettings = new HashMap<>();
    private static final int MAX_WIDTH = 2147483647;
    private static final int MIN_WIDTH = 15;
    private static final int PREFERRED_WIDTH = 75;

    public int getColumnIdByName(CustomColumnName customColumnName) {
        return IntStream.range(0, this.getColumnCount())
                .filter(i -> this.getColumnName(i).equals(customColumnName.getColumnName())).findFirst()
                .orElse(-1);
    }

    public void setColumnVisibility(SearchColumnName searchColumnName, boolean visible) {
        if (visible) {
            unhideColumn(searchColumnName);
        } else {
            hideColumn(searchColumnName);
        }
    }

    public void hideColumn(SearchColumnName searchColumnName) {
        int columnId = getColumnIdByName(searchColumnName);
        if (columnId > -1) {
            columnSettings.put(searchColumnName, new int[] {
                    getColumnModel().getColumn(columnId).getMaxWidth(),
                    getColumnModel().getColumn(columnId).getMinWidth(),
                    getColumnModel().getColumn(columnId).getPreferredWidth() });
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
            return getColumnModel().getColumn(columnId).getMinWidth() == 0
                    && getColumnModel().getColumn(columnId).getPreferredWidth() == 0;
        }
        return true;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        int row = rowAtPoint(e.getPoint());
        int column = columnAtPoint(e.getPoint());

        if (row > -1 && column > -1 && getColumnCount() >= column && getRowCount() >= row) {
            Object value = getValueAt(row, column);
            return value == null ? null : value.toString();
        }
        return null;
    }

}
