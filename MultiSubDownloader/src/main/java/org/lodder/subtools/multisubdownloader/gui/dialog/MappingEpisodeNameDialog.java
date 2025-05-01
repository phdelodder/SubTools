package org.lodder.subtools.multisubdownloader.gui.dialog;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.Serial;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.val;
import manifold.ext.props.rt.api.var;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.multisubdownloader.UserInteractionHandlerGUI;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProvider;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleProviderStore;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.model.TvRelease;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;

public class MappingEpisodeNameDialog extends MultiSubDialog {

    @Serial private static final long serialVersionUID = 1L;

    private final MappingTableModel mappingTableModel;
    private final SubtitleProviderStore subtitleProviderStore;
    private final JButton btnAddCustomMapping;
    private final JTable table;
    private Optional<SubtitleProvider> selectedSubtitleProvider;
    private MappingType selectedMappingType;

    public MappingEpisodeNameDialog(@Nullable JFrame frame=null, Manager manager,
        SubtitleProviderStore subtitleProviderStore,
        UserInteractionHandlerGUI userInteractionHandler) {
        super(frame, getText("MappingEpisodeNameDialog.Title"), true);
        this.subtitleProviderStore = subtitleProviderStore;
        this.mappingTableModel = new MappingTableModel(manager);
        setResizable(true);
        setBounds(150, 150, 650, 400);

        table = new JTable().model(mappingTableModel).rowSorter(TableRowSorter::new);

        contentPane
            .layout(new BorderLayout())
            .addComponent(BorderLayout.CENTER, new JPanel()
                .border(new EmptyBorder(5, 5, 5, 5))
                .layout(new GridBagLayout()
                    .columnWidths(new int[]{0, 0})
                    .rowHeights(new int[]{0, 40, 0})
                    .columnWeights(new double[]{1.0, Double.MIN_VALUE})
                    .rowWeights(new double[]{0.0, 1.0, Double.MIN_VALUE}))
                // select provider panel
                .addComponent(new JPanel()
                    .addComponent(new JLabel(getText("MappingEpisodeNameDialog.SelectProvider")))
                    .addComponent(new JComboBox<>()
                        .model(new DefaultComboBoxModel<>(MappingType.values()))
                        .itemListener(event -> selectMappingType((MappingType) event.getItem()))))
                .addComponent(new JPanel(),
                    new GridBagConstraints().insets(new Insets(0, 0, 5, 0))
                        .fill(GridBagConstraints.BOTH).gridx(0).gridy(0))
                .addComponent(new JScrollPane().viewportView(table),
                    new GridBagConstraints().fill(GridBagConstraints.BOTH).gridx(0).gridy(1)))
            // button panel
            .addComponent(BorderLayout.SOUTH, new JPanel()
                .layout(new MigLayout("", "[25px][50px][grow][50px][grow][50px][25px]",
                    "[][25px,grow,fill]"))
                .addComponent("skip", new JButton(getText("MappingEpisodeNameDialog.DeleteRow"))
                    .actionListener(_ -> {
                        int rowNbr = table.convertRowIndexToModel(table.getSelectedRow());
                        MappingTableModel model = (MappingTableModel) table.getModel();
                        Row row = (Row) model.getDataVector().get(rowNbr);
                        manager.getCache(CacheType.DISK, row.key).remove();
                        if (row.selectionForKeyPrefix.deleteOtherFunction() != null) {
                            manager.getCache(CacheType.DISK,
                                row.selectionForKeyPrefix.deleteOtherFunction().apply(row.key)).remove();
                        }
                        model.removeRow(rowNbr);
                    }))
                .addComponent("skip", btnAddCustomMapping =
                    new JButton(getText("MappingEpisodeNameDialog.ChangeMapping"))
                        .actionListener(() -> {
                            int rowNbr = table.convertRowIndexToModel(table.getSelectedRow());
                            MappingTableModel model = (MappingTableModel) table.getModel();

                            Row row = (Row) model.getDataVector().get(rowNbr);
                            String currentName = row.serieMapping.name;

                            String message = getText("MappingEpisodeNameDialog.enterNewNameForSerie",
                                currentName);
                            selectedSubtitleProvider.ifPresent(provider ->
                                userInteractionHandler.enter(message).ifPresent(newName -> {
                                    TvRelease tvRelease = new TvRelease(
                                        name:currentName,
                                        season:row.serieMapping.season,
                                        episode:1,
                                        originalName:currentName,
                                        customName:newName);
                                    try {
                                        provider.getProviderSerieId(tvRelease).ifPresentOrElse(serieId -> {
                                            row.serieMapping =
                                                new SerieMapping(currentName, serieId.providerId, serieId.providerName,
                                                    serieId.season);
                                            List<? extends SortKey> sortKeys = table.rowSorter.sortKeys;
                                            selectMappingType(selectedMappingType);
                                            table.rowSorter.sortKeys = sortKeys;
                                        }, () -> userInteractionHandler.message(
                                            getText("MappingEpisodeNameDialog.NoResultsFoundForSerieName", newName),
                                            getText("App.Info")));
                                    } catch (Exception e) {
                                        userInteractionHandler.message(getText("App.ErrorOccurred", e.getMessage()),
                                            getText("App.Error"));
                                    }
                                }));
                        }))
                .addComponent("skip", new JButton(getText("App.Close"))
                    .defaultButtonFor(getRootPane())
                    .actionListener(() -> setVisible(false))
                    .actionCommand(getText("App.Close"))));
        selectMappingType(MappingType.values()[0]);
    }

    private void selectMappingType(MappingType mappingType) {
        this.selectedMappingType = mappingType;
        this.selectedSubtitleProvider = subtitleProviderStore.getAllProviders()
            .stream()
            .filter(subtitleProvider -> subtitleProvider.providerName.equals(mappingType.providerName))
            .findAny();
        btnAddCustomMapping.enabled = selectedSubtitleProvider.isPresent();
        mappingTableModel.mappingType = mappingType;
        repaint();
    }

    public enum MappingType {
        TVDB("TVDB", "TVDB",
            new SelectionForKeyPrefix("", "TVDB-serieId-", k -> k.replace("-serieId-", "-tvdbSerie-"))),
        ADDIC7ED("Addic7ed", SubtitleSource.ADDIC7ED, new SelectionForKeyPrefix("", "ADDIC7ED-serieName-name:"),
            new SelectionForKeyPrefix("", "ADDIC7ED-serieName-tvdbId:")),
        ADDIC7ED_PROXY("Addic7ed (Proxy)", SubtitleSource.ADDIC7ED.name() + "-GESTDOWN",
            new SelectionForKeyPrefix("", "ADDIC7ED-GESTDOWN-serieName-name:"),
            new SelectionForKeyPrefix("", "ADDIC7ED-GESTDOWN-serieName-tvdbId:")),
        SUBSCENE("Subscene", SubtitleSource.SUBSCENE, new SelectionForKeyPrefix("", "SUBSCENE-serieName-name:"),
            new SelectionForKeyPrefix("", "SUBSCENE-serieName-tvdbId:")),
        TV_SUBTITLES("TVSubtitles", SubtitleSource.TVSUBTITLES,
            new SelectionForKeyPrefix("", "TVSUBTITLES-serieName-name:"),
            new SelectionForKeyPrefix("", "TVSUBTITLES-serieName-tvdbId:")),
        OPEN_SUBTITLES("OpenSubtitles", SubtitleSource.OPENSUBTITLES,
            new SelectionForKeyPrefix("", "OPENSUBTITLES-serieName-name:"),
            new SelectionForKeyPrefix("", "OPENSUBTITLES-serieName-tvdbId:")),
        PODNAPISI("Podnapisi", SubtitleSource.PODNAPISI, new SelectionForKeyPrefix("", "PODNAPISI-serieName-name:"),
            new SelectionForKeyPrefix("", "PODNAPISI-serieName-tvdbId:"));

        public static final BiFunction<Manager, SelectionForKeyPrefix, List<Pair<String, SerieMapping>>>
            MAPPING_SUPPLIER;
        @val String name;
        @val String providerName;
        @val String nameColumn;
        @val String mappingColumn;
        @val String providerNameColumn;
        @val SelectionForKeyPrefix[] selectionForKeyPrefixList;

        @Override
        public String toString() {
            return name;
        }

        static {
            MAPPING_SUPPLIER = (manager, selectionForKeyPrefix) ->
                manager.getCache(CacheType.DISK, k -> k.startsWith(selectionForKeyPrefix.keyPrefix)).getEntries();
        }

        MappingType(String name, SubtitleSource subtitleSource, SelectionForKeyPrefix... selectionForKeyPrefixList) {
            this(name, subtitleSource.name(), selectionForKeyPrefixList);
        }

        MappingType(String name, String providerName, SelectionForKeyPrefix... selectionForKeyPrefixList) {
            this.name = name;
            this.providerName = providerName;
            this.nameColumn = getText("MappingEpisodeNameDialog.SceneShowName");
            this.mappingColumn = getText("MappingEpisodeNameDialog.ProviderId");
            this.providerNameColumn = getText("MappingEpisodeNameDialog.ProviderName");
            this.selectionForKeyPrefixList = selectionForKeyPrefixList;
        }
    }

    public record SelectionForKeyPrefix(String name, String keyPrefix, Function<String, String> deleteOtherFunction) {
        public SelectionForKeyPrefix(String name, String keyPrefix) {
            this(name, keyPrefix, null);
        }
    }

    private static class Row extends Vector<String> {
        @Serial private static final long serialVersionUID = 8620670431074648999L;
        @val String key;
        @val SelectionForKeyPrefix selectionForKeyPrefix;
        @var SerieMapping serieMapping;

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

    @AllArgsConstructor
    private static class MappingTableModel extends DefaultTableModel {
        @Serial private static final long serialVersionUID = 7860605766969472980L;
        @val Manager manager;

        void setMappingType(MappingType mappingType) {
            setDataVector(null,
                new String[]{mappingType.nameColumn, mappingType.mappingColumn, mappingType.providerNameColumn});
            Arrays.stream(mappingType.selectionForKeyPrefixList)
                .flatMap(selectionForKeyPrefix -> MappingType.MAPPING_SUPPLIER.apply(manager, selectionForKeyPrefix)
                    .stream()
                    .map(serieMappingPair -> {
                        SerieMapping serieMapping = serieMappingPair.getValue();
                        String providerId = serieMapping.providerId == null ? "" : serieMapping.providerId;
                        if (providerId.contains("/")) {
                            providerId = providerId.substring(providerId.lastIndexOf("/") + 1);
                        }
                        providerId = providerId.replace(".html", "");
                        return new Row(serieMappingPair.getKey(), serieMapping.name, providerId,
                            serieMapping.providerName, serieMapping, selectionForKeyPrefix);
                    }))
                .sorted(Comparator.comparing(
                    row -> row.serieMapping == null || row.serieMapping.providerName == null ? "zzz" :
                        row.serieMapping.name))
                .forEach(this::addRow);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }
}
