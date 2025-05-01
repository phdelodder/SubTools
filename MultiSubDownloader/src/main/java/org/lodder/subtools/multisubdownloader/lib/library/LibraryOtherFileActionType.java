package org.lodder.subtools.multisubdownloader.lib.library;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.lodder.subtools.multisubdownloader.Messages;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum LibraryOtherFileActionType {

    NOTHING("PreferenceDialog.Action.Nothing"),
    REMOVE("PreferenceDialog.Action.Remove"),
    RENAME("PreferenceDialog.Action.Rename"),
    MOVE("PreferenceDialog.Action.Move"),
    MOVEANDRENAME("PreferenceDialog.Action.MoveAndRename");

    private final String msgCode;

    @Override
    public String toString() {
        return Messages.getText(msgCode);
    }

    @Deprecated(since = "Settings version 2")
    public static LibraryOtherFileActionType fromString(String description) {
        return LibraryOtherFileActionType.values().stream()
                .filter(v -> description.equalsIgnoreCase(v.toString())).findAny()
                .orElse(LibraryOtherFileActionType.NOTHING);
    }
}
