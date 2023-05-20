package org.lodder.subtools.multisubdownloader.gui.extra.table;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private static final Map<String, SearchColumnName> MAP =
            Arrays.stream(SearchColumnName.values()).collect(Collectors.toMap(SearchColumnName::getColumnName, Function.identity()));

    private final String columnNameCode;
    @Getter
    private final Class<?> c;
    @Getter
    private final boolean editable;

    @Override
    public String getColumnName() {
        return Messages.getString(columnNameCode);
    }

    public static Optional<SearchColumnName> getForColumnName(String columnName) {
        return Optional.ofNullable(MAP.get(columnName));
    }
}
