package draw.displayItems;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jdom2.Attribute;
import org.jdom2.Element;

import draw.DrawingUtils;
import main.DisplayWindow;

public class BackgroundImage implements DisplayableItems {
	
	private BufferedImage background = null;
	
	public BackgroundImage(String fileName)
	{
		
			background = DrawingUtils.loadImage(fileName);
			
		
	}



	@Override
	public void drawMe(Graphics2D g) {
		AffineTransform at = g.getTransform();
		g.setTransform(new AffineTransform());
		/*AffineTransform at = new AffineTransform();
		at.scale((double)DisplayWindow.getWindowWidth()/(double)background.getWidth(), 
				(double)DisplayWindow.getWindowHeight()/(double)background.getHeight());
		g.drawImage(background, at, null);*/
		g.drawImage(background, 0, 0,DisplayWindow.getWindowWidth(), DisplayWindow.getWindowHeight(), null);
		g.setTransform(at);
	}



	public static DisplayableItems generate(String filename) {
		return new BackgroundImage(filename);
	}



	public BufferedImage getImage() {
		return background;
	}

}
