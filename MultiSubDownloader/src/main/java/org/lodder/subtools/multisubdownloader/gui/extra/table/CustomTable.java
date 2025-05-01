package org.lodder.subtools.multisubdownloader.gui.extra.table;

import javax.swing.table.*;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.IntStream;

public class CustomTable extends ZebraJTable {

    @Serial
    private static final long serialVersionUID = -3889524906608098585L;
    private static final int MAX_WIDTH = 2147483647;
    private static final int MIN_WIDTH = 15;
    private static final int PREFERRED_WIDTH = 75;

    private final Map<SearchColumnName, int[]> columnSettings = new EnumMap<>(SearchColumnName.class);

    public int getColumnIdByName(CustomColumnName customColumnName) {
        return IntStream.range(0, this.getColumnCount())
            .filter(i -> this.getColumnName(i).equals(customColumnName.columnName)).findFirst()
            .orElse(-1);
    }

    public void setColumnVisibility(SearchColumnName searchColumnName, boolean visible) {
        if (visible) {
            showColumn(searchColumnName);
        } else {
            hideColumn(searchColumnName);
        }
    }

    public void hideColumn(SearchColumnName searchColumnName) {
        int columnId = getColumnIdByName(searchColumnName);
        if (columnId > -1) {
            TableColumn column = columnModel.getColumn(columnId);
            columnSettings.put(searchColumnName, new int[]{ column.maxWidth, column.minWidth, column.preferredWidth });
            column.maxWidth = 0;
            column.minWidth = 0;
            column.preferredWidth = 0;
        }
    }

    public void showColumn(SearchColumnName searchColumnName) {
        int columnId = getColumnIdByName(searchColumnName);
        if (columnId > -1) {
            TableColumn column = getColumnModel().getColumn(columnId);
            if (columnSettings.containsKey(searchColumnName)) {
                int[] columnSetting = columnSettings.get(searchColumnName);
                column.maxWidth = columnSetting[0];
                column.minWidth = columnSetting[1];
                column.preferredWidth = columnSetting[2];
            } else {
                column.maxWidth = MAX_WIDTH;
                column.minWidth = MIN_WIDTH;
                column.preferredWidth = PREFERRED_WIDTH;
            }
        }
    }

    public boolean isHideColumn(SearchColumnName searchColumnName) {
        int columnId = getColumnIdByName(searchColumnName);
        if (columnId > -1) {
            TableColumn column = getColumnModel().getColumn(columnId);
            return column.minWidth == 0 && column.preferredWidth == 0;
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
