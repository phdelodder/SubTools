package org.lodder.subtools.multisubdownloader.gui.dialog;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.Serial;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.table.CustomTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableColumnName;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableModel;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.JButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

@ExtensionMethod({ JButtonExtension.class, AbstractButtonExtension.class, JComponentExtension.class })
public class SelectDialog extends MultiSubDialog {

    @Serial
    private static final long serialVersionUID = -4092909537478305235L;
    private List<Integer> selectedSubtitleIdxs;
    private final List<Subtitle> subtitles;
    private final Release release;
    private CustomTable customTable;

    /**
     * Create the dialog.
     */
    public SelectDialog(JFrame frame, List<Subtitle> subtitles, Release release) {
        super(frame, Messages.getString("SelectDialog.SelectCorrectSubtitle"), true);
        this.subtitles = subtitles.stream().distinct().sorted(Comparator.comparing(Subtitle::getScore).reversed()).toList();
        this.release = release;
        initialize();
        pack();
        setDialogLocation(frame);
        setVisible(true);
    }

    private void initialize() {
        getContentPane().setLayout(new MigLayout("", "[1000px:n,grow,fill]", "[][::100px,fill][grow]"));
        JLabel lblNewLabel =
                new JLabel(Messages.getString("SelectDialog.SelectCorrectSubtitleThisRelease")
                        + release.getFileName());
        getContentPane().add(lblNewLabel, "cell 0 0");
        {
            JScrollPane scrollPane = new JScrollPane();
            getContentPane().add(scrollPane, "cell 0 1,grow");
            customTable = createCustomTable();
            scrollPane.setViewportView(customTable);
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, "cell 0 2,grow");

            new JButton(Messages.getString("App.OK"))
                    .defaultButtonFor(getRootPane())
                    .withActionListener(() -> {
                        selectedSubtitleIdxs = getSelectedIdxs();
                        setVisible(false);
                    })
                    .withActionCommand(Messages.getString("App.OK"))
                    .addTo(buttonPane);

            new JButton(Messages.getString("SelectDialog.Everything"))
                    .withActionListener(() -> {
                        selectedSubtitleIdxs = IntStream.range(0, release.getMatchingSubs().size()).boxed().toList();
                        setVisible(false);
                    })
                    .withActionCommand(Messages.getString("App.All"))
                    .addTo(buttonPane);

            new JButton(Messages.getString("App.Cancel"))
                    .withActionListener(() -> {
                        selectedSubtitleIdxs = List.of();
                        setVisible(false);
                    })
                    .withActionCommand(Messages.getString("App.Cancel"))
                    .addTo(buttonPane);
        }
    }

    private CustomTable createCustomTable() {
        CustomTable customTable = new CustomTable();
        customTable.setModel(SubtitleTableModel.getDefaultSubtitleTableModel());
        final RowSorter<TableModel> sorter = new TableRowSorter<>(customTable.getModel());
        customTable.setRowSorter(sorter);
        customTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        SubtitleTableModel subtitleTableModel = (SubtitleTableModel) customTable.getModel();

        int columnId = customTable.getColumnIdByName(SubtitleTableColumnName.SELECT);
        customTable.getColumnModel().getColumn(columnId).setResizable(false);
        customTable.getColumnModel().getColumn(columnId).setPreferredWidth(55);
        customTable.getColumnModel().getColumn(columnId).setMaxWidth(55);

        columnId = customTable.getColumnIdByName(SubtitleTableColumnName.SCORE);
        customTable.getColumnModel().getColumn(columnId).setResizable(false);
        customTable.getColumnModel().getColumn(columnId).setPreferredWidth(60);
        customTable.getColumnModel().getColumn(columnId).setMaxWidth(60);

        columnId = customTable.getColumnIdByName(SubtitleTableColumnName.FILENAME);
        customTable.getColumnModel().getColumn(columnId).setResizable(true);
        customTable.getColumnModel().getColumn(columnId).setMinWidth(500);

        for (Subtitle subtitle : subtitles) {
            subtitleTableModel.addRow(subtitle);
        }

        return customTable;
    }

    private List<Integer> getSelectedIdxs() {
        return IntStream.range(0, customTable.getModel().getRowCount())
                .filter(i -> (boolean) customTable.getModel().getValueAt(i, customTable.getColumnIdByName(SubtitleTableColumnName.SELECT)))
                .boxed().toList();
    }

    public List<Integer> getSelection() {
        return selectedSubtitleIdxs;
    }
}
