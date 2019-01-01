package input.configuration;

import java.awt.Color;

public class TextParameters {

	public static final TextParameters DEFAULT = new TextParameters(Color.GREEN);

	private Color c;
	private TextParameters(Color c) {
		this.c = c;
	}

	public static TextParameters newInstance(Color c) {
		return new TextParameters(c);
	}

	public Color getColor() {
		return c;
	}

	public void setColor(Color color) {
		this.c = color;
	}

}
