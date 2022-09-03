package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param;

public enum ForeignPartsOnlyEnum implements ParamIntf {
    EXCLUDE("exclude"), INCLUDE("include"), ONLY("only");

    private String value;

    ForeignPartsOnlyEnum(String value) {
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
