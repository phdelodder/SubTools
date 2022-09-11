package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ForeignPartsOnlyEnum implements ParamIntf {
    EXCLUDE("exclude"), INCLUDE("include"), ONLY("only");

    private final String value;

    @Override
    public String toString() {
        return getValue();
    }
}
