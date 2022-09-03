package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param;

public enum TypeEnum implements ParamIntf {
	MOVIE("movie"), EPISODE("episode"), ALL("all");

	private String value;

	TypeEnum(String value) {
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
