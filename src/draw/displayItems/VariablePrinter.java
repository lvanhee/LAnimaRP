package draw.displayItems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.jdom2.Element;

import logic.XMLParser;
import logic.variables.DisplayableVariable;

public class VariablePrinter implements DisplayableItems {
	
	Point position;
	DisplayableVariable v;
	String preText;

	public VariablePrinter(Point p, String preText, DisplayableVariable v2) {
		this.position=p;
		this.v=v2;
		this.preText=preText;
	}

	public static VariablePrinter generate(Element e) {
		Point p = XMLParser.parsePosition(e);
		DisplayableVariable v =(DisplayableVariable) XMLParser.parseVariable(e);
		String preText = "";
		if(XMLParser.hasPreText(e))
			preText = XMLParser.getPreText(e);
		return new VariablePrinter(p,preText,v);
	}

	@Override
	public void drawMe(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.drawString(preText+v.getDisplayValue(),position.x,position.y);
	}

}
