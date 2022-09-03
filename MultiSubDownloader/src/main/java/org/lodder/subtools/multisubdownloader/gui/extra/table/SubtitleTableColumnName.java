package org.lodder.subtools.multisubdownloader.gui.extra.table;

import org.lodder.subtools.multisubdownloader.Messages;

public enum SubtitleTableColumnName implements CustomColumnName {
    SELECT(Messages.getString("SubtitleTableColumnName.Select"), Boolean.class, true), SCORE(Messages
            .getString("SubtitleTableColumnName.Score"), Integer.class, false),
    FILENAME(Messages
            .getString("SubtitleTableColumnName.Filename"), String.class, false),
    SOURCE(Messages
            .getString("SubtitleTableColumnName.Source"), String.class, false),
    UPLOADER(Messages
            .getString("SubtitleTableColumnName.Uploader"), String.class, false),
    HEARINGIMPAIRED(
            Messages.getString("SubtitleTableColumnName.hearingimpaired"), Boolean.class, false),
    QUALITY(
            Messages.getString("SubtitleTableColumnName.Quality"), String.class, false),
    RELEASEGROUP(
            Messages.getString("SubtitleTableColumnName.Releasegroup"), String.class, false);

    private final String columnName;
    private final Class<?> c;
    private final boolean editable;

    private SubtitleTableColumnName(String columnName, Class<?> c, boolean editable) {
        this.columnName = columnName;
        this.c = c;
        this.editable = editable;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public Class<?> getC() {
        return c;
    }
}
