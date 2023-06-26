package org.lodder.subtools.multisubdownloader.gui.dialog;

import java.io.Serial;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.tuple.Pair;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.UserInteractionHandlerGUI;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.JButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JButtonExtension.class, AbstractButtonExtension.class, JComponentExtension.class })
public class MappingEpisodeNameDialog extends MultiSubDialog {

    @Serial
    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private final Manager manager;
    private final MappingTableModel mappingTableModel;
    private final SubtitleProviderStore subtitleProviderStore;
    private final UserInteractionHandlerGUI userInteractionHandler;
    private Optional<SubtitleProvider> selectedSubtitleProvider;
    private final JButton btnAddCustomMapping;
    private MappingType selectedMappingType;

    /**
     * Create the dialog.
     */
    public MappingEpisodeNameDialog(JFrame frame, final SettingsControl prefCtrl, Manager manager, SubtitleProviderStore subtitleProviderStore,
            UserInteractionHandlerGUI userInteractionHandler) {
        super(frame, Messages.getString("MappingEpisodeNameDialog.Title"), true);
        this.manager = manager;
        this.subtitleProviderStore = subtitleProviderStore;
        this.userInteractionHandler = userInteractionHandler;
        this.btnAddCustomMapping = new JButton(Messages.getString("MappingEpisodeNameDialog.ChangeMapping"));
        this.mappingTableModel = new MappingTableModel(manager);
        initialize();
    }

    private void selectMappingType(MappingType mappingType) {
        this.selectedMappingType = mappingType;
        this.selectedSubtitleProvider = subtitleProviderStore.getAllProviders().stream()
                .filter(subtitleProvider -> subtitleProvider.getProviderName().equals(mappingType.getProviderName()))
                .findAny();
        btnAddCustomMapping.setEnabled(selectedSubtitleProvider.isPresent());
        mappingTableModel.setMappingType(mappingType);
        repaint();
    }

    @Getter
    public enum MappingType {
        TVDB("TVDB", "TVDB",
                new SelectionForKeyPrefix("", "TVDB-serieId-", k -> k.replace("-serieId-", "-tvdbSerie-"))),
        ADDIC7ED("Addic7ed", SubtitleSource.ADDIC7ED,
                new SelectionForKeyPrefix("", "ADDIC7ED-serieName-name:"),
                new SelectionForKeyPrefix("", "ADDIC7ED-serieName-tvdbId:")),
        ADDIC7ED_PROXY("Addic7ed (Proxy)", SubtitleSource.ADDIC7ED.name() + "-GESTDOWN",
                new SelectionForKeyPrefix("", "ADDIC7ED-GESTDOWN-serieName-name:"),
                new SelectionForKeyPrefix("", "ADDIC7ED-GESTDOWN-serieName-tvdbId:")),
        SUBSCENE("Subscene", SubtitleSource.SUBSCENE,
                new SelectionForKeyPrefix("", "SUBSCENE-serieName-name:"),
                new SelectionForKeyPrefix("", "SUBSCENE-serieName-tvdbId:")),
        TV_SUBTITLES("TVSubtitles", SubtitleSource.TVSUBTITLES,
                new SelectionForKeyPrefix("", "TVSUBTITLES-serieName-name:"),
                new SelectionForKeyPrefix("", "TVSUBTITLES-serieName-tvdbId:")),
        OPEN_SUBTITLES("OpenSubtitles", SubtitleSource.OPENSUBTITLES,
                new SelectionForKeyPrefix("", "OPENSUBTITLES-serieName-name:"),
                new SelectionForKeyPrefix("", "OPENSUBTITLES-serieName-tvdbId:")),
        PODNAPISI("Podnapisi", SubtitleSource.PODNAPISI,
                new SelectionForKeyPrefix("", "PODNAPISI-serieName-name:"),
                new SelectionForKeyPrefix("", "PODNAPISI-serieName-tvdbId:"));

        public static final BiFunction<Manager, SelectionForKeyPrefix, List<Pair<String, SerieMapping>>> MAPPING_SUPPLIER;
        private final String name;
        private final String providerName;
        private final String nameColumn;
        private final String mappingColumn;
        private final String providerNameColumn;
        private final SelectionForKeyPrefix[] selectionForKeyPrefixList;

        @Override
        public String toString() {
            return name;
        }

        static {
            MAPPING_SUPPLIER = (manager, selectionForKeyPrefix) -> manager.valueBuilder()
                    .cacheType(CacheType.DISK)
                    .keyFilter(k -> k.startsWith(selectionForKeyPrefix.keyPrefix))
                    .returnType(SerieMapping.class)
                    .getEntries();
        }

        MappingType(String name, SubtitleSource subtitleSource, SelectionForKeyPrefix... selectionForKeyPrefixList) {
            this(name, subtitleSource.name(), selectionForKeyPrefixList);
        }

        MappingType(String name, String providerName, SelectionForKeyPrefix... selectionForKeyPrefixList) {
            this.name = name;
            this.providerName = providerName;
            this.nameColumn = Messages.getString("MappingEpisodeNameDialog.SceneShowName");
            this.mappingColumn = Messages.getString("MappingEpisodeNameDialog.ProviderId");
            this.providerNameColumn = Messages.getString("MappingEpisodeNameDialog.ProviderName");
            this.selectionForKeyPrefixList = selectionForKeyPrefixList;
        }
    }

    public static record SelectionForKeyPrefix(String name, String keyPrefix, Function<String, String> deleteOtherFunction) {
        public SelectionForKeyPrefix(String name, String keyPrefix) {
            this(name, keyPrefix, null);
        }
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class Row extends Vector<String> {
        @Serial
        private static final long serialVersionUID = 8620670431074648999L;
        private final String key;
        private SerieMapping serieMapping;
        private final SelectionForKeyPrefix selectionForKeyPrefix;

        public Row(String key, String name, String providerId, String providerName, SerieMapping serieMapping,
                SelectionForKeyPrefix selectionForKeyPrefix) {
            this.key = key;
            this.serieMapping = serieMapping;
            this.selectionForKeyPrefix = selectionForKeyPrefix;
            add(name);
            add(providerId);
            add(providerName);
        }
    }

    @RequiredArgsConstructor
    private static class MappingTableModel extends DefaultTableModel {
        @Serial
        private static final long serialVersionUID = 7860605766969472980L;
        private final Manager manager;

        void setMappingType(MappingType mappingType) {
            setDataVector(null, new String[] { mappingType.getNameColumn(), mappingType.getMappingColumn(), mappingType.getProviderNameColumn() });
            Arrays.stream(mappingType.getSelectionForKeyPrefixList())
                    .flatMap(selectionForKeyPrefix -> MappingType.MAPPING_SUPPLIER.apply(manager, selectionForKeyPrefix).stream()
                            .map(serieMappingPair -> {
                                SerieMapping serieMapping = serieMappingPair.getValue();
                                String name = serieMapping.getName();
                                String providerId = serieMapping.getProviderId() == null ? "" : serieMapping.getProviderId();
                                String providerName = serieMapping.getProviderName();
                                if (providerId.contains("/")) {
                                    providerId = providerId.substring(providerId.lastIndexOf("/") + 1);
                                }
                                providerId = providerId.replace(".html", "");
                                return new Row(serieMappingPair.getKey(), name, providerId, providerName, serieMapping, selectionForKeyPrefix);
                            }))
                    .sorted(Comparator.comparing(row -> row.getSerieMapping() == null || row.getSerieMapping().getProviderName() == null ? "zzz"
                            : row.getSerieMapping().getName()))
                    .forEach(this::addRow);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }

    private void initialize() {
        setResizable(true);
        setBounds(150, 150, 650, 400);
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
            JPanel selectionPane = new JPanel();
            contentPanel.add(selectionPane);

            JLabel lblDefaultIncomingFolder = new JLabel(Messages.getString("MappingEpisodeNameDialog.SelectProvider"));
            selectionPane.add(lblDefaultIncomingFolder);

            JComboBox<MappingType> mappingTypeList = new JComboBox<>();
            mappingTypeList.setModel(new DefaultComboBoxModel<>(MappingType.values()));
            mappingTypeList.addItemListener(arg0 -> selectMappingType((MappingType) arg0.getItem()));
            selectMappingType(MappingType.values()[0]);
            selectionPane.add(mappingTypeList);
        }
        {
            JPanel pnlButtons = new JPanel();
            GridBagConstraints gbc_pnlButtons = new GridBagConstraints();
            gbc_pnlButtons.insets = new Insets(0, 0, 5, 0);
            gbc_pnlButtons.fill = GridBagConstraints.BOTH;
            gbc_pnlButtons.gridx = 0;
            gbc_pnlButtons.gridy = 0;
            contentPanel.add(pnlButtons, gbc_pnlButtons);
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
            buttonPane.setLayout(new MigLayout("", "[25px][50px][grow][50px][grow][50px][25px]", "[][25px,grow,fill]"));

            {
                JButton btnDeleteSelectedRow = new JButton(Messages.getString("MappingEpisodeNameDialog.DeleteRow"));
                btnDeleteSelectedRow.addActionListener(arg0 -> {
                    int rowNbr = table.convertRowIndexToModel(table.getSelectedRow());
                    MappingTableModel model = (MappingTableModel) table.getModel();

                    Row row = (Row) model.getDataVector().get(rowNbr);
                    String key = row.getKey();
                    manager.valueBuilder()
                            .cacheType(CacheType.DISK)
                            .key(key)
                            .remove();
                    if (row.getSelectionForKeyPrefix().deleteOtherFunction() != null) {
                        manager.valueBuilder()
                                .cacheType(CacheType.DISK)
                                .key(row.getSelectionForKeyPrefix().deleteOtherFunction().apply(key))
                                .remove();
                    }
                    model.removeRow(rowNbr);
                });
                buttonPane.add(btnDeleteSelectedRow, "skip");
            }

            {
                btnAddCustomMapping.withActionListener(() -> {
                    int rowNbr = table.convertRowIndexToModel(table.getSelectedRow());
                    MappingTableModel model = (MappingTableModel) table.getModel();

                    Row row = (Row) model.getDataVector().get(rowNbr);
                    String currentName = row.getSerieMapping().getName();

                    String message = Messages.getString("MappingEpisodeNameDialog.enterNewNameForSerie", currentName);
                    selectedSubtitleProvider.ifPresent(provider -> {
                        userInteractionHandler.enter(message, message).ifPresent(newName -> {
                            TvRelease tvRelease = TvRelease.builder()
                                    .name(currentName)
                                    .season(row.getSerieMapping().getSeason())
                                    .episode(1)
                                    .originalName(currentName)
                                    .customName(newName).build();
                            try {
                                provider.getProviderSerieId(tvRelease).ifPresentOrElse(providerSerieId -> {
                                    SerieMapping newSerieMapping =
                                            new SerieMapping(currentName, providerSerieId.getProviderId(), providerSerieId.getProviderName(),
                                                    providerSerieId.getSeason());
                                    row.setSerieMapping(newSerieMapping);
                                    List<? extends SortKey> sortKeys = table.getRowSorter().getSortKeys();
                                    selectMappingType(selectedMappingType);
                                    table.getRowSorter().setSortKeys(sortKeys);
                                }, () -> userInteractionHandler.message(
                                        Messages.getString("MappingEpisodeNameDialog.NoResultsFoundForSerieName", newName),
                                        Messages.getString("App.Info")));
                            } catch (Exception e) {
                                userInteractionHandler.message(
                                        Messages.getString("App.ErrorOccurred", e.getMessage()), Messages.getString("App.Error"));
                            }
                        });
                    });
                });
                buttonPane.add(btnAddCustomMapping, "skip");
            }

            {
                new JButton(Messages.getString("App.Close"))
                        .defaultButtonFor(getRootPane())
                        .withActionListener(() -> setVisible(false))
                        .withActionCommand(Messages.getString("App.Close"))
                        .addTo(buttonPane, "skip");
            }
        }
    }
}
