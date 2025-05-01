package org.lodder.subtools.multisubdownloader.subtitleproviders.tvsubtitles.model;

import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;
import manifold.ext.props.rt.api.val;
import org.lodder.subtools.sublibrary.control.VideoPatterns.Source;

@Builder
public class TVSubtitlesSubtitleDescriptor implements Serializable {

    @Serial
    private static final long serialVersionUID = 6423513286301479905L;
    @val String title;
    @val String filename;
    @val String url;
    @val Source source;
    @val String releaseGroup;

}
