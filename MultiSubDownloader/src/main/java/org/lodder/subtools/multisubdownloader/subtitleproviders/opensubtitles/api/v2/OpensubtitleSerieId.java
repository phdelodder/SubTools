package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2;

import org.lodder.subtools.sublibrary.data.ProviderSerieId;

import lombok.Getter;

@Getter
public class OpensubtitleSerieId extends ProviderSerieId {

    private static final long serialVersionUID = 5858875211782260667L;
    private final String year;

    public OpensubtitleSerieId(String name, int id, String year) {
        super(name, String.valueOf(id));
        this.year = year;
    }

}
