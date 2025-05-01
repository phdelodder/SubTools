package org.lodder.subtools.multisubdownloader.gui.extra.table;

import java.util.function.Function;

import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.sublibrary.model.Subtitle;

public enum SubtitleTableColumnName implements CustomColumnName {
    SELECT("App.Select", Boolean.class, true, _ -> false),
    SCORE("SubtitleTableColumnName.Score", Integer.class, false, Subtitle::getScore),
    FILENAME("SubtitleTableColumnName.Filename", String.class, false, Subtitle::getFileName),
    SOURCE("SubtitleTableColumnName.Source", String.class, false, Subtitle::getSubtitleSource),
    UPLOADER("SubtitleTableColumnName.Uploader", String.class, false, Subtitle::getUploader),
    HEARINGIMPAIRED("SubtitleTableColumnName.hearingImpaired", Boolean.class, false, Subtitle::isHearingImpaired),
    QUALITY("SubtitleTableColumnName.Quality", String.class, false, Subtitle::getQuality),
    RELEASEGROUP("SubtitleTableColumnName.Releasegroup", String.class, false, Subtitle::getReleaseGroup);


    @val @override String columnName;
    @val @override Class<?> clazz;
    @val @override boolean editable;
    @val Function<Subtitle, Object> valueFunction;

    SubtitleTableColumnName(String columnNameCode, Class<?> clazz, boolean editable,
            Function<Subtitle, Object> valueFunction) {
        this.columnName = Messages.getText(columnNameCode);
        this.clazz = clazz;
        this.editable = editable;
        this.valueFunction = valueFunction;
    }

    public static SubtitleTableColumnName forColumnName(String columnName) {
        return SubtitleTableColumnName.values().stream()
                .filter(stcn -> stcn.columnName.equals(columnName))
                .findAny()
                .orElseThrow();
    }

    public Object getValue(Subtitle subtitle) {
        return valueFunction.apply(subtitle);
    }
}
