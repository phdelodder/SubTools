package org.lodder.subtools.multisubdownloader.gui.dialog.progress.search;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.table.DefaultTableModel;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class SearchProgressTableModel extends DefaultTableModel {

    @Serial
    private static final long serialVersionUID = -8366722569174216456L;
    private final Map<String, Integer> rowMap = new HashMap<>();

    public SearchProgressTableModel() {
        super();
        this.setColumnCount(3);
        this.setColumnIdentifiers(new String[]{
            getText("SearchProgressTableModel.Source"),
            getText("SearchProgressTableModel.Queue"),
            getText("SearchProgressTableModel.Release") });
    }

    public void update(String source, int queue, String release) {
        if (!this.rowMap.containsKey(source)) {
            /* Source has no row, so create row */
            createRow(source, queue, release);
            return;
        }

        /* SubtitleSource has a row, update row */
        int rowNr = this.rowMap.get(source);
        updateRow(rowNr, queue, release);
    }

    private void updateRow(int rowNr, int queue, String release) {
        this.setValueAt(queue, rowNr, 1); // Queue
        this.setValueAt(release, rowNr, 2); // Release
    }

    private void createRow(String source, int queue, String release) {
        this.rowMap.put(source, this.getRowCount());

        Object[] row = { source, queue, release };
        this.addRow(row);
    }

    public void clear() {
        rowMap.clear();
        dataVector.removeAllElements();
    }
}
