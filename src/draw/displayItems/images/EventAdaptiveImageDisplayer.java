package draw.displayItems.images;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.jdom2.Element;
import draw.displayItems.DisplayableItem;
import draw.utils.DrawingUtils;
import input.configuration.XMLParser;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.StringEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.LAnimaRPEventPublisher;
import logic.data.PeriodicRefreshInfo;
import logic.data.drawing.StretchingType;
import logic.data.fileLocators.FileLocator;
import logic.data.fileLocators.FileManagerUtils;
import logic.data.string.EvolvingString;

public class EventAdaptiveImageDisplayer implements DisplayableItem,
LAnimaRPEventListener<LAnimaRPEvent>{
	
	private PassiveImagePrinter id;
	private final FileLocator fileToDisplay;
		
	private EventAdaptiveImageDisplayer(final FileLocator f, Rectangle displayZone, 
			StretchingType dt) {
		this.fileToDisplay = f;
		if(fileToDisplay instanceof LAnimaRPEventPublisher)
			((LAnimaRPEventPublisher)fileToDisplay).subscribe(this);
		id= PassiveImagePrinter.newInstance(
				new ImageIcon(), displayZone, dt);
		updateFile();
	}

	public static EventAdaptiveImageDisplayer newInstance(Element e) {

		Rectangle displayZone = XMLParser.parseRectangle(e);
		FileLocator f = XMLParser.parseFileLocator(e);
		PeriodicRefreshInfo refreshRate;
		StretchingType dt = XMLParser.parseStrechtingType(e);
		
		
		return new EventAdaptiveImageDisplayer(f,displayZone, dt);
	}

	@Override
	public void drawMe(Graphics2D g) {
		id.drawMe(g);
	}
	@Override
	public void terminate() {
		id.terminate();
	}
	
	public String toString()
	{
		return EventAdaptiveImageDisplayer.class.getTypeName()+":"+fileToDisplay;
	}

	@Override
	public void handleEvent(LAnimaRPEvent event) {
		updateFile();
	}

	private void updateFile() {
		
		id.setImage(DrawingUtils.loadImage(FileManagerUtils.getLocalFileFor(fileToDisplay.toString())));
	}
}