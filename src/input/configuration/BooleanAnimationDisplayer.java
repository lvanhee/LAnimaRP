package input.configuration;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Optional;
import java.util.function.Supplier;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import draw.displayItems.images.ImageDisplayer;
import draw.displayItems.text.textprinter.PassiveAppendTextAreaDrawer;
import logic.data.fileLocators.FileLocator;
import logic.variables.variableTypes.BooleanVariable;

public class BooleanAnimationDisplayer implements DisplayableItem {
	
	private final BooleanVariable v;
	
	private final Supplier<DisplayableItem> displayItemGenerator;
	private Optional<DisplayableItem> toDisplay;
	
	private boolean wasVisibleBefore = false;
	public BooleanAnimationDisplayer(BooleanVariable v, Supplier<DisplayableItem> toDisplay) {
		this.v = v;
		this.displayItemGenerator = toDisplay;
	}

	public static BooleanAnimationDisplayer newInstance(Element e, LAnimaRPContext context) {
		BooleanVariable v = (BooleanVariable)XMLParser.parseVariable(e, context);
		Supplier<DisplayableItem> s = ()->XMLParser.parseDisplayableItem(e, context);
		return new BooleanAnimationDisplayer(v,s);
	}
	
	

	@Override
	public void drawMe(Graphics2D g) {
		if(v.getValue())
			{
			if(!wasVisibleBefore) toDisplay = Optional.of(displayItemGenerator.get());
			
			toDisplay.get().drawMe(g);
			}
		
		if(!v.getValue() && wasVisibleBefore)
		{
			toDisplay.get().terminate();
			toDisplay = Optional.empty();
		}
		wasVisibleBefore = v.getValue();
	}

	@Override
	public void terminate() {
		toDisplay.get().terminate();
		toDisplay=Optional.empty();
	}
}
