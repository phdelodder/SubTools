package org.lodder.subtools.multisubdownloader.gui.extra.table;

import java.util.Arrays;
import java.util.function.Function;

import org.lodder.subtools.multisubdownloader.Messages;
import org.lodder.subtools.sublibrary.model.Subtitle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SubtitleTableColumnName implements CustomColumnName {
    SELECT("App.Select", Boolean.class, true, subtitle -> false),
    SCORE("SubtitleTableColumnName.Score", Integer.class, false, Subtitle::getScore),
    FILENAME("SubtitleTableColumnName.Filename", String.class, false, Subtitle::getFileName),
    SOURCE("SubtitleTableColumnName.Source", String.class, false, Subtitle::getSubtitleSource),
    UPLOADER("SubtitleTableColumnName.Uploader", String.class, false, Subtitle::getUploader),
    HEARINGIMPAIRED("SubtitleTableColumnName.hearingimpaired", Boolean.class, false, Subtitle::isHearingImpaired),
    QUALITY("SubtitleTableColumnName.Quality", String.class, false, Subtitle::getQuality),
    RELEASEGROUP("SubtitleTableColumnName.Releasegroup", String.class, false, Subtitle::getReleaseGroup);

    private final String columnNameCode;
    @Getter
    private final Class<?> c;
    @Getter
    private final boolean editable;
    @Getter
    private final Function<Subtitle, Object> valueFunction;

    @Override
    public String getColumnName() {
        return Messages.getString(columnNameCode);
    }

    public static SubtitleTableColumnName forColumnName(String columnName) {
        return Arrays.stream(SubtitleTableColumnName.values()).filter(stcn -> stcn.getColumnName().equals(columnName)).findAny().orElseThrow();
    }

    public Object getValue(Subtitle subtitle) {
        return valueFunction.apply(subtitle);
    }
}
