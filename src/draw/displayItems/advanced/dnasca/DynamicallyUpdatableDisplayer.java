package draw.displayItems.advanced.dnasca;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.configuration.ProcessXML;
import input.configuration.XMLParser;
import input.events.eventTypes.StringEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.FileBasedEvolvingString;
import logic.data.string.EvolvingString;

public class GenericDisplayer implements DisplayableItem, LAnimaRPEventListener<StringEvolvedEvent> {
	
	private final EvolvingString toDisplay;
	
	private CopyOnWriteArrayList<DisplayableItem> itemsToDisplay;
		
	private GenericDisplayer(EvolvingString es) {
		toDisplay = es;
		es.subscribe(this);
		itemsToDisplay = new CopyOnWriteArrayList<>();
				itemsToDisplay.addAll(ProcessXML.getDisplayableItemFromString(toDisplay.getString()));
	}

	@Override
	public void drawMe(Graphics2D g) {
		itemsToDisplay.forEach(
				x->
				x.drawMe(g));
	}

	public static DisplayableItem newInstance(Element e) {
		EvolvingString es=null;
		if(XMLParser.hasFileLocation(e))
			es = FileBasedEvolvingString.newInstance(e);
		
		if(es==null)
			throw new Error("Must instantiate the text input for "+GenericDisplayer.class);
		
		return new GenericDisplayer(es);
			
	}

	@Override
	public synchronized void handleEvent(StringEvolvedEvent event) {
		itemsToDisplay.stream().forEach(x->x.terminate());
		itemsToDisplay.clear();
		Collection<DisplayableItem>temp = ProcessXML.getDisplayableItemFromString(event.toString());
		itemsToDisplay.addAll(temp);
	}

	@Override
	public synchronized void terminate() {
		toDisplay.unsubscribe(this);
		itemsToDisplay.stream().forEach(x->x.terminate());
	}

}
