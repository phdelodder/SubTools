package org.lodder.subtools.multisubdownloader.lib.library;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.val;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum LibraryActionType {
    NOTHING("PreferenceDialog.Action.Nothing"),
    RENAME("PreferenceDialog.Action.Rename"),
    MOVE("PreferenceDialog.Action.Move"),
    MOVEANDRENAME("PreferenceDialog.Action.MoveAndRename");

    @val String msgCode;

    @Deprecated(since = "Settings version 2")
    public static LibraryActionType fromString(String description) {
        return LibraryActionType.values().stream()
                .filter(v -> description.equalsIgnoreCase(v.toString())).findAny()
                .orElse(LibraryActionType.NOTHING);
    }
}
