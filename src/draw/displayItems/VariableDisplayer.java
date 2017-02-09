package draw.displayItems;

import java.awt.Graphics2D;
import java.awt.Point;

import logic.variables.BoundedIntegerVariable;
import logic.variables.DisplayableVariable;

public class VariableDisplayer implements DisplayableItems {
	
	

	public static final String PERCENT = "Percent";

	public static final String NAKED = "Naked";
	
	DisplayableVariable variable;
	String displayMode;
	Point p;

	public VariableDisplayer(Point p, DisplayableVariable biv, String displayMode) {
		variable = biv;
		this.displayMode = displayMode;
		this.p=p;
	}

	@Override
	public void drawMe(Graphics2D g) {
		if(displayMode== PERCENT)
			g.drawString(variable.getDisplayValue()+"%", p.x, p.y);
		else if(displayMode==NAKED)
			g.drawString(variable.getDisplayValue(), p.x, p.y);
		else throw new Error();
		
	}

}
