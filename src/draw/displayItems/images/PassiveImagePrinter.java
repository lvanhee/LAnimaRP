package draw.displayItems.images;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.net.URL;

import javax.sql.rowset.CachedRowSet;
import javax.swing.ImageIcon;

import com.ibm.media.bean.multiplayer.DTWinAdapter;

import draw.displayItems.DisplayableItem;
import draw.utils.DrawingUtils;
import logic.data.drawing.StretchingType;
import logic.data.fileLocators.URLLocator;
import main.DisplayWindow;

public class PassiveImagePrinter implements DisplayableItem {
	
	private ImageIcon image;
	private Rectangle displayZone;
	private StretchingType st;
	private final GenericParameters gp;
	private PassiveImagePrinter(ImageIcon image, Rectangle displayZone, StretchingType dt, GenericParameters gp) {
		this.image = image;
		this.displayZone = displayZone;
		this.st = dt;
		this.gp = gp;
	}

	public static PassiveImagePrinter newInstance(ImageIcon image, Rectangle displayZone, StretchingType stretch, GenericParameters gp)
	{
		return new PassiveImagePrinter(image, displayZone,stretch, gp);
	}

	@Override
	public void drawMe(Graphics2D g) {
		if(!gp.isDisplayed())return;
		AffineTransform temp = g.getTransform();
		AffineTransform at = new AffineTransform();
		
		if(st.isRequiringBands())
		{
			g.setPaint(Color.BLACK);
			g.fill(displayZone);
		}
		
		
		switch(st)
		{
		case STRETCH:
			at.translate(displayZone.getX(), displayZone.getY());
			at.scale(
				((double)displayZone.getWidth())/(double)image.getIconWidth(), 
				displayZone.getHeight()/image.getIconHeight());
			break;
		
		case SCALE_NO_STRETCH_NO_BAND:
		case STRETCH_AND_BANDS: 
			double minScale = displayZone.getWidth()/image.getIconWidth();
			if(minScale > displayZone.getHeight()/image.getIconHeight())
				minScale = displayZone.getHeight()/image.getIconHeight();
			
			double totalBlackX = displayZone.getWidth()-image.getIconWidth()*minScale;
			double totalBlackY = displayZone.getHeight()-image.getIconHeight()*minScale;
						
			at.translate(displayZone.getX()+totalBlackX/2, displayZone.getY()+totalBlackY/2);
			
			at.scale(minScale, minScale); break;
			
		default : throw new Error();
		}
		

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

	public static PassiveImagePrinter newInstance(String string, Rectangle bounds, StretchingType stretch, GenericParameters gp) {
		return newInstance(DrawingUtils.loadImage(string), bounds, stretch, gp);
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName()+":"+displayZone;
	}

	public static DisplayableItem newInstance(URLLocator x, Rectangle displayZone2, StretchingType st,
			GenericParameters parameters) {
		return newInstance(DrawingUtils.loadImage(x.getURL()), displayZone2, st, parameters);
	}

}
