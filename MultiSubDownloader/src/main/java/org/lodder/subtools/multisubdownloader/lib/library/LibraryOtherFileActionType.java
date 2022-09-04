package org.lodder.subtools.multisubdownloader.lib.library;

public enum LibraryOtherFileActionType {
    NOTHING("-- Maak uw keuze --"), REMOVE("Verwijderen"), RENAME("Hernoemen"), MOVE("Verplaatsen"), MOVEANDRENAME(
            "Verplaats en Hernoemen");

    private final String description;

    LibraryOtherFileActionType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }

    public static LibraryOtherFileActionType fromString(String description) {
        if (description != null) {
            for (LibraryOtherFileActionType action : LibraryOtherFileActionType.values()) {
                if (description.equalsIgnoreCase(action.toString())) {
                    return action;
                }
            }
        }
        return LibraryOtherFileActionType.NOTHING;
    }
}
