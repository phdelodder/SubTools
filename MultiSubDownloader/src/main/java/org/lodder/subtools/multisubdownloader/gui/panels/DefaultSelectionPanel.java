package org.lodder.subtools.multisubdownloader.gui.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.ArrowButton;
import org.lodder.subtools.multisubdownloader.gui.extra.MyComboBoxEditor;
import org.lodder.subtools.multisubdownloader.gui.extra.MyComboBoxRenderer;
import org.lodder.subtools.sublibrary.control.VideoPatterns;

import net.miginfocom.swing.MigLayout;

public class DefaultSelectionPanel extends JPanel {

    private static final long serialVersionUID = 2925926997236198235L;
    private JTable defaultSelectionTable;
    private final VideoPatterns videoPatterns = new VideoPatterns();

    public DefaultSelectionPanel() {
        initialize_ui();
        initializeTable();
    }

    private void initialize_ui() {
        this.setLayout(new MigLayout("", "[grow][]", "[][][][][][grow]"));
        JScrollPane scrollPane = new JScrollPane();
        defaultSelectionTable = new JTable();
        scrollPane.setViewportView(defaultSelectionTable);
        this.add(scrollPane, "cell 0 0 1 6,grow");
        JButton btnAdd = new JButton(Messages.getString("DefaultSelectionPanel.Add")); //$NON-NLS-1$
        btnAdd.addActionListener(arg0 -> addRuleRow(""));
        this.add(btnAdd, "cell 1 1");
        JButton btnDelete = new JButton(Messages.getString("DefaultSelectionPanel.Delete")); //$NON-NLS-1$
        btnDelete.addActionListener(arg0 -> removeRuleRow());
        this.add(btnDelete, "cell 1 2");
        JButton btnUp = new ArrowButton(SwingConstants.NORTH, 1, 10);
        btnUp.addActionListener(arg0 -> moveRuleRowUp());
        this.add(btnUp, "cell 1 3");
        JButton btnDown = new ArrowButton(SwingConstants.SOUTH, 1, 10);
        btnDown.addActionListener(arg0 -> moveRuleRowUp());
        this.add(btnDown, "cell 1 4");
    }

    private void initializeTable() {
        DefaultTableModel model = (DefaultTableModel) defaultSelectionTable.getModel();

        // Add some columns
        model.addColumn(Messages.getString("DefaultSelectionPanel.RuleNumber"));
        model.addColumn(Messages.getString("DefaultSelectionPanel.Quality"));
    }

    private void addRuleRow(String q) {
        DefaultTableModel model = (DefaultTableModel) defaultSelectionTable.getModel();
        model.addRow(new Object[] { model.getRowCount() + 1, q });
        // These are the combobox values
        List<String> both = new ArrayList<>();
        Collections.addAll(both, Messages.getString("DefaultSelectionPanel.Select"));

        both.addAll(videoPatterns.getQualityKeywords());
        String[] values = both.toArray(new String[both.size()]);

        // Set the combobox editor on the 1st visible column
        int vColIndex = 1;
        TableColumn col = defaultSelectionTable.getColumnModel().getColumn(vColIndex);
        col.setCellEditor(new MyComboBoxEditor(values));
        // If the cell should appear like a combobox in its
        // non-editing state, also set the combobox renderer
        col.setCellRenderer(new MyComboBoxRenderer(values));
    }

    private void removeRuleRow() {
        DefaultTableModel model = (DefaultTableModel) defaultSelectionTable.getModel();
        if (defaultSelectionTable.getSelectedRow() >= 0) {
            model.removeRow(defaultSelectionTable.getSelectedRow());
        }
    }

    protected void moveRuleRowDown() {
        DefaultTableModel model = (DefaultTableModel) defaultSelectionTable.getModel();
        if (defaultSelectionTable.getSelectedRow() >= 0 && defaultSelectionTable.getSelectedRow() < model.getRowCount()) {
            Object oSelected = model.getValueAt(defaultSelectionTable.getSelectedRow(), 1);
            Object oDown = model.getValueAt(defaultSelectionTable.getSelectedRow() + 1, 1);
            model.setValueAt(oSelected, defaultSelectionTable.getSelectedRow() + 1, 1);
            model.setValueAt(oDown, defaultSelectionTable.getSelectedRow(), 1);
        }
    }

    protected void moveRuleRowUp() {
        DefaultTableModel model = (DefaultTableModel) defaultSelectionTable.getModel();
        if (defaultSelectionTable.getSelectedRow() >= 0) {
            Object oSelected = model.getValueAt(defaultSelectionTable.getSelectedRow(), 1);
            Object oUp = model.getValueAt(defaultSelectionTable.getSelectedRow() - 1, 1);
            model.setValueAt(oSelected, defaultSelectionTable.getSelectedRow() - 1, 1);
            model.setValueAt(oUp, defaultSelectionTable.getSelectedRow(), 1);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Arrays.stream(this.getComponents()).forEach(c -> c.setEnabled(enabled));
    }

    public List<String> getDefaultSelectionList() {
        DefaultTableModel model = (DefaultTableModel) defaultSelectionTable.getModel();
        return IntStream.range(0, model.getRowCount()).mapToObj(i -> (String) model.getValueAt(i, 1)).collect(Collectors.toList());
    }

    public void setDefaultSelectionList(List<String> optionsDefaultSelectionQualityList) {
        optionsDefaultSelectionQualityList.forEach(this::addRuleRow);
    }
}
