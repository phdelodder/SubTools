package org.lodder.subtools.multisubdownloader.lib.library;

public enum LibraryActionType {
    NOTHING("-- Maak uw keuze --"), RENAME("Hernoemen"), MOVE("Verplaatsen"), MOVEANDRENAME(
            "Verplaats en Hernoemen");

    private final String description;

    LibraryActionType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }

    public static LibraryActionType fromString(String description) {
        if (description != null) {
            for (LibraryActionType action : LibraryActionType.values()) {
                if (description.equalsIgnoreCase(action.toString())) {
                    return action;
                }
            }
        }
        return LibraryActionType.NOTHING;
    }
}
