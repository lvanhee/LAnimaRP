package input.configuration;

import java.awt.Rectangle;

public class DisplayParameters {

	private Rectangle screenSize;
	
	public void setScreenSize(Rectangle parseRectangle) {
		screenSize = parseRectangle;
	}

	public static DisplayParameters newInstance() {
		return new DisplayParameters();
	}

	public int getScreenWidth() {
		return screenSize.width;
	}

	public int getScreenHeight() {
		return screenSize.height;
	}

	public boolean isFullScreen() {
		return screenSize==null;
	}

	public int getPositionX() {
		return screenSize.x;
	}

	public int getPositionY() {
		return screenSize.y;
	}

}
