package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.model;

import java.io.Serial;

import manifold.ext.props.rt.api.val;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;

public class OpensubtitleSerieId extends ProviderSerieId {

    @Serial private static final long serialVersionUID = 5858875211782260667L;
    @val String year;

    public OpensubtitleSerieId(String name, int id, String year) {
        super(name, String.valueOf(id));
        this.year = year;
    }

}
