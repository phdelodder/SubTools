package org.lodder.subtools.multisubdownloader.gui.panels.preference;

import static java.util.function.Predicate.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.multisubdownloader.gui.extra.ArrowButton;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.button.AbstractButtonExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jscrollpane.JScrollPaneExtension;
import org.lodder.subtools.multisubdownloader.settings.SettingsControl;
import org.lodder.subtools.sublibrary.control.VideoPatterns.Source;

import java.awt.Component;
import java.awt.Container;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ Arrays.class, JComponentExtension.class, AbstractButtonExtension.class, JScrollPaneExtension.class })
public class DefaultSelectionPanel extends JPanel implements PreferencePanelIntf {

    private static final long serialVersionUID = 1L;
    private final SettingsControl settingsCtrl;
    private final ScrollTable<Source> unusedPatternsTable;
    private final ScrollTable<Source> usedPatternsTable;

    public DefaultSelectionPanel(SettingsControl settingsCtrl) {
        super(new MigLayout("fill, bottom, insets 0", "[grow][][grow][]", "[grow, bottom][grow, top]"));
        this.settingsCtrl = settingsCtrl;

        unusedPatternsTable = ScrollTable.create(Messages.getString("PreferenceDialog.DefaultSelectionUnused"), Source.class).add(this, "spany 2");
        new ArrowButton(SwingConstants.EAST, 1, 10).withActionListener(this::addPattern).addTo(this);
        usedPatternsTable = ScrollTable.create(Messages.getString("PreferenceDialog.DefaultSelectionUsed"), Source.class).add(this, "spany 2");
        new ArrowButton(SwingConstants.NORTH, 1, 10).withActionListener(this::moveRuleRowUp).addTo(this, "wrap");

        new ArrowButton(SwingConstants.WEST, 1, 10).withActionListener(this::removePattern).addTo(this, "skip");
        new ArrowButton(SwingConstants.SOUTH, 1, 10).withActionListener(this::moveRuleRowDown).addTo(this, "skip");

        loadPreferenceSettings();
    }

    private static class ScrollTable<E> extends Container {

        private static final long serialVersionUID = 1L;

        private final JScrollPane scrollPane;
        private final JTable table;
        private final DefaultTableModel model;

        public static <T> ScrollTable<T> create(String header, Class<T> elementType) {
            return new ScrollTable<>(header, elementType);
        }

        public ScrollTable(String header, Class<E> elementType) {
            this(header, (Collection<E>) null);
        }

        public ScrollTable(String header, Collection<E> items) {
            this(header, items == null ? null : items.stream());
        }

        private ScrollTable(String header, Stream<E> items) {
            this.table = new JTable(new DefaultTableModel(new String[] { header }, 1));
            this.scrollPane = new JScrollPane().withViewportView(table);
            this.model = (DefaultTableModel) table.getModel();
            model.removeRow(0);
            if (items != null) {
                items.forEach(this::addItem);
            }
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        public void addItem(E item) {
            model.addRow(new Object[] { item });
        }

        public int getSelectedRow() {
            return table.getSelectedRow();
        }

        public E getSelectedItem() {
            return getItemAt(getSelectedRow());
        }

        public void removeSelectedRow() {
            model.removeRow(getSelectedRow());
        }

        @SuppressWarnings("unchecked")
        public E getItemAt(int idx) {
            return (E) model.getValueAt(idx, 0);
        }

        public void setItemAt(E item, int idx) {
            model.setValueAt(item, idx, 0);
        }

        public void swapItems(int idx, int idxOther) {
            if (isValidIndex(idx) && isValidIndex(idxOther)) {
                E oSelected = getItemAt(idx);
                E oDown = getItemAt(idxOther);
                setItemAt(oSelected, idxOther);
                setItemAt(oDown, idx);
                setSelectedRow(idxOther);
            }
        }

        private boolean isValidIndex(int idx) {
            return idx >= 0 && idx < table.getRowCount();
        }

        public <S extends Container> ScrollTable<E> add(S parent, Object constraints) {
            parent.add(scrollPane, constraints);
            return this;
        }

        public int getRowCount() {
            return table.getRowCount();
        }

        public boolean isEmpty() {
            return getRowCount() == 0;
        }

        public void setSelectedRow(int idx) {
            table.setRowSelectionInterval(idx, idx);
        }

        public List<E> getItems() {
            return IntStream.range(0, model.getRowCount()).mapToObj(i -> getItemAt(i)).toList();
        }

        @Override
        public Component[] getComponents() {
            return new Component[] { scrollPane, table };
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            scrollPane.setEnabled(enabled);
            table.setEnabled(enabled);
        }
    }

    private void addPattern() {
        moveToOtherTable(unusedPatternsTable, usedPatternsTable);
    }

    private void removePattern() {
        moveToOtherTable(usedPatternsTable, unusedPatternsTable);
    }

    private void moveToOtherTable(ScrollTable<Source> source, ScrollTable<Source> destination) {
        if (source.getSelectedRow() >= 0) {
            int selectedRow = source.getSelectedRow();
            destination.addItem(source.getSelectedItem());
            source.removeSelectedRow();

            if (!source.isEmpty()) {
                source.setSelectedRow(selectedRow < source.getRowCount() ? selectedRow : selectedRow - 1);
            }
            destination.setSelectedRow(destination.getRowCount() - 1);
        }
    }

    protected void moveRuleRowDown() {
        int selectedRow = usedPatternsTable.getSelectedRow();
        usedPatternsTable.swapItems(selectedRow, selectedRow + 1);
    }

    protected void moveRuleRowUp() {
        int selectedRow = usedPatternsTable.getSelectedRow();
        usedPatternsTable.swapItems(selectedRow, selectedRow - 1);
    }

    public void loadPreferenceSettings() {
        Source.values().stream().filter(not(settingsCtrl.getSettings().getOptionsDefaultSelectionQualityList()::contains))
                .forEach(unusedPatternsTable::addItem);
        settingsCtrl.getSettings().getOptionsDefaultSelectionQualityList().forEach(usedPatternsTable::addItem);
    }

    public void savePreferenceSettings() {
        settingsCtrl.getSettings().setOptionsDefaultSelectionQualityList(usedPatternsTable.getItems());
    }

    @Override
    public boolean hasValidSettings() {
        return true;
    }
}
