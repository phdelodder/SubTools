package org.lodder.subtools.multisubdownloader.gui.dialog;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.Serial;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.CustomTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableColumnName;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableModel;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class SelectDialog extends MultiSubDialog {

    @Serial private static final long serialVersionUID = -4092909537478305235L;

    private final List<Subtitle> subtitles;
    private final CustomTable customTable;

    private List<Integer> selectedSubtitleIdxs;

    /**
     * Create the dialog.
     */
    public SelectDialog(@Nullable JFrame frame = null, List<Subtitle> subtitles, Release release) {
        super(frame, getText("SelectDialog.SelectCorrectSubtitle"), true);
        this.subtitles =
            subtitles.stream().distinct().sorted(Comparator.comparing(Subtitle::getScore).reversed()).toList();
        contentPane
            .layout(new MigLayout("", "[1000px:n,grow,fill]", "[][::100px,fill][grow]"))
            .addComponent("cell 0 0",
                new JLabel(getText("SelectDialog.SelectCorrectSubtitleThisRelease") + release.fileName))
            .addComponent("cell 0 1,grow", new JScrollPane().viewportView(customTable = createCustomTable()))
            .addComponent("cell 0 2,grow", new JPanel()
                .layout(new FlowLayout(FlowLayout.RIGHT))
                .addComponent(new JButton(getText("App.OK"))
                    .defaultButtonFor(getRootPane())
                    .actionListener(() -> {
                        selectedSubtitleIdxs = getSelectedIdxs();
                        setVisible(false);
                    })
                    .actionCommand(getText("App.OK")))
                .addComponent(new JButton(getText("SelectDialog.Everything"))
                    .actionListener(() -> {
                        selectedSubtitleIdxs = IntStream.range(0, release.getMatchingSubs().size()).boxed().toList();
                        setVisible(false);
                    })
                    .actionCommand(getText("App.All")))
                .addComponent(new JButton(getText("App.Cancel"))
                    .actionListener(() -> {
                        selectedSubtitleIdxs = List.of();
                        setVisible(false);
                    })
                    .actionCommand(getText("App.Cancel"))));
        pack();
        setDialogLocation(frame);
        setVisible(true);
    }

    private CustomTable createCustomTable() {
        SubtitleTableModel subtitleTableModel = SubtitleTableModel.createDefaultSubtitleTableModel();
        CustomTable table = new CustomTable()
            .model(subtitleTableModel)
            .rowSorter(TableRowSorter::new)
            .autoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableColumnModel columnModel = table.getColumnModel();

        TableColumn column = columnModel.getColumn(table.getColumnIdByName(SubtitleTableColumnName.SELECT));
        column.resizable = false;
        column.preferredWidth = 55;
        column.maxWidth = 55;

        column = columnModel.getColumn(table.getColumnIdByName(SubtitleTableColumnName.SCORE));
        column.resizable = false;
        column.preferredWidth = 60;
        column.maxWidth = 60;

        column = columnModel.getColumn(table.getColumnIdByName(SubtitleTableColumnName.FILENAME));
        column.resizable = true;
        column.minWidth = 500;

        subtitles.forEach(subtitleTableModel::addRow);
        return table;
    }

    private List<Integer> getSelectedIdxs() {
        return IntStream.range(0, customTable.getModel().getRowCount())
            .filter(i -> (boolean) customTable.getModel()
                .getValueAt(i, customTable.getColumnIdByName(SubtitleTableColumnName.SELECT)))
            .boxed()
            .toList();
    }

    public List<Integer> getSelection() {
        return selectedSubtitleIdxs;
    }
}
