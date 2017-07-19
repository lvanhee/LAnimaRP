package logic.data;

public class Range {
	
	private int min;
	private int max;

	private Range(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public static Range newInstance(String attributeValue, String attributeValue2) {
		return new Range(Integer.parseInt(attributeValue),Integer.parseInt(attributeValue2));
	}

	public int getMinValue() {
		return min;
	}

	public int getMaxValue() {
		return max;
	}

}
