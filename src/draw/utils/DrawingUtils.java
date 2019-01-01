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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import logic.data.drawing.StretchingType;
import logic.data.fileLocators.FileManagerUtils;

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

	public static ImageIcon loadImage(String fileName) {
		File file = FileManagerUtils.getLocalFileFor(fileName);  
		return loadImage(file);
	}

	public static ImageIcon loadImage(File f) {
		FileInputStream fis=null;
		Image res;
		try {
			fis = new FileInputStream(f);
			res = new ImageIcon(f.toURL()).getImage();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String message = sw.toString();
			JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
					JOptionPane.ERROR_MESSAGE);
			throw new Error(message);
		}
		return new ImageIcon(res);
	}

}
