package input.configuration;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import main.DisplayWindow;

public class FullScreenFiller implements DisplayableItem {
	
	private final Color c;
	
	private FullScreenFiller(Color c) {
		this.c = c;
	}

	public static FullScreenFiller newInstance(Element e, LAnimaRPContext context)
	{
		Color c = XMLParser.parseColor(e);
		return new FullScreenFiller(c);		
	}

	@Override
	public void drawMe(Graphics2D g) {
		g.setPaint(c);
		g.fillRect(0, 0, DisplayWindow.getWindowWidth(), DisplayWindow.getWindowHeight());
	}

	@Override
	public void terminate() {
	}

}
