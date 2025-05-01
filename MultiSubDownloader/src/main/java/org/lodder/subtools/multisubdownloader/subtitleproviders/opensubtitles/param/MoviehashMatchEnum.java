package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.param;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import manifold.ext.props.rt.api.override;
import manifold.ext.props.rt.api.val;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MoviehashMatchEnum implements ParamIntf {
    INCLUDE("include"), ONLY("only");

    @val @override String value;

    @Override
    public String toString() {
        return value;
    }
}
