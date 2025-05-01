package extensions.javax.swing.JTable;

import javax.swing.*;
import javax.swing.table.*;
import java.util.function.Function;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

@Extension
@UtilityClass
public class JTableExt {

    public static @Self JTable model(@This JTable jTable, TableModel dataModel) {
        jTable.setModel(dataModel);
        return jTable;
    }

    public static @Self JTable rowSorter(@This JTable jTable, RowSorter<? extends TableModel> sorter) {
        jTable.setRowSorter(sorter);
        return jTable;
    }

    public static @Self JTable rowSorter(@This JTable jTable,
        Function<TableModel, RowSorter<? extends TableModel>> sorter) {
        jTable.setRowSorter(sorter.apply(jTable.getModel()));
        return jTable;
    }

    public static @Self JTable autoResizeMode(@This JTable jTable,
        int autoResizeMode) {
        jTable.setAutoResizeMode(autoResizeMode);
        return jTable;
    }
}
