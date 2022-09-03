package org.lodder.subtools.multisubdownloader.settings.model;

public class SettingsExcludeItem {

    private SettingsExcludeType type;
    private String description;

    public SettingsExcludeItem(String description, SettingsExcludeType type) {
        this.type = type;
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setType(SettingsExcludeType type) {
        this.type = type;
    }

    public SettingsExcludeType getType() {
        return type;
    }

}
