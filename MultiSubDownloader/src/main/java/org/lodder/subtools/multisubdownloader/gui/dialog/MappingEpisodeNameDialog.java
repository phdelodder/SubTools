package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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

import net.miginfocom.swing.MigLayout;

import javax.swing.JCheckBox;

import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.settings.model.MappingTvdbScene;

public class MappingEpisodeNameDialog extends MutliSubDialog {

  /**
     *
     */
  private static final long serialVersionUID = 1L;
  private final JPanel contentPanel = new JPanel();
  private JTable table;
  private final SettingsControl prefCtrl;
  private final Settings pref;
  private JCheckBox chkAutoUpdateMapping;

  /**
   * Create the dialog.
   */
  public MappingEpisodeNameDialog(JFrame frame, final SettingsControl prefCtrl) {
    super(frame, "Mapping Tvdb/Scene", true);
    this.prefCtrl = prefCtrl;
    pref = prefCtrl.getSettings();
    initialize();
    loadMappingTable();
    repaint();
  }

  private void loadMappingTable() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    while (model.getRowCount() > 0) {
      model.removeRow(0);
    }
    for (int i = 0; i < pref.getMappingSettings().getMappingList().size(); i++) {
      String tvdbId = "";
      if (pref.getMappingSettings().getMappingList().get(i).getTvdbId() > 0)
        tvdbId = Integer.toString(pref.getMappingSettings().getMappingList().get(i).getTvdbId());
      model.addRow(new String[] {pref.getMappingSettings().getMappingList().get(i).getSceneName(), tvdbId});
    }

    chkAutoUpdateMapping.setSelected(pref.isAutoUpdateMapping());
  }

  private void initialize() {
    setResizable(false);
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[] {0, 0};
    gbl_contentPanel.rowHeights = new int[] {0, 40, 0};
    gbl_contentPanel.columnWeights = new double[] {1.0, Double.MIN_VALUE};
    gbl_contentPanel.rowWeights = new double[] {0.0, 1.0, Double.MIN_VALUE};
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
        JButton btnAdd = new JButton("Rij toevoegen");
        btnAdd.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            String scene = JOptionPane.showInputDialog("Please input scene show name");
            if (!scene.equals("")) {
              String tvdbId = JOptionPane.showInputDialog("Please input TVDB SERIE ID");
              if (!tvdbId.equals("")) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(new Object[] {scene, tvdbId});
              }
            }
          }
        });
        pnlButtons.add(btnAdd);
      }
      {
        JButton btnDeleteSelectedRow = new JButton("Verwijder geselecteerde rij");
        btnDeleteSelectedRow.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            int row = table.getSelectedRow();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.removeRow(row);
          }
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
        table.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"Scene Serie naam",
            "TVDB Serie Id"}) {
          /**
                     *
                     */
          private static final long serialVersionUID = 1L;
          @SuppressWarnings("rawtypes")
          Class[] columnTypes = new Class[] {String.class, String.class, String.class};

          @SuppressWarnings({"unchecked", "rawtypes"})
          public Class getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
          }

          boolean[] columnEditables = new boolean[] {true, true, true};

          public boolean isCellEditable(int row, int column) {
            return columnEditables[column];
          }
        });
        RowSorter<TableModel> sorter;
        sorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(sorter);
        scrollPane.setViewportView(table);
      }
    }
    {
      JPanel buttonPane = new JPanel();
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton(" OK ");
        okButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            setVisible(false);
            storeMappingTable();
          }
        });
        buttonPane.setLayout(new MigLayout("", "[117px][grow,fill][62px,trailing]",
            "[][25px,grow,fill]"));
        {
          JButton btnUpdateMapping = new JButton("Update met Online Mapping");
          btnUpdateMapping.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              try {
                storeMappingTable();
                prefCtrl.updateMappingFromOnline();
                loadMappingTable();
              } catch (Exception e) {
                Logger.instance.error(Logger.stack2String(e));
              } catch (Throwable e) {
                Logger.instance.error(Logger.stack2String(e));
              }
            }
          });
          {
            chkAutoUpdateMapping = new JCheckBox("Mapping updaten bij het opstarten?");
            buttonPane.add(chkAutoUpdateMapping, "cell 0 0 2 1");
          }
          buttonPane.add(btnUpdateMapping, "cell 0 1,alignx left,aligny top");
        }
        okButton.setActionCommand("OK");
        buttonPane.add(okButton, "cell 2 1,alignx right,aligny top");
        getRootPane().setDefaultButton(okButton);
      }
    }
  }

  private void storeMappingTable() {
    List<MappingTvdbScene> list = new ArrayList<MappingTvdbScene>();
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    MappingTvdbScene item;
    for (int i = 0; i < model.getRowCount(); i++) {
      int tvdbid = 0;
      if (model.getValueAt(i, 1) != null && ((String) model.getValueAt(i, 1)).length() != 0) {
        tvdbid = Integer.parseInt((String) model.getValueAt(i, 1));
      }
      String scene = (String) model.getValueAt(i, 0);
      item = new MappingTvdbScene(scene, tvdbid);
      list.add(item);
    }
    pref.getMappingSettings().setMappingList(list);

    pref.setAutoUpdateMapping(chkAutoUpdateMapping.isSelected());
  }

}
