package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.api.v2.param;

public enum OrderDirectionEnum implements ParamIntf {
	ACSENDING("asc"), DESCENDING("desc");

	private String value;

	OrderDirectionEnum(String value) {
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
