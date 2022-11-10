package org.lodder.subtools.multisubdownloader.gui.extra.table;

import org.lodder.subtools.multisubdownloader.Messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SearchColumnName implements CustomColumnName {
    RELEASE("App.Release", String.class, false),
    FILENAME("SearchColumnName.Filename", String.class, false),
    FOUND("SearchColumnName.NumberFound", Integer.class, false),
    SELECT("App.Select", Boolean.class, true),
    OBJECT("App.EpisodeObject", Object.class, false),
    SEASON("App.Season", String.class, false),
    EPISODE("App.Episode", String.class, false),
    TYPE("SearchColumnName.Type", String.class, false),
    TITLE("SearchColumnName.Title", String.class, false),
    SOURCE("SearchColumnName.Source", String.class, false),
    SCORE("SearchColumnName.Score", Integer.class, false);

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
