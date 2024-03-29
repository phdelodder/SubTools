package org.lodder.subtools.multisubdownloader.gui.panels;

import java.io.Serial;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.table.DefaultTableModel;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.table.CustomTable;
import org.lodder.subtools.multisubdownloader.gui.extra.table.SearchColumnName;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTableModel;

import java.awt.event.ActionListener;

import net.miginfocom.swing.MigLayout;

public class ResultPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = 2368028332402129899L;
    private JButton btnMove;
    private JButton btnSelectAll;
    private JScrollPane scrollPane;
    private JButton btnDownload;
    private JButton btnSelectFound;
    private JButton btnSelectNone;
    private CustomTable resultTable;

    public ResultPanel() {
        setLayout(new MigLayout("", "[500px:n,grow][]", "[][][][][grow][]"));

        createComponents();
        setupListeners();
        addComponentsToPanel();
    }

    private void addComponentsToPanel() {
        JPanel actionButtonsPanel = new JPanel();
        actionButtonsPanel.setLayout(new MigLayout("", "[300px][176px]", "[]"));
        actionButtonsPanel.add(btnDownload, "cell 0 0,alignx right,aligny top");
        actionButtonsPanel.add(btnMove, "cell 1 0,alignx left,aligny top");

        add(new JLabel(Messages.getString("ResultPanel.SearchResults")), "cell 0 0 2 1,gapy 5");
        add(new JSeparator(), "cell 0 0 2 1,growx,gaptop 5");
        add(scrollPane, "cell 0 1 1 4,grow");
        add(btnSelectNone, "cell 1 1,aligny bottom");
        add(btnSelectFound, "cell 1 2,growy");
        add(btnSelectAll, "cell 1 4,aligny top");
        add(actionButtonsPanel, "cell 0 5,aligny top");
    }

    public void hideSelectFoundSubtitlesButton() {
        this.btnSelectFound.setVisible(false);
    }

    public void showSelectFoundSubtitlesButton() {
        this.btnSelectFound.setVisible(true);
    }

    public void enableButtons() {
        this.setEnableButtons(true);
    }

    public void disableButtons() {
        this.setEnableButtons(false);
    }

    public void showDownloadButton() {
        this.btnDownload.setVisible(true);
    }

    public void hideDownloadButton() {
        this.btnDownload.setVisible(false);
    }

    public void showMoveButton() {
        this.btnMove.setVisible(true);
    }

    public void hideMoveButton() {
        this.btnMove.setVisible(false);
    }

    public void setTable(CustomTable table) {
        this.resultTable = table;
        scrollPane.setViewportView(this.resultTable);
    }

    public void setDownloadAction(ActionListener downloadAction) {
        if (downloadAction != null) {
            btnDownload.addActionListener(downloadAction);
        }
    }

    public void setMoveAction(ActionListener moveAction) {
        if (moveAction != null) {
            btnMove.addActionListener(moveAction);
        }
    }

    private void setupListeners() {
        btnSelectNone.addActionListener(e -> deselectAllRows());
        btnSelectFound.addActionListener(e -> selectRowsWithFoundSubtitles());
        btnSelectAll.addActionListener(e -> selectAllRows());
    }

    private void createComponents() {
        scrollPane = new JScrollPane();
        btnSelectNone = new JButton(Messages.getString("ResultPanel.SelectNothing"));
        btnSelectFound = new JButton(Messages.getString("ResultPanel.SelectFound"));
        btnSelectAll = new JButton(Messages.getString("ResultPanel.SelectEverything"));
        btnDownload = new JButton(Messages.getString("ResultPanel.DownloadSelected"));
        btnMove = new JButton(Messages.getString("ResultPanel.MoveSelected"));
    }

    private void setEnableButtons(boolean enabled) {
        this.btnDownload.setEnabled(enabled);
        this.btnSelectFound.setEnabled(enabled);
        this.btnSelectAll.setEnabled(enabled);
        this.btnSelectNone.setEnabled(enabled);
        this.btnMove.setEnabled(enabled);
    }

    private VideoTableModel getTableModel() {
        return (VideoTableModel) resultTable.getModel();
    }

    private void setRowsSelection(boolean selected) {
        final DefaultTableModel model = getTableModel();
        int columnNr = resultTable.getColumnIdByName(SearchColumnName.SELECT);
        for (int rowNr = 0; rowNr < model.getRowCount(); rowNr++) {
            model.setValueAt(selected, rowNr, columnNr);
        }
    }

    public void selectAllRows() {
        setRowsSelection(true);
    }

    public void deselectAllRows() {
        setRowsSelection(false);
    }

    public void selectRowsWithFoundSubtitles() {
        final DefaultTableModel model = getTableModel();
        int columnNrSELECT = resultTable.getColumnIdByName(SearchColumnName.SELECT);
        int columnNrFOUNDSUBTITLES = resultTable.getColumnIdByName(SearchColumnName.FOUND);

        for (int rowNr = 0; rowNr < model.getRowCount(); rowNr++) {
            final int numFound = (Integer) model.getValueAt(rowNr, columnNrFOUNDSUBTITLES);
            model.setValueAt(false, rowNr, columnNrSELECT);

            if (numFound > 0) {
                model.setValueAt(true, rowNr, columnNrSELECT);
            }
        }
    }

    public CustomTable getTable() {
        return this.resultTable;
    }

    public void clearTable() {
        getTableModel().clearTable();
    }
}
