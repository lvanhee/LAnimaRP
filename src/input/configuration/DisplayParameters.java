package input.configuration;

import java.awt.Dimension;
import java.awt.Rectangle;

public class DisplayParameters {

	private Rectangle screenSize;
	private boolean fullScreen;
	
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
		return fullScreen;
	}

	public int getPositionX() {
		return screenSize.x;
	}

	public int getPositionY() {
		return screenSize.y;
	}

	public void setFullScreen(boolean parseFullScreen) {
		fullScreen = parseFullScreen;
	}

	public Dimension getDimensions() {
		return new Dimension(screenSize.width, screenSize.height);
	}

}
