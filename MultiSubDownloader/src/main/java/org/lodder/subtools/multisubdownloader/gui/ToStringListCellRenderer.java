package org.lodder.subtools.multisubdownloader.gui;

import java.util.function.Function;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.lodder.subtools.multisubdownloader.Messages;

import java.awt.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToStringListCellRenderer<T> implements ListCellRenderer<T> {
    private final ListCellRenderer originalRenderer;
    private final Function<T, String> toStringMapper;

    public static <T> ToStringListCellRenderer<T> of(ListCellRenderer originalRenderer, Function<T, String> toStringMapper) {
        return new ToStringListCellRenderer<>(originalRenderer, toStringMapper);
    }

    public static <T> ToStringListCellRenderer<T> ofMessage(ListCellRenderer originalRenderer, Function<T, String> toStringMapper) {
        return of(originalRenderer, item -> Messages.getString(toStringMapper.apply(item)));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        return originalRenderer.getListCellRendererComponent(list, toStringMapper.apply(value), index, isSelected, cellHasFocus);
    }

}
