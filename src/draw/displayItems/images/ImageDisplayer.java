package draw.displayItems.images;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;

import org.w3c.dom.css.Rect;

import draw.displayItems.DisplayableItem;
import main.DisplayWindow;

public class ImageDisplayer implements DisplayableItem {
	
	private ImageIcon image;
	private final Rectangle displayZone;
	private ImageDisplayer(ImageIcon image, Rectangle displayZone) {
		this.image = image;
		this.displayZone = displayZone;
	}

	public static ImageDisplayer newInstance(ImageIcon image, Rectangle displayZone)
	{
		return new ImageDisplayer(image, displayZone);
	}

	@Override
	public void drawMe(Graphics2D g) {
		AffineTransform temp = g.getTransform();
		AffineTransform at = new AffineTransform();
		at.translate(displayZone.getX(), displayZone.getY());
		at.scale(
				((double)displayZone.getWidth())/(double)image.getIconWidth(), 
				displayZone.getHeight()/image.getIconHeight());

		AffineTransform at2 = g.getTransform();
		at2.concatenate(at);

		g.setTransform(at2);
		image.paintIcon(DisplayWindow.getFrame(), g, 0, 0);
		g.setTransform(temp);
		
	}

	public void setImage(ImageIcon loadImage) {
		image = loadImage;
	}

	@Override
	public void terminate() {
	}

}
