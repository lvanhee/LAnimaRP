package logic.data.drawing;

public enum StretchingType {
	STRETCH, STRETCH_AND_BANDS, SCALE_NO_STRETCH_NO_BAND
	;

	public static StretchingType getDefaultValue() {
		return STRETCH;
	}

	public boolean isRequiringBands() {
		return this == StretchingType.STRETCH_AND_BANDS;
	}

}
