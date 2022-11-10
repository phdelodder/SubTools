package org.lodder.subtools.sublibrary.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VideoSearchType {
    EPISODE("App.Episode"),
    MOVIE("App.Movie"),
    RELEASE("App.Release");

    private final String msgCode;
}
