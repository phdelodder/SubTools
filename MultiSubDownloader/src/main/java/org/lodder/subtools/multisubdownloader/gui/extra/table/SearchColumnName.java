package org.lodder.subtools.multisubdownloader.gui.extra.table;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.multisubdownloader.Messages;

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
            SearchColumnName.values().stream()
                    .collect(Collectors.toMap(SearchColumnName::getColumnName, Function.identity()));

    @val @override String columnName;
    @val @override Class<?> clazz;
    @val @override boolean editable;

    SearchColumnName(String columnNameCode, Class<?> clazz, boolean editable) {
        this.columnName = Messages.getText(columnNameCode);
        this.clazz = clazz;
        this.editable = editable;
    }

    public static Optional<SearchColumnName> getForColumnName(String columnName) {
        return Optional.ofNullable(MAP.get(columnName));
    }
}
