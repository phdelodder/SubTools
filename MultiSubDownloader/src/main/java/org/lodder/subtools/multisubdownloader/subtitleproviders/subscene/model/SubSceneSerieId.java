package org.lodder.subtools.multisubdownloader.subtitleproviders.subscene.model;

import org.lodder.subtools.sublibrary.data.ProviderSerieId;

import lombok.Getter;

@Getter
public class SubSceneSerieId extends ProviderSerieId {

    private static final long serialVersionUID = 5858875211782260667L;
    private final int season;

    public SubSceneSerieId(String name, String id, int season) {
        super(name, id);
        this.season = season;
    }

}
