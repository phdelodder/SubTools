package org.lodder.subtools.sublibrary.model;

import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.val;

@AllArgsConstructor
public enum SubtitleSource {
    OPENSUBTITLES("OpenSubtitles"),
    PODNAPISI("Podnapisi"),
    ADDIC7ED("Addic7ed"),
    TVSUBTITLES("TvSubtitles"),
    LOCAL("Local"),
    SUBSCENE("Subscene");

    @val String name;
}
