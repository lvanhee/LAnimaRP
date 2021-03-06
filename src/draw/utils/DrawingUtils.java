package draw.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import logic.data.drawing.StretchingType;
import logic.data.fileLocators.URLManagerUtils;

public class DrawingUtils {

	public static void drawImage(Graphics2D g, BufferedImage displayedImage, Rectangle displayZone) {
		g.drawImage(displayedImage, 
				(int)displayZone.getX(), 
				(int)displayZone.getY(), 
				(int)displayZone.getWidth(), 
				(int)displayZone.getHeight(),
				null);
	}

	public static boolean equals(BufferedImage img1, BufferedImage img2) {
		if (img1.getWidth() == img2.getWidth(null) && img1.getHeight() == img2.getHeight(null)) {
			for (int x = 0; x < img1.getWidth(); x++) {
				for (int y = 0; y < img1.getHeight(); y++) {
					if (img1.getRGB(x, y) != img2.getRGB(x, y))
						return false;
				}
			}
		} else {
			return false;
		}
		return true;

	}

	public static ImageIcon loadImage(URL url) {		
		Image res;

		res = new ImageIcon(url).getImage();

		return new ImageIcon(res);
	}

}
