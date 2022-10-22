package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrderDirectionEnum implements ParamIntf {
    ACSENDING("asc"), DESCENDING("desc");

    private final String value;

    @Override
    public String toString() {
        return getValue();
    }
}
