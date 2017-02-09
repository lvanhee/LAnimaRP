package draw.displayItems;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;

import input.VariableManager;
import logic.XMLParser;
import logic.variables.BoundedIntegerVariable;
import main.DisplayWindow;

public class BarDisplayer implements DisplayableItems {

	private static final Color DEFAULT_COLOR = Color.white;
	BoundedIntegerVariable bv;
	Rectangle position;
	private Color displayColor= DEFAULT_COLOR;
	public BarDisplayer(BoundedIntegerVariable boundedVariable, Rectangle rectangle) {
		position = rectangle;
		bv = boundedVariable;
	}

	public BarDisplayer(BoundedIntegerVariable biv, Rectangle rectangle, Color displayColor) {
		this(biv,rectangle);
		this.displayColor=displayColor;
	}

	@Override
	public void drawMe(Graphics2D g) {
		g.setColor(displayColor);
		g.fillRect(position.x, position.y, position.width, (int)(position.getHeight()*bv.getRatio()));
		g.draw(position);
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
	


}
