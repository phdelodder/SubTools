package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * @author <a href="http://vetruvet.blogspot.com/2011/03/jcombobox-with-disabled-items.html">author</a>
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

    public static <T> PartialDisableComboBox<T> of(T... items) {
        return new PartialDisableComboBox<>(items);
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
        requireValidIndex(index);
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
        requireValidIndex(index);
        if (itemsState.get(index)) {
            super.setSelectedIndex(index);
        }
    }

    public void setItemEnabled(int index, boolean enabled) {
        itemsState.set(requireValidIndex(index), enabled);
    }

    public boolean isItemEnabled(int index) {
        return itemsState.get(requireValidIndex(index));
    }

    private int requireValidIndex(int index) {
        if (index < 0 || index >= itemsState.size()) {
            throw new IllegalArgumentException("Item Index out of Bounds!");
        }
        return index;
    }
}