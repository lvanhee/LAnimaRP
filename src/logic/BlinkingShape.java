package logic;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jdom2.Element;

import draw.displayItems.DisplayableItems;
import input.BooleanVariable;
import input.VariableManager;



public class BlinkingShape implements DisplayableItems {
	
	Shape shape;
	BooleanVariable activated;
	private Color blinkingColor;

	public BlinkingShape(BooleanVariable callingVariable, Shape s, Color c) {
		activated = callingVariable;
		this.shape = s;
		blinkingColor = c;
	}

	public Shape getShape() {
		return shape;
	}
	
	

	
	@Override
	public void drawMe(Graphics2D g) {
		if(!activated.isActive())return;
		g.setColor(new Color((int)blinkingColor.getRed(),(int)blinkingColor.getGreen(), (int)blinkingColor.getBlue(), 
				(int)((System.currentTimeMillis()%1000)*255)/1000));
		
		
		g.fill(shape);
	}

	public static DisplayableItems generate(Element e) {
		
		BooleanVariable callingVariable = (BooleanVariable) VariableManager.get(e.getChild("variable").getAttributeValue("name"));
		Shape s = XMLParser.processShape(e.getChild("points"));
		Color c = XMLParser.parseColor(e.getChild("color").getAttributeValue("value"));
		return new BlinkingShape(callingVariable,s,c);
	};


}
