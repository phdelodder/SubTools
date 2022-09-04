package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param;

public enum MoviehashMatchEnum implements ParamIntf {
	INCLUDE("include"), ONLY("only");

	private String value;

	MoviehashMatchEnum(String value) {
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
