package org.lodder.subtools.sublibrary.model;

import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.val;

@AllArgsConstructor
public enum VideoSearchType {
    EPISODE("App.Episode"),
    MOVIE("App.Movie"),
    RELEASE("App.Release");

    @val String msgCode;
}
