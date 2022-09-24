package org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TVsubtitlesSubtitleDescriptor implements Serializable {

    private static final long serialVersionUID = 6423513286301479905L;
    private String filename;
    private String url;
    private String rip;
    private String author;

}
