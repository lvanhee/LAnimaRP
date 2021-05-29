package draw.displayItems.controlflow;

import java.awt.Graphics2D;
import java.util.function.Supplier;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.configuration.LAnimaRPContext;
import input.configuration.XMLKeywords;
import input.configuration.XMLParser;
import logic.variables.variableTypes.BooleanVariable;

public class TemporarilyDisplayedAnimation implements DisplayableItem {
	
	private DisplayableItem s;

	private TemporarilyDisplayedAnimation(DisplayableItem s, long duration) {
		this.s = s;
		new Thread(()->
		{
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			endAnimation();
		}).start();
	}

	private synchronized void endAnimation() {
		s.terminate();
		s=null;
	}

	@Override
	public synchronized void drawMe(Graphics2D g) {
		if(s!=null)
			s.drawMe(g);
	}

	@Override
	public synchronized void terminate() {
		if(s!= null) s.terminate();
	}
	
	public static TemporarilyDisplayedAnimation newInstance(Element e, LAnimaRPContext context) {
		DisplayableItem s = XMLParser.parseDisplayableItem(e, context);
		if(e.getAttributeValue(XMLKeywords.DURATION.getName())==null) 
			throw new Error("Missing attribute value for temporarily displayed animation");
		long duration = Long.parseLong(e.getAttributeValue(XMLKeywords.DURATION.getName()));
		return new TemporarilyDisplayedAnimation(s, duration);
	}

}
