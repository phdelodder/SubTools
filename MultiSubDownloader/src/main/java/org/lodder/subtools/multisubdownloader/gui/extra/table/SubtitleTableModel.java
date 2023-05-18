package org.lodder.subtools.multisubdownloader.gui.extra.table;

import static org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableColumnName.*;

import java.io.Serial;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.table.DefaultTableModel;

import org.lodder.subtools.sublibrary.model.Subtitle;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ Arrays.class })
public class SubtitleTableModel extends DefaultTableModel {

    @Serial
    private static final long serialVersionUID = 4205143311042280620L;

    private final static SubtitleTableColumnName[] COLUMNS =
            Stream.of(SELECT, SCORE, FILENAME, RELEASEGROUP, QUALITY, SOURCE, UPLOADER, HEARINGIMPAIRED).toArray(SubtitleTableColumnName[]::new);

    public SubtitleTableModel(Object[][] data, String[] columnNames) {
        super(data, columnNames);
    }

    public static SubtitleTableModel getDefaultSubtitleTableModel() {
        String[] columnNames = Arrays.stream(COLUMNS).map(SubtitleTableColumnName::getColumnName).toArray(String[]::new);
        return new SubtitleTableModel(new Object[][] {}, columnNames);
    }

    public void addRow(Subtitle subtitle) {
        Object[] row = IntStream.range(0, getColumnCount()).mapToObj(this::getColumnName).map(SubtitleTableColumnName::forColumnName)
                .map(stcn -> stcn.getValue(subtitle)).toArray(Object[]::new);
        this.addRow(row);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMNS[columnIndex].getC();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return COLUMNS[column].isEditable();
    }
}
