package org.lodder.subtools.multisubdownloader.gui;

import java.util.function.Function;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import java.awt.Component;


public final class ToStringListCellRenderer<T> implements ListCellRenderer<T> {
    private final ListCellRenderer originalRenderer;
    private final Function<T, String> toStringMapper;

    public ToStringListCellRenderer(final ListCellRenderer<T> originalRenderer,
            final Function<T, String> toStringMapper) {
        this.originalRenderer = originalRenderer;
        this.toStringMapper = toStringMapper;
    }

    @Override
    public Component getListCellRendererComponent(final JList<? extends T> list,
            final T value, final int index, final boolean isSelected,
            final boolean cellHasFocus) {
        return originalRenderer.getListCellRendererComponent(list,
                toStringMapper.apply(value), index, isSelected, cellHasFocus);
    }

}
