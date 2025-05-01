package org.lodder.subtools.multisubdownloader.settings.model;

import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.val;

@AllArgsConstructor
public enum UpdateType {
    STABLE("InputPanel.UpdateType.Stable"),
    NIGHTLY("InputPanel.UpdateType.Nightly");

    @val String msgCode;
}
