package org.lodder.subtools.multisubdownloader.lib.library;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum LibraryOtherFileActionType {
    NOTHING("-- Maak uw keuze --"),
    REMOVE("Verwijderen"),
    RENAME("Hernoemen"),
    MOVE("Verplaatsen"),
    MOVEANDRENAME("Verplaats en Hernoemen");

    private final String description;

    @Override
    public String toString() {
        return this.description;
    }

    @Deprecated(since = "Settings version 2")
    public static LibraryOtherFileActionType fromString(String description) {
        return Arrays.stream(LibraryOtherFileActionType.values())
                .filter(v -> description.equalsIgnoreCase(v.toString())).findAny()
                .orElse(LibraryOtherFileActionType.NOTHING);
    }
}
