package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.settings.model.TvdbMapping;
import org.lodder.subtools.sublibrary.settings.model.TvdbMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.miginfocom.swing.MigLayout;

public class MappingEpisodeNameDialog extends MultiSubDialog {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MappingEpisodeNameDialog.class);
    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private final Manager manager;
    private final MappingTableModel mappingTableModel;

    /**
     * Create the dialog.
     */
    public MappingEpisodeNameDialog(JFrame frame, final SettingsControl prefCtrl, Manager manager) {
        super(frame, Messages.getString("MappingEpisodeNameDialog.Title"), true);
        this.manager = manager;
        this.mappingTableModel = new MappingTableModel(manager);
        initialize();
        repaint();
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    private static class Cell {
        private final String initialValue;
        private String value;

        public Cell() {
            this.initialValue = null;
        }

        public Cell(String value) {
            this.initialValue = value;
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public boolean isChanged() {
            return initialValue == null || !initialValue.equals(value);
        }
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class Row {

        private final TvdbMapping tvdbMapping;
        private final Cell nameCell;
        private final Cell idCell;

        public Row(String name, int id) {
            this.tvdbMapping = null;
            this.nameCell = new Cell(name);
            this.idCell = new Cell(String.valueOf(id));
        }

        public Row(TvdbMapping tvdbMapping) {
            this.tvdbMapping = tvdbMapping;
            this.nameCell = new Cell(tvdbMapping.getName());
            this.idCell = new Cell(String.valueOf(tvdbMapping.getId()));
        }

        public boolean isChanged() {
            return nameCell.isChanged() || idCell.isChanged();
        }
    }

    private static class MappingTableModel extends DefaultTableModel {
        @Getter
        private final List<String> removedSerieName = new ArrayList<>();

        public MappingTableModel(Manager manager) {
            super(new String[] { Messages.getString("MappingEpisodeNameDialog.SceneShowName"),
                    Messages.getString("MappingEpisodeNameDialog.TvdbId") }, 0);
            TvdbMappings.getPersistedTvdbMappings(manager).stream().forEach(tvdbMapping -> {
                Vector<Object> vector = new Vector<>(2);
                vector.add(new Cell(tvdbMapping.getName()));
                vector.add(new Cell(String.valueOf(tvdbMapping.getId())));
                addRow(vector);
            });
        }

        public List<Vector<Cell>> getChangedRows() {
            return super.getDataVector().stream().map(vector -> (Vector<Cell>) vector)
                    .filter(vector -> vector.get(0).isChanged() || vector.get(1).isChanged()).collect(Collectors.toList());
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            Cell cell = ((Cell) getValueAt(row, column)).setValue((String) value);
            super.setValueAt(cell, row, column);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        @Override
        public void removeRow(int row) {
            if (row < 0) {
                return;
            }
            Vector<Cell> vector = super.getDataVector().get(row);
            String initialName = vector.get(0).getInitialValue();
            if (StringUtils.isNotBlank(initialName)) {
                removedSerieName.add(initialName);
            }
            super.removeRow(row);
        }

        public void addRow(String scene, int tvdbId) {
            Vector<Object> vector = new Vector<>(2);
            vector.add(new Cell().setValue(scene));
            vector.add(new Cell().setValue(String.valueOf(tvdbId)));
            super.addRow(vector);
        }
    }

    private void initialize() {
        setResizable(false);
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 40, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            JPanel pnlButtons = new JPanel();
            GridBagConstraints gbc_pnlButtons = new GridBagConstraints();
            gbc_pnlButtons.insets = new Insets(0, 0, 5, 0);
            gbc_pnlButtons.fill = GridBagConstraints.BOTH;
            gbc_pnlButtons.gridx = 0;
            gbc_pnlButtons.gridy = 0;
            contentPanel.add(pnlButtons, gbc_pnlButtons);
            {
                JButton btnAdd = new JButton(Messages.getString("MappingEpisodeNameDialog.AddRow"));
                btnAdd.addActionListener(arg0 -> {
                    String scene =
                            JOptionPane.showInputDialog(Messages
                                    .getString("MappingEpisodeNameDialog.EnterSceneShowName"));
                    if (!"".equals(scene)) {
                        boolean isInvalid = true;
                        int tvdbId = 0;
                        while (isInvalid) {
                            try {
                                tvdbId = Integer.parseInt(JOptionPane.showInputDialog(Messages.getString("MappingEpisodeNameDialog.EnterTvdbId")));
                                isInvalid = false;
                            } catch (NumberFormatException e) {
                                // retry
                            }
                        }
                        MappingTableModel model = (MappingTableModel) table.getModel();
                        model.addRow(scene, tvdbId);
                    }
                });
                pnlButtons.add(btnAdd);
            }
            {
                JButton btnDeleteSelectedRow =
                        new JButton(Messages.getString("MappingEpisodeNameDialog.DeleteRow"));
                btnDeleteSelectedRow.addActionListener(arg0 -> {
                    int row = table.getSelectedRow();
                    MappingTableModel model = (MappingTableModel) table.getModel();
                    model.removeRow(row);
                });
                pnlButtons.add(btnDeleteSelectedRow);
            }
        }
        {
            JScrollPane scrollPane = new JScrollPane();
            GridBagConstraints gbc_scrollPane = new GridBagConstraints();
            gbc_scrollPane.fill = GridBagConstraints.BOTH;
            gbc_scrollPane.gridx = 0;
            gbc_scrollPane.gridy = 1;
            contentPanel.add(scrollPane, gbc_scrollPane);
            {
                table = new JTable();

                table.setModel(mappingTableModel);
                RowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
                table.setRowSorter(sorter);
                scrollPane.setViewportView(table);
            }
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton(Messages.getString("MappingEpisodeNameDialog.OK"));
                okButton.addActionListener(arg0 -> {
                    setVisible(false);
                    storeMappingTable();
                });
                buttonPane.setLayout(new MigLayout("", "[117px][grow,fill][62px,trailing]",
                        "[][25px,grow,fill]"));
                okButton.setActionCommand(Messages.getString("MappingEpisodeNameDialog.OK"));
                buttonPane.add(okButton, "cell 2 1,alignx right,aligny top");
                getRootPane().setDefaultButton(okButton);
            }
        }
    }

    private void storeMappingTable() {
        List<Vector<Cell>> changedRows = mappingTableModel.getChangedRows();
        changedRows.stream().map(vector -> vector.get(0).getInitialValue()).forEach(this::removeRowFromCache);
        changedRows.forEach(vector -> putRowInCache(vector.get(0).getValue(), vector.get(1).getValue()));
        mappingTableModel.getRemovedSerieName().forEach(this::removeRowFromCache);
    }

    private void removeRowFromCache(String serieName) {
        if (StringUtils.isNotBlank(serieName)) {
            System.out.println("removeTvdbMapping " + serieName);
            TvdbMappings.removeTvdbMapping(manager, serieName);
        }
    }

    private void putRowInCache(String serieName, String id) {
        try {
            TvdbMappings.persistTvdbMapping(manager, serieName, Integer.parseInt(id));
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid tvdb id: " + e.getMessage(), e);
        }
    }
}
