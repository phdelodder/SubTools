package org.lodder.subtools.sublibrary.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SubtitleSource {
    OPENSUBTITLES("OpenSubtitles"),
    PODNAPISI("Podnapisi"),
    ADDIC7ED("Addic7ed"),
    TVSUBTITLES("TvSubtitles"),
    LOCAL("Local"),
    SUBSCENE("Subscene");

    private final String name;
}
