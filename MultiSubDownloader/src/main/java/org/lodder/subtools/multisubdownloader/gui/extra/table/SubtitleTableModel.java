package org.lodder.subtools.multisubdownloader.gui.extra.table;

import static org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableColumnName.*;

import javax.swing.table.*;
import java.io.Serial;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.lodder.subtools.sublibrary.model.Subtitle;

public class SubtitleTableModel extends DefaultTableModel {

    @Serial
    private static final long serialVersionUID = 4205143311042280620L;

    private static final SubtitleTableColumnName[] COLUMNS =
        Stream.of(SELECT, SCORE, FILENAME, RELEASEGROUP, QUALITY, SOURCE, UPLOADER, HEARINGIMPAIRED)
            .toArray(SubtitleTableColumnName[]::new);

    public SubtitleTableModel(Object[][] data, String[] columnNames) {
        super(data, columnNames);
    }

    public static SubtitleTableModel createDefaultSubtitleTableModel() {
        String[] columnNames = COLUMNS.stream().map(SubtitleTableColumnName::getColumnName).toArray(String[]::new);
        return new SubtitleTableModel(new Object[][]{}, columnNames);
    }

    public void addRow(Subtitle subtitle) {
        Object[] row = IntStream.range(0, getColumnCount())
            .mapToObj(this::getColumnName)
            .map(SubtitleTableColumnName::forColumnName)
            .map(stcn -> stcn.getValue(subtitle))
            .toArray(Object[]::new);
        this.addRow(row);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMNS[columnIndex].clazz;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return COLUMNS[column].editable;
    }
}
