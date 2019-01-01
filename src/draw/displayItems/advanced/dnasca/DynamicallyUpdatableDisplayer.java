package draw.displayItems.advanced.dnasca;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.configuration.LAnimaRPContext;
import input.configuration.ProcessXML;
import input.configuration.XMLParser;
import input.events.eventTypes.StringEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.FileBasedEvolvingString;
import logic.data.string.EvolvingString;

public class DynamicallyUpdatableDisplayer implements DisplayableItem, LAnimaRPEventListener<StringEvolvedEvent> {
	
	private final EvolvingString toDisplay;
	
	private DisplayableItem itemsToDisplay;
	
	private final LAnimaRPContext context;
		
	private DynamicallyUpdatableDisplayer(EvolvingString es, LAnimaRPContext context) {
		this.context = context;
		toDisplay = es;
		es.subscribe(this);
		itemsToDisplay =ProcessXML.getDisplayableItemFromString(toDisplay.getString(), context);
	}

	@Override
	public void drawMe(Graphics2D g) {
		itemsToDisplay.drawMe(g);
	}

	public static DisplayableItem newInstance(Element e, LAnimaRPContext context) {
		EvolvingString es=null;
		if(XMLParser.hasFileLocation(e))
			es = FileBasedEvolvingString.newInstance(e, context);
		
		if(es==null)
			throw new Error("Must instantiate the text input for "+DynamicallyUpdatableDisplayer.class);
		
		return new DynamicallyUpdatableDisplayer(es, context);
			
	}

	@Override
	public synchronized void handleEvent(StringEvolvedEvent event) {
		itemsToDisplay.terminate();
		itemsToDisplay= ProcessXML.getDisplayableItemFromString(event.toString(), context);
	}

	@Override
	public synchronized void terminate() {
		toDisplay.unsubscribe(this);
		itemsToDisplay.terminate();
	}

}
