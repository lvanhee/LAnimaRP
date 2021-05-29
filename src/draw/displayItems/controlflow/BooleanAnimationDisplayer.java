package draw.displayItems.controlflow;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Optional;
import java.util.function.Supplier;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import draw.displayItems.images.ImageDisplayer;
import draw.displayItems.text.textprinter.PassiveAppendTextAreaDrawer;
import input.configuration.LAnimaRPContext;
import input.configuration.XMLKeywords;
import input.configuration.XMLParser;
import input.events.publishers.FileBasedEvolvingString;
import logic.data.fileLocators.URLLocator;
import logic.data.string.EvolvingString;
import logic.data.string.TextSource;
import logic.variables.variableTypes.BooleanVariable;
import logic.variables.variableTypes.TextSourceBasedBooleanVariable;

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
		BooleanVariable bv = null;
		if(e.getAttribute(XMLKeywords.VARIABLE.getName())!=null)
			bv = (BooleanVariable)XMLParser.parseVariable(e, context);
		else if(e.getChild(XMLKeywords.TEXT_SOURCE.getName())!=null)
		{
			String defValStr = e.getChild(XMLKeywords.TEXT_SOURCE.getName())
					.getAttribute(XMLKeywords.DEFAULT.getName()).getName();
			boolean def = Boolean.parseBoolean(defValStr);
			bv = TextSourceBasedBooleanVariable.newInstance(
					XMLParser.parseTextSource(e, context), def);
		}
		Supplier<DisplayableItem> s = ()->XMLParser.parseDisplayableItem(e, context);
		return new BooleanAnimationDisplayer(bv,s);
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
