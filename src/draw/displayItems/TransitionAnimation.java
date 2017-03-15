package draw.displayItems;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import draw.DrawingUtils;

public class TransitionAnimation implements DisplayableItems{
	
	private long displayUntil;
	private final long duration; 
	//private final File imageFile;
	private final Rectangle localization;
	private final boolean fadeAway;
	
	BufferedImage bi;

	private TransitionAnimation(File f, Rectangle r, int duration2, boolean fadeAway2) {
		localization = r;
		duration = duration2;
		fadeAway = fadeAway2;
		
		try {
			bi = ImageIO.read(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void drawMe(Graphics2D g) {
		DrawingUtils.drawImage(g,bi,localization);
	}

	public void restart() {
		displayUntil = System.currentTimeMillis()+duration;
	}

	public static TransitionAnimation newInstance(File f, Rectangle r, int duration2, boolean fadeAway) {
		return new TransitionAnimation(f,r,duration2,fadeAway);
	}

	public boolean isActive() {
		return displayUntil>System.currentTimeMillis();
	}

}
