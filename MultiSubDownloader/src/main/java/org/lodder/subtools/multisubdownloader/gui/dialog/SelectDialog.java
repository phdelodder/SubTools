package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.awt.FlowLayout;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.table.CustomTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableColumnName;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SubtitleTableModel;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;

public class SelectDialog extends MultiSubDialog {

    public enum SelectionType {
        OK(1), ALL(999999), CANCEL(-1);

        private final int code;

        private SelectionType(int c) {
            code = c;
        }

        public int getSelectionCode() {
            return code;
        }
    }

    private static final long serialVersionUID = -4092909537478305235L;
    private SelectionType answer = SelectionType.CANCEL;
    private List<Subtitle> subtitles;
    private Release release;
    private CustomTable customTable;
    private JFrame frame;

    /**
     * Create the dialog.
     */
    public SelectDialog(JFrame frame, List<Subtitle> subtitles, Release release) {
        super(frame, Messages.getString("SelectDialog.SelectCorrectSubtitle"), true);
        this.subtitles = subtitles;
        this.release = release;
        this.frame = frame;
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
            {
                JButton okButton = new JButton(Messages.getString("SelectDialog.OK"));
                okButton.addActionListener(arg0 -> {
                    if (testSelection()) {
                        answer = SelectionType.OK;
                        setVisible(false);
                    } else {
                        String message = Messages.getString("SelectDialog.MultipleSubtitlesSelected");
                        JOptionPane.showConfirmDialog(frame, message, Messages.getString("SelectDialog.Name"), JOptionPane.CLOSED_OPTION,
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton allButton = new JButton(Messages.getString("SelectDialog.Everything"));
                allButton.addActionListener(arg0 -> {
                    answer = SelectionType.ALL;
                    setVisible(false);
                });
                allButton.setActionCommand("Alles");
                if (subtitles.size() == 1) {
                    allButton.setEnabled(false);
                }
                buttonPane.add(allButton);
            }
            {
                JButton cancelButton = new JButton(Messages.getString("SelectDialog.Cancel"));
                cancelButton.addActionListener(arg0 -> {
                    answer = SelectionType.CANCEL;
                    setVisible(false);
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
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

    private boolean testSelection() {
        int count = 0;
        SubtitleTableModel subtitleTableModel = (SubtitleTableModel) customTable.getModel();
        for (int i = 0; i < subtitleTableModel.getRowCount(); i++) {
            if ((Boolean) subtitleTableModel.getValueAt(i,
                    customTable.getColumnIdByName(SubtitleTableColumnName.SELECT))) {
                count++;
            }
        }
        return !(count > 1);
    }

    public int getSelection() {
        int selectedRow = -1;
        if (answer == SelectionType.OK) {
            SubtitleTableModel subtitleTableModel = (SubtitleTableModel) customTable.getModel();
            for (int i = 0; i < subtitleTableModel.getRowCount(); i++) {
                if ((Boolean) subtitleTableModel.getValueAt(i,
                        customTable.getColumnIdByName(SubtitleTableColumnName.SELECT))) {
                    selectedRow = i;
                }
            }
        }
        return selectedRow;
    }

    public SelectionType getAnswer() {
        return answer;
    }

}
