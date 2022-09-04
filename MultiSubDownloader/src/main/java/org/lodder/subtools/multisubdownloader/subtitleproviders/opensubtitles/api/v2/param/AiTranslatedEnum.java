package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param;

public enum AiTranslatedEnum implements ParamIntf {
	EXCLUDE("exclude"), INCLUDE("include");

	private String value;

	AiTranslatedEnum(String value) {
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
