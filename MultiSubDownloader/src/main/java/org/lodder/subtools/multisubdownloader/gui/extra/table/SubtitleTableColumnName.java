package org.lodder.subtools.multisubdownloader.gui.extra.table;

import org.lodder.subtools.multisubdownloader.Messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubtitleTableColumnName implements CustomColumnName {
    SELECT(Messages.getString("App.Select"), Boolean.class, true),
    SCORE(Messages.getString("SubtitleTableColumnName.Score"), Integer.class, false),
    FILENAME(Messages.getString("SubtitleTableColumnName.Filename"), String.class, false),
    SOURCE(Messages.getString("SubtitleTableColumnName.Source"), String.class, false),
    UPLOADER(Messages.getString("SubtitleTableColumnName.Uploader"), String.class, false),
    HEARINGIMPAIRED(Messages.getString("SubtitleTableColumnName.hearingimpaired"), Boolean.class, false),
    QUALITY(Messages.getString("SubtitleTableColumnName.Quality"), String.class, false),
    RELEASEGROUP(Messages.getString("SubtitleTableColumnName.Releasegroup"), String.class, false);

    private final String columnName;
    private final Class<?> c;
    private final boolean editable;

}
