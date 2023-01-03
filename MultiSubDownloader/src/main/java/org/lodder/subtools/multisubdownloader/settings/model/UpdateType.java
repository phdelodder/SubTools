package org.lodder.subtools.multisubdownloader.settings.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UpdateType {
    STABLE("InputPanel.UpdateType.Stable"),
    NIGHTLY("InputPanel.UpdateType.Nightly");

    private final String msgCode;
}
