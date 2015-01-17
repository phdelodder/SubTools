package org.lodder.subtools.multisubdownloader.gui.extra;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.table.DefaultTableModel;

import org.lodder.subtools.multisubdownloader.gui.extra.table.SearchColumnName;
import org.lodder.subtools.multisubdownloader.gui.extra.table.VideoTable;

import net.miginfocom.swing.MigLayout;

public class SearchPanel extends JPanel {

  /**
	 * 
	 */
  private static final long serialVersionUID = -7602822323779710089L;
  private JPanel resultPanel;
  private JButton btnMove;
  private JButton btnSelectAll;
  private JScrollPane scrollPane;
  private JButton btnDownload;
  private JButton btnSelectFound;
  private JButton btnSelectNone;
  private VideoTable resultTable;

  public SearchPanel() {
    initialize();
  }

  private void initialize() {
    setLayout(new MigLayout("", "[grow,fill]", "[][][]"));
    resultPanel = new JPanel();
    add(resultPanel, "cell 0 1");
    resultPanel.setLayout(new MigLayout("", "[500px:n,grow][]", "[][][][][grow][]"));
    resultPanel.add(new JLabel("Zoek Resultaten"), "cell 0 0 2 1,gapy 5");
    resultPanel.add(new JSeparator(), "cell 0 0 2 1,growx,gaptop 5");

    scrollPane = new JScrollPane();
    resultPanel.add(scrollPane, "cell 0 1 1 4,grow");

    final JPanel panel_buttons_file = new JPanel();
    panel_buttons_file.setLayout(new MigLayout("", "[300px][176px]", "[]"));

    btnSelectNone = new JButton("Selecteer niets");
    resultPanel.add(btnSelectNone, "cell 1 1,aligny bottom");
    btnSelectNone.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        final DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
          model.setValueAt(false, i, resultTable.getColumnIdByName(SearchColumnName.SELECT));
        }
      }
    });

    btnSelectFound = new JButton("Selecteer gevonden");
    resultPanel.add(btnSelectFound, "cell 1 2,growy");
    btnSelectFound.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        final DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
          String found = (String) model.getValueAt(i, resultTable.getColumnIdByName(SearchColumnName.FOUND));
          final int numFound = Integer.parseInt(found.split("/")[0]);
          model.setValueAt(false, i, resultTable.getColumnIdByName(SearchColumnName.SELECT));
          if (numFound > 0) {
            model.setValueAt(true, i, resultTable.getColumnIdByName(SearchColumnName.SELECT));
          }
        }
      }
    });

    btnSelectAll = new JButton("Selecteer alles");
    resultPanel.add(btnSelectAll, "cell 1 4,aligny top");
    btnSelectAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        final DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
          model.setValueAt(true, i, resultTable.getColumnIdByName(SearchColumnName.SELECT));
        }
      }
    });

    resultPanel.add(panel_buttons_file, "cell 0 5,aligny top");

    btnDownload = new JButton("Download geselecteerde");
    btnDownload.setVisible(false);
    panel_buttons_file.add(btnDownload, "cell 0 0,alignx right,aligny top");

    btnMove = new JButton("Verplaats geselecteerde");
    btnMove.setVisible(false);
    panel_buttons_file.add(btnMove, "cell 1 0,alignx left,aligny top");
  }

  public void setInputPanel(JPanel inputPanel) {
    add(inputPanel, "cell 0 0");
  }

  public void setTable(VideoTable table) {
    resultTable = table;
    scrollPane.setViewportView(table);
  }

  public void setActionDownload(ActionListener actionListener) {
    btnDownload.addActionListener(actionListener);
    btnDownload.setVisible(true);
  }

  public void setActionMove(ActionListener actionListener) {
    btnMove.addActionListener(actionListener);
    btnMove.setVisible(true);
  }

  public void setEnableDownloadButtons(boolean b) {
    btnDownload.setEnabled(b);
    btnSelectFound.setEnabled(b);
    btnSelectAll.setEnabled(b);
    btnSelectNone.setEnabled(b);
    btnMove.setEnabled(b);
  }

  public void setSelectFoundVisible(boolean b) {
    btnSelectFound.setVisible(b);
  }
}
