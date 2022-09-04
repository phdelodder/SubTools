package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param;

public enum TrustedSourcesEnum implements ParamIntf {
    INCLUDE("include"), ONLY("only");

    private final String value;

    TrustedSourcesEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
