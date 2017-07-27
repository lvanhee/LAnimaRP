package draw.displayItems.shapes.bars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import draw.displayItems.DisplayableItem;
import logic.variables.variableTypes.BoundedIntegerVariable;

public class PassiveBar implements DisplayableItem{
	
	public enum FillDirection{
		HORIZONTAL, VERTICAL
	}
	
	
	private float ratio;
	private final Rectangle position;
	private final Color displayColor;
	private final FillDirection direction;
	
	private PassiveBar(float ratio, 
			Rectangle rectangle, 
			Color displayColor2,
			FillDirection d) {
		this.ratio = ratio;
		this.position =rectangle;
		this.displayColor = displayColor2;
		this.direction = d;
	}


	@Override
	public void drawMe(Graphics2D g) {
		g.setColor(displayColor);
		
		switch (direction) {
		case HORIZONTAL:
			g.fillRect(position.x, position.y, (int) (position.width*ratio), position.height);
			break;
		case VERTICAL:
			g.fillRect(position.x, position.y, position.width, (int)(position.getHeight()*ratio));
			break;

		default: throw new Error();
		}
		
		g.draw(position);
	}
	
	
	@Override
	public void terminate() {
	}


	public static PassiveBar newInstance(float ratio, Rectangle rectangle, Color displayColor2, FillDirection d) {
		return new PassiveBar(ratio, rectangle, displayColor2,d);
	}


	public void setRatio(float ratio2) {
		ratio = ratio2;
	}

}
