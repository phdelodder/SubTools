package org.lodder.subtools.multisubdownloader.lib.library;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum LibraryActionType {
    NOTHING("PreferenceDialog.Action.Nothing"),
    RENAME("PreferenceDialog.Action.Rename"),
    MOVE("PreferenceDialog.Action.Move"),
    MOVEANDRENAME("PreferenceDialog.Action.MoveAndRename");

    @Getter
    private final String msgCode;

    @Deprecated(since = "Settings version 2")
    public static LibraryActionType fromString(String description) {
        return Arrays.stream(LibraryActionType.values())
                .filter(v -> description.equalsIgnoreCase(v.toString())).findAny()
                .orElse(LibraryActionType.NOTHING);
    }
}
