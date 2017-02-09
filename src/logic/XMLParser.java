package logic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import org.jdom2.Element;

import input.BooleanVariable;
import input.Variable;
import input.VariableManager;
import logic.variables.BoundedIntegerVariable;

public class XMLParser {

	private static final String PRE_TEXT_NAME = "pre_text";
	private static final String VALUE_NAME = "value";

	public static Shape processShape(Element child) {
		GeneralPath gp = null;
		for(Element e: child.getChildren())
		{
			if(gp==null)
				{gp = new GeneralPath();
				gp.moveTo(Double.parseDouble(e.getAttributeValue("x")), Double.parseDouble(e.getAttributeValue("y")));
				}
			else
				gp.lineTo(Double.parseDouble(e.getAttributeValue("x")), Double.parseDouble(e.getAttributeValue("y")));
		}
		gp.closePath();
		return gp;
	}

	public static Color parseColor(String s) {
		return new Color(
				Integer.valueOf( s.substring( 1, 3 ), 16 ),
				Integer.valueOf( s.substring( 3, 5 ), 16 ),
				Integer.valueOf( s.substring( 5, 7 ), 16 ) );
	}

	public static Rectangle parseRectangle(Element e) {
		Point position = parsePosition(e); 
		Dimension dimensions = new Dimension(
				Integer.parseInt(e.getChild("dimensions").getAttributeValue("width")),
				Integer.parseInt(e.getChild("dimensions").getAttributeValue("height")));
		return new Rectangle(position,dimensions);
	}

	public static BoundedIntegerVariable getParseBIV(Element e) {
		return (BoundedIntegerVariable)parseVariable(e);
	}
	
	public static Variable parseVariable(Element e) {
		return VariableManager.get(e.getChild("variable").getAttributeValue("name"));
	}

	public static Color parseColor(Element e) {
		return parseColor(e.getChild("color").getAttributeValue("value"));
	}

	public static Point parsePosition(Element e) {
		return new Point(Integer.parseInt(e.getChild("position").getAttributeValue("x")), 
		Integer.parseInt(e.getChild("position").getAttributeValue("y")));
	}

	public static BooleanVariable getParseBooleanVariable(Element e,String function) {
		for(Element e2:e.getChildren())
		{
			if(e2.getName().equals("variable")&&e2.getAttributeValue("function").equals(function))
				return (BooleanVariable) VariableManager.get(e2.getAttributeValue("name"));
		}
		throw new Error("Variable has not been created.");
	}

	public static boolean hasPreText(Element e) {
		for(Element e2:e.getChildren())
			if(e2.getName().equals(PRE_TEXT_NAME))
				return true;
		return false;
	}

	public static String getPreText(Element e) {
		return e.getChild(PRE_TEXT_NAME).getAttributeValue(VALUE_NAME);
	}

}
