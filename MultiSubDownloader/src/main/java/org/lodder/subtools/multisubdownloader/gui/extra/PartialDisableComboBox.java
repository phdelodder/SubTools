package org.lodder.subtools.multisubdownloader.gui.extra;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import java.awt.Component;

/**
 *
 * @author http://vetruvet.blogspot.com/2011/03/jcombobox-with-disabled-items.html
 */
public class PartialDisableComboBox<T> extends JComboBox<T> {
    @Serial
    private static final long serialVersionUID = -1690671707274328126L;

    private final List<Boolean> itemsState = new ArrayList<>();

    public PartialDisableComboBox(T[] items) {
        super();
        Arrays.stream(items).forEach(this::addItem);
        this.setRenderer(new BasicComboBoxRenderer() {
            @Serial
            private static final long serialVersionUID = -2774241371293899669L;

            @SuppressWarnings("rawtypes")
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                boolean disabled = index >= 0 && index < itemsState.size() && !itemsState.get(index);
                c.setEnabled(!disabled);
                c.setFocusable(!disabled);
                return c;
            }
        });
    }

    @Override
    public void addItem(T item) {
        this.addItem(item, true);
    }

    public void addItem(T item, boolean enabled) {
        super.addItem(item);
        itemsState.add(enabled);
    }

    @Override
    public void insertItemAt(T item, int index) {
        this.insertItemAt(item, index, true);
    }

    public void insertItemAt(T item, int index, boolean enabled) {
        super.insertItemAt(item, index);
        itemsState.add(index, enabled);
    }

    @Override
    public void removeAllItems() {
        super.removeAllItems();
        itemsState.clear();
    }

    @Override
    public void removeItemAt(int index) {
        if (index < 0 || index >= itemsState.size()) {
            throw new IllegalArgumentException("Item Index out of Bounds!");
        }
        super.removeItemAt(index);
        itemsState.remove(index);
    }

    @Override
    public void removeItem(Object item) {
        IntStream.range(0, this.getItemCount()).filter(i -> this.getItemAt(i) == item).forEach(itemsState::remove);
        super.removeItem(item);
    }

    @Override
    public void setSelectedIndex(int index) {
        if (index < 0 || index >= itemsState.size()) {
            throw new IllegalArgumentException("Item Index out of Bounds!");
        }
        if (itemsState.get(index)) {
            super.setSelectedIndex(index);
        }
    }

    public void setItemEnabled(int index, boolean enabled) {
        if (index < 0 || index >= itemsState.size()) {
            throw new IllegalArgumentException("Item Index out of Bounds!");
        }
        itemsState.set(index, enabled);
    }

    public boolean isItemEnabled(int index) {
        if (index < 0 || index >= itemsState.size()) {
            throw new IllegalArgumentException("Item Index out of Bounds!");
        }
        return itemsState.get(index);
    }
}
