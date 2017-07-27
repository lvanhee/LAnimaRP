package draw.displayItems.shapes.bars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import draw.displayItems.shapes.bars.PassiveBar.FillDirection;
import input.configuration.XMLParser;
import input.events.eventTypes.VariableEvolvedEvent;
import input.events.listeners.GenericLAnimaRPEventListener;
import input.events.listeners.LAnimaRPEventListener;
import logic.variables.management.VariableManager;
import logic.variables.variableTypes.BoundedIntegerVariable;

public class BarDisplayer implements DisplayableItem {
	
	private final PassiveBar pb;
	private static final Color DEFAULT_COLOR = Color.white;
	
	private final LAnimaRPEventListener<VariableEvolvedEvent> listener;
			
	

	private BarDisplayer(BoundedIntegerVariable boundedVariable, Rectangle rectangle) {
		this(boundedVariable, rectangle, DEFAULT_COLOR);
	}

	private BarDisplayer(final BoundedIntegerVariable biv, Rectangle rectangle, Color displayColor) {
		pb = PassiveBar.newInstance((float)biv.getRatio(),rectangle,displayColor, FillDirection.VERTICAL);
		
		listener =GenericLAnimaRPEventListener.newInstance(biv,
				x->pb.setRatio((float)biv.getRatio())
				);
	}



	public static BarDisplayer generate(List<Element> list) {
		BoundedIntegerVariable biv = null;
		Rectangle r=null;
		Point position=null;
		Dimension dimensions = null;
		Color displayColor = BarDisplayer.DEFAULT_COLOR;
		for(Element e:list)
			switch(e.getName())
			{
			case "variable":biv = (BoundedIntegerVariable)VariableManager.get(e.getAttribute("name").getValue());break;
			case "position":position = 
					new Point(Integer.parseInt(e.getAttribute("x").getValue()),
					Integer.parseInt(e.getAttribute("y").getValue()));break;
			case "dimensions":dimensions = 
					new Dimension(Integer.parseInt(e.getAttribute("width").getValue()),
					Integer.parseInt(e.getAttribute("height").getValue()));break;
					
			case "color":displayColor = XMLParser.parseColor(e.getAttributeValue("value"));break;
					
			default: throw new Error();
			}

		return new BarDisplayer(biv, new Rectangle(position, dimensions),displayColor);
	}

	@Override
	public void terminate() {
	}

	@Override
	public void drawMe(Graphics2D g) {
		pb.drawMe(g);
	}
	


}
