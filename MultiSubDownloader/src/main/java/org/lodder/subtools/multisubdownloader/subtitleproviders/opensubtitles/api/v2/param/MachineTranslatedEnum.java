package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param;

public enum MachineTranslatedEnum implements ParamIntf {
    EXCLUDE("exclude"), INCLUDE("include");

    private final String value;

    MachineTranslatedEnum(String value) {
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
