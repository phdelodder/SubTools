package org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TVsubtitlesSubtitleDescriptor implements Serializable {

    private static final long serialVersionUID = 6423513286301479905L;
    private final String filename;
    private final String url;
    private final String rip;
    private final String author;

}
