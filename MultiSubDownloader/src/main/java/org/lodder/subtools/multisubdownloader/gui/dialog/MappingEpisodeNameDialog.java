package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.settings.SettingValue;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.settings.model.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import net.miginfocom.swing.MigLayout;

public class MappingEpisodeNameDialog extends MultiSubDialog {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MappingEpisodeNameDialog.class);

    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private final SettingsControl prefCtrl;
    private final Settings pref;
    private JCheckBox chkAutoUpdateMapping;

    /**
     * Create the dialog.
     */
    public MappingEpisodeNameDialog(JFrame frame, final SettingsControl prefCtrl) {
        super(frame, Messages.getString("MappingEpisodeNameDialog.Title"), true);
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
        pref.getMappingSettings().forEach((tvdbId, tvdbMapping) -> model.addRow(new String[] { tvdbMapping.getName(), String.valueOf(tvdbId) }));

        chkAutoUpdateMapping.setSelected(pref.isAutoUpdateMapping());
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
                        String tvdbId =
                                JOptionPane.showInputDialog(Messages
                                        .getString("MappingEpisodeNameDialog.EnterTvdbId"));
                        if (!"".equals(tvdbId)) {
                            DefaultTableModel model = (DefaultTableModel) table.getModel();
                            model.addRow(new Object[] { scene, tvdbId });
                        }
                    }
                });
                pnlButtons.add(btnAdd);
            }
            {
                JButton btnDeleteSelectedRow =
                        new JButton(Messages.getString("MappingEpisodeNameDialog.DeleteRow"));
                btnDeleteSelectedRow.addActionListener(arg0 -> {
                    int row = table.getSelectedRow();
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
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
                table.setModel(new DefaultTableModel(new Object[][] {}, new String[] {
                        Messages.getString("MappingEpisodeNameDialog.SceneShowName"),
                        Messages.getString("MappingEpisodeNameDialog.TvdbId") }) {
                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;
                    @SuppressWarnings("rawtypes")
                    Class[] columnTypes = { String.class, String.class, String.class };

                    @Override
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    public Class getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }

                    boolean[] columnEditables = { true, true, true };

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return columnEditables[column];
                    }
                });
                RowSorter<TableModel> sorter;
                sorter = new TableRowSorter<>(table.getModel());
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
                {
                    JButton btnUpdateMapping =
                            new JButton(Messages.getString("MappingEpisodeNameDialog.UpdateWithOnlineMapping"));
                    btnUpdateMapping.addActionListener(arg0 -> {
                        try {
                            storeMappingTable();
                            prefCtrl.updateMappingFromOnline();
                            loadMappingTable();
                        } catch (Throwable e) {
                            LOGGER.error("btnUpdateMapping", e);
                        }
                    });
                    {
                        chkAutoUpdateMapping =
                                new JCheckBox(Messages.getString("MappingEpisodeNameDialog.UpdateMappingOnStart"));
                        buttonPane.add(chkAutoUpdateMapping, "cell 0 0 2 1");
                    }
                    buttonPane.add(btnUpdateMapping, "cell 0 1,alignx left,aligny top");
                }
                okButton.setActionCommand(Messages.getString("MappingEpisodeNameDialog.OK"));
                buttonPane.add(okButton, "cell 2 1,alignx right,aligny top");
                getRootPane().setDefaultButton(okButton);
            }
        }
    }

    private void storeMappingTable() {
        Preferences preferences = Preferences.userRoot().node("MultiSubDownloader");
        SettingValue.DICTIONARY.store(prefCtrl, preferences);
        pref.setAutoUpdateMapping(chkAutoUpdateMapping.isSelected());
    }

}
