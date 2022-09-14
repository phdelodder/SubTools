package org.lodder.subtools.multisubdownloader.lib.library;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum LibraryActionType {
    NOTHING("-- Maak uw keuze --"),
    RENAME("Hernoemen"),
    MOVE("Verplaatsen"),
    MOVEANDRENAME("Verplaats en Hernoemen");

    private final String description;

    @Override
    public String toString() {
        return this.description;
    }

    @Deprecated(since = "Settings version 2")
    public static LibraryActionType fromString(String description) {
        return Arrays.stream(LibraryActionType.values())
                .filter(v -> description.equalsIgnoreCase(v.toString())).findAny()
                .orElse(LibraryActionType.NOTHING);
    }
}
