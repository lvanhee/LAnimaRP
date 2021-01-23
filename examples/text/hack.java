package draw.displayItems;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.xml.crypto.Data;

import org.jdom2.Element;
import org.w3c.dom.css.Rect;

import logic.XMLParser;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;


public class TextPrinter implements DisplayableItems {

	String hackingText="";	
	Rectangle drawingRectangle;
	BufferedImage bi ;

	private TextPrinter(Rectangle r){
		drawingRectangle = r;
		bi = new BufferedImage((int)r.getWidth(), (int)r.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		readText();

		Graphics2D g2d = (Graphics2D) bi.getGraphics();
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				throw new Error();
			}
		}).start();
	}
	
	private void readText() {
		try {
		  BufferedReader br = new BufferedReader(new FileReader("input/text/hackingTextFile.txt"));
		    
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        hackingText = sb.toString();
		        br.close();
		    } catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void drawMe(Graphics2D g) {
		g.getFontMetrics();
		throw new Error();
	}

	public static DisplayableItems newInstance(Element e) {
		Rectangle r = XMLParser.parseRectangle(e);
		
		return new TextPrinter(r);
	}
}

package draw.displayItems;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.xml.crypto.Data;

import org.jdom2.Element;
import org.w3c.dom.css.Rect;

import logic.XMLParser;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;


public class TextPrinter implements DisplayableItems {

	String hackingText="";	
	Rectangle drawingRectangle;
	BufferedImage bi ;

	private TextPrinter(Rectangle r){
		drawingRectangle = r;
		bi = new BufferedImage((int)r.getWidth(), (int)r.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		readText();

		Graphics2D g2d = (Graphics2D) bi.getGraphics();
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				throw new Error();
			}
		}).start();
	}
	
	private void readText() {
		try {
		  BufferedReader br = new BufferedReader(new FileReader("input/text/hackingTextFile.txt"));
		    
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        hackingText = sb.toString();
		        br.close();
		    } catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void drawMe(Graphics2D g) {
		g.getFontMetrics();
		throw new Error();
	}

	public static DisplayableItems newInstance(Element e) {
		Rectangle r = XMLParser.parseRectangle(e);
		
		return new TextPrinter(r);
	}
}