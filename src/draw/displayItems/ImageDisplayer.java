package draw.displayItems;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdom2.Element;

import draw.DrawingUtils;
import logic.XMLParser;

public class ImageDisplayer implements DisplayableItems{

	
	private BufferedImage displayedImage;
	final private Rectangle displayZone;
	final private TransitionAnimation transition;
	private ImageDisplayer(final File f, Rectangle displayZone, final int refreshRate, final TransitionAnimation anim) {
		this.displayZone = displayZone;
		this.displayedImage = new BufferedImage((int)displayZone.getWidth(), (int)displayZone.getHeight(), BufferedImage.TYPE_INT_ARGB);
		this.transition = anim;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true)
				{
					reloadImage();
					try {
						Thread.sleep(refreshRate);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			private void reloadImage() {
				try {
				    FileInputStream fis = new FileInputStream(f);
				    BufferedImage aux = ImageIO.read(fis);
				    if(!DrawingUtils.equals(aux,displayedImage))
				    	transition.restart();
					displayedImage = aux;
				} catch (IOException e) {
					e.printStackTrace();
					throw new Error();
				}
			}
		}).start();
	}

	public static DisplayableItems newInstance(Element e) {	

		Rectangle displayZone = XMLParser.parseRectangle(e);
		File f = XMLParser.parseFile(e);
		int refreshRate = XMLParser.parseRefreshRate(e);
		TransitionAnimation anim= XMLParser.getTransitionAnimation(e);
		return new ImageDisplayer(f,displayZone, refreshRate, anim);
	}

	@Override
	public void drawMe(Graphics2D g) {
		DrawingUtils.drawImage(g, displayedImage, displayZone);
		if(transition.isActive())
			transition.drawMe(g);
	}

}
