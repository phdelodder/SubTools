package org.lodder.subtools.multisubdownloader.settings.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SettingsExcludeItem {

    private String description;
    private SettingsExcludeType type;

}
