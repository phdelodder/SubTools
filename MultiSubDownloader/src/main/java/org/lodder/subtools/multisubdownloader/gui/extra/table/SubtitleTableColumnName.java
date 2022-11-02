package org.lodder.subtools.multisubdownloader.gui.extra.table;

import org.lodder.subtools.multisubdownloader.Messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SubtitleTableColumnName implements CustomColumnName {
    SELECT("App.Select", Boolean.class, true),
    SCORE("SubtitleTableColumnName.Score", Integer.class, false),
    FILENAME("SubtitleTableColumnName.Filename", String.class, false),
    SOURCE("SubtitleTableColumnName.Source", String.class, false),
    UPLOADER("SubtitleTableColumnName.Uploader", String.class, false),
    HEARINGIMPAIRED("SubtitleTableColumnName.hearingimpaired", Boolean.class, false),
    QUALITY("SubtitleTableColumnName.Quality", String.class, false),
    RELEASEGROUP("SubtitleTableColumnName.Releasegroup", String.class, false);

    private final String columnNameCode;
    @Getter
    private final Class<?> c;
    @Getter
    private final boolean editable;

    @Override
    public String getColumnName() {
        return Messages.getString(columnNameCode);
    }
}
