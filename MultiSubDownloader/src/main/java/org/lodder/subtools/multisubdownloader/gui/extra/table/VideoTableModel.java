package org.lodder.subtools.multisubdownloader.gui.extra.table;

import javax.swing.table.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.Setter;
import org.lodder.subtools.multisubdownloader.UserInteractionHandler;
import org.lodder.subtools.sublibrary.model.MovieRelease;
import org.lodder.subtools.sublibrary.model.Release;
import org.lodder.subtools.sublibrary.model.Subtitle;
import org.lodder.subtools.sublibrary.model.TvRelease;

public class VideoTableModel extends DefaultTableModel {

    @Serial
    private static final long serialVersionUID = 4205143311042280620L;

    private static final List<SearchColumnName> SHOW_COLUMNS =
            List.of(SearchColumnName.TYPE, SearchColumnName.RELEASE, SearchColumnName.FILENAME, SearchColumnName.TITLE, SearchColumnName.SEASON,
                    SearchColumnName.EPISODE, SearchColumnName.FOUND, SearchColumnName.SELECT, SearchColumnName.OBJECT);

    private static final List<SearchColumnName> SUBTITLE_COLUMNS =
            List.of(SearchColumnName.FILENAME, SearchColumnName.SOURCE, SearchColumnName.SCORE, SearchColumnName.SELECT, SearchColumnName.OBJECT);

    private static final Map<SearchColumnName, Integer> SHOW_COLUMNS_INDEX = IntStream.range(0, SHOW_COLUMNS.size())
            .collect(() -> new EnumMap<>(SearchColumnName.class), (map, i) -> map.put(SHOW_COLUMNS.get(i), i), (l, r) -> {
                throw new IllegalArgumentException("Duplicate keys [%s] and [%s]".formatted(l, r));
            });

    private final Class<?>[] columnTypes;
    private final Boolean[] columnEditables;
    private final Map<Release, Row> rowMap = new LinkedHashMap<>();
    private boolean showOnlyFound = false;
    @Setter
    private UserInteractionHandler userInteractionHandler;

    private VideoTableModel(List<SearchColumnName> searchColumnNames) {
        super(new Object[][] {}, searchColumnNames.stream().map(SearchColumnName::getColumnName).toArray(String[]::new));
        this.columnTypes = searchColumnNames.stream().map(SearchColumnName::getC).toArray(Class<?>[]::new);
        this.columnEditables = searchColumnNames.stream().map(SearchColumnName::isEditable).toArray(Boolean[]::new);
    }

    public static VideoTableModel getDefaultVideoTableModel() {
        return new VideoTableModel(SHOW_COLUMNS);
    }

    public static VideoTableModel getDefaultSubtitleTableModel() {
        return new VideoTableModel(SUBTITLE_COLUMNS);
    }

    public void addRows(List<Release> l) {
        l.forEach(this::addRow);
    }

    public void addRow(Release release) {
        /* If we try to add an existing release, we just have to update that row */
        synchronized (this) {
            if (rowMap.containsKey(release)) {
                Row row = this.rowMap.get(release);
                int rowNr = new ArrayList<>(rowMap.keySet()).indexOf(release);
                int subsFound = row.updateSubsFound();
                this.setValueAt(subsFound, rowNr, SHOW_COLUMNS_INDEX.get(SearchColumnName.FOUND));
                return;
            }

            if (!showOnlyFound || release.getMatchingSubCount() != 0) {
                Row row = createRow(release);
                rowMap.put(release, row);
                this.addRow(row.getRowObject());
            }
        }
    }

    private Row createRow(Release release) {
        return new Row(release, userInteractionHandler);
    }

    private static class Row {
        private final Release release;
        private final UserInteractionHandler userInteractionHandler;
        @Getter
        public final Vector<Object> rowObject;

        public Row(Release release, UserInteractionHandler userInteractionHandler) {
            this.release = release;
            this.userInteractionHandler = userInteractionHandler;
            this.rowObject = SHOW_COLUMNS.stream().map(searchColumn -> switch (searchColumn) {
                case RELEASE -> {
                    if (release instanceof TvRelease tvRelease) {
                        yield tvRelease.getOriginalName();
                    } else if (release instanceof MovieRelease movieRelease) {
                        yield movieRelease.getName();
                    } else {
                        throw new IllegalArgumentException("Unexpected release type: " + release.getClass());
                    }
                }
                case FILENAME -> release.getFileName();
                case FOUND -> calculateSubsFound();
                case SELECT -> false;
                case OBJECT -> release;
                case SEASON -> release instanceof TvRelease tvRelease ? tvRelease.getSeason() : null;
                case EPISODE -> release instanceof TvRelease tvRelease ? tvRelease.getEpisodeNumbers().get(0) : null;
                case TYPE -> release.getVideoType();
                case TITLE -> release instanceof TvRelease tvRelease ? tvRelease.getTitle() : null;
                default -> throw new IllegalArgumentException("Unexpected value: " + searchColumn);
            }).collect(Collectors.toCollection(Vector::new));
        }

        private int calculateSubsFound() {
            return userInteractionHandler != null
                    ? userInteractionHandler.getAutomaticSelection(release.getMatchingSubs()).size()
                    :  release.getMatchingSubCount();
        }

        public int updateSubsFound() {
            synchronized (this) {
                int subsFound = calculateSubsFound();
                rowObject.set(SHOW_COLUMNS_INDEX.get(SearchColumnName.FOUND), subsFound);
                return subsFound;
            }
        }

        public boolean isSelected() {
            return (boolean) rowObject.get(SHOW_COLUMNS_INDEX.get(SearchColumnName.SELECT));
        }
    }

    public void addRow(Subtitle subtitle) {
        synchronized (this) {
            Vector<Object> row = SUBTITLE_COLUMNS.stream().map(searchColumn -> switch (searchColumn) {
                case FILENAME -> subtitle.getFileName();
                case SELECT -> false;
                case OBJECT -> subtitle;
                case SOURCE -> subtitle.getSubtitleSource();
                case SCORE -> subtitle.getScore();
                default -> throw new IllegalArgumentException("Unexpected value: " + searchColumn);
            }).collect(Collectors.toCollection(Vector::new));
            this.addRow(row);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return columnEditables[column];
    }

    public void processTable(Runnable runnable) {
        synchronized (this) {
            runnable.run();
        }
    }

    @Override
    public void removeRow(int i) {
        throw new IllegalStateException("Should not be used!)");
    }

    public void removeShow(Release selectedShow) {
        Iterator<Release> iterator = rowMap.keySet().iterator();
        int idx = -1;
        while (iterator.hasNext()) {
            idx++;
            Release release = iterator.next();
            if (release == selectedShow) {
                iterator.remove();
                super.removeRow(idx);
                return;
            }
        }
    }

    private void updateTable() {
        synchronized (this) {
            List<Release> newRowList = new ArrayList<>(this.rowMap.keySet());
            clearTable();
            addRows(newRowList);
        }
    }

    public void clearTable() {
        synchronized (this) {
            while (getRowCount() > 0) {
                super.removeRow(0);
            }
            rowMap.clear();
        }
    }

    public void setShowOnlyFound(boolean showOnlyFound) {
        this.showOnlyFound = showOnlyFound;
        updateTable();
    }

    public boolean isShowOnlyFound() {
        return this.showOnlyFound;
    }

    public void executedSynchronized(Runnable runnable) {
        synchronized (this) {
            runnable.run();
        }
    }

    public int getSelectedAmountOfShows() {
        return (int) rowMap.values().stream().filter(Row::isSelected).count();
    }

    public List<Release> getSelectedShows() {
        return rowMap.entrySet().stream().filter(entry -> entry.getValue().isSelected()).map(Entry::getKey).toList();
    }
}
