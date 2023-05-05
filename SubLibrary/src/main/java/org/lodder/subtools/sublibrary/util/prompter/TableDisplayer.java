package org.lodder.subtools.sublibrary.util.prompter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import dnl.utils.text.table.TextTable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TableDisplayer<T> {

    private final List<ColumnDisplayer<T>> columnDisplayers;

    public void display(T... tableElements) {
        String[] columnNames = columnDisplayers.stream().map(ColumnDisplayer::getColumnName).toArray(String[]::new);
        Object[][] dataTable = Arrays.stream(tableElements)
                .map(tableElement -> columnDisplayers.stream().map(columnDisplayer -> columnDisplayer.getToStringMapper().apply(tableElement))
                        .toArray())
                .toArray(Object[][]::new);

        TextTable tt = new TextTable(columnNames, dataTable);
        // this adds the numbering on the left
        tt.setAddRowNumbering(true);
        tt.printTable();
    }

    public void display(List<T> tableElements) {

        String[] columnNames = columnDisplayers.stream().map(ColumnDisplayer::getColumnName).toArray(String[]::new);
        Object[][] dataTable = tableElements.stream()
                .map(tableElement -> columnDisplayers.stream().map(columnDisplayer -> columnDisplayer.getToStringMapper().apply(tableElement))
                        .toArray())
                .toArray(Object[][]::new);

        TextTable tt = new TextTable(columnNames, dataTable);
        // this adds the numbering on the left
        tt.setAddRowNumbering(true);
        tt.printTable();
    }
}
