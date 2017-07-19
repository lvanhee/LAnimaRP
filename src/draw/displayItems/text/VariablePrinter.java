package draw.displayItems.text;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.configuration.XMLParser;
import logic.variables.variableTypes.DisplayableVariable;

public class VariablePrinter implements DisplayableItem {
	
	final private Point position;
	final private DisplayableVariable v;
	final private String preText;

	private VariablePrinter(Point p, String preText, DisplayableVariable v2) {
		this.position=p;
		this.v=v2;
		this.preText=preText;
	}

	public static VariablePrinter newInstance(Element e) {
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

	@Override
	public void terminate() {
	}

}
