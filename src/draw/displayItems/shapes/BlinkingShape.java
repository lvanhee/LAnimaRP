package draw.displayItems.shapes;
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
import java.util.Optional;

import javax.imageio.ImageIO;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.configuration.LAnimaRPContext;
import input.configuration.XMLKeywords;
import input.configuration.XMLParser;
import logic.variables.variableTypes.BooleanVariable;
import logic.variables.variableTypes.StringVariable;
import logic.variables.variableTypes.Variable;



public class BlinkingShape implements DisplayableItem {
	
	private final Shape shape;
	private final Optional<StringVariable> activated;
	private final Color blinkingColor;

	private BlinkingShape(Optional<StringVariable> v, Shape s, Color c) {
		if(v == null)
			throw new Error();
		activated = v;
		this.shape = s;
		blinkingColor = c;
	}

	public Shape getShape() {
		return shape;
	}
	
	

	
	@Override
	public void drawMe(Graphics2D g) {
		if(isInBlinkingMode())
		{
			g.setColor(new Color((int)blinkingColor.getRed(),(int)blinkingColor.getGreen(), (int)blinkingColor.getBlue(), 
					(int)((System.currentTimeMillis()%1000)*255)/1000));
			g.fill(shape);
		}
		else if(isInSolidMode())
		{
			g.setColor(new Color((int)blinkingColor.getRed(),(int)blinkingColor.getGreen(), (int)blinkingColor.getBlue(),
					(int)((System.currentTimeMillis()%400)*100)/400+155));
			g.fill(shape);
		}
	}

	private boolean isInSolidMode() {
		if(!activated.isPresent())return false;
		return activated.get().getString().equals(XMLKeywords.SOLID.getName());
	}

	private boolean isInBlinkingMode() {
		if(!activated.isPresent())return true;
		return(activated.get().getString().equals(XMLKeywords.BLINKING.getName()));
	}

	public static BlinkingShape newInstance(Element e, LAnimaRPContext context) {
		Optional<StringVariable> v = Optional.empty();
		
		if(e.getChild(XMLKeywords.VARIABLE.getName())!=null)
				v=Optional.of((StringVariable)context.getVariable(e.getChild(XMLKeywords.VARIABLE.getName()).getAttributeValue(XMLKeywords.NAME.getName())));

		
		if(v.isPresent() && (!( v.get() instanceof StringVariable)))throw new Error("The input variable should be a string");

		Shape s = XMLParser.processShape(e.getChild("points"));
		Color c = XMLParser.parseColor(e.getChild("color").getAttributeValue("value"));
		
		return new BlinkingShape(v,s,c);
	}

	@Override
	public void terminate() {
	};


}
